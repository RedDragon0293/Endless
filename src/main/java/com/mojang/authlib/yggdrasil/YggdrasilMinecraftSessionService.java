package com.mojang.authlib.yggdrasil;

import cn.asone.endless.utils.ClientUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.HttpMinecraftSessionService;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.request.JoinMinecraftServerRequest;
import com.mojang.authlib.yggdrasil.response.HasJoinedMinecraftServerResponse;
import com.mojang.authlib.yggdrasil.response.MinecraftProfilePropertiesResponse;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.authlib.yggdrasil.response.Response;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.gui.GuiMultiplayer;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.*;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class YggdrasilMinecraftSessionService extends HttpMinecraftSessionService {
    private static final String[] WHITELISTED_DOMAINS = {
            ".minecraft.net",
            ".mojang.com",
            "163.com",
            "authserver.163.com"
    };
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String BASE_URL = "https://sessionserver.mojang.com/session/minecraft/";
    private static final URL JOIN_URL = HttpAuthenticationService.constantURL(BASE_URL + "join");
    private static final URL CHECK_URL = HttpAuthenticationService.constantURL(BASE_URL + "hasJoined");

    private final PublicKey publicKeyMojang;
    private final PublicKey publicKeyNetease;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
    private final LoadingCache<GameProfile, GameProfile> insecureProfiles = CacheBuilder
            .newBuilder()
            .expireAfterWrite(6, TimeUnit.HOURS)
            .build(new CacheLoader<GameProfile, GameProfile>() {
                @Override
                public GameProfile load(GameProfile key) {
                    return fillGameProfile(key, false);
                }
            });

    protected YggdrasilMinecraftSessionService(YggdrasilAuthenticationService authenticationService) {
        super(authenticationService);

        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(IOUtils.toByteArray(Objects.requireNonNull(YggdrasilMinecraftSessionService.class.getResourceAsStream("/yggdrasil_session_pubkey.der"))));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKeyMojang = keyFactory.generatePublic(spec);
            spec = new X509EncodedKeySpec(IOUtils.toByteArray(Objects.requireNonNull(YggdrasilMinecraftSessionService.class.getResourceAsStream("/netease.der"))));
            publicKeyNetease = keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new Error("Missing/invalid yggdrasil/netease public key!");
        }
    }

    @Override
    public void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException {
        if (GuiMultiplayer.authType) {
            ClientUtils.logger.info("ServerID: " + serverId);
            try {
                Socket sock = new Socket("127.0.0.1", 55996);
                sock.getOutputStream().write((serverId + "\000").getBytes());
                sock.getOutputStream().flush();
                sock.getInputStream().read();
                sock.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JoinMinecraftServerRequest request = new JoinMinecraftServerRequest();
                request.accessToken = authenticationToken;
                request.selectedProfile = profile.getId();
                request.serverId = serverId;

                getAuthenticationService().makeRequest(JOIN_URL, request, Response.class);
            } catch (Exception ignored) {
            }
        } else {
            JoinMinecraftServerRequest request = new JoinMinecraftServerRequest();
            request.accessToken = authenticationToken;
            request.selectedProfile = profile.getId();
            request.serverId = serverId;

            getAuthenticationService().makeRequest(JOIN_URL, request, Response.class);
        }
    }

    @Override
    public GameProfile hasJoinedServer(GameProfile user, String serverId, InetAddress address) throws AuthenticationUnavailableException {
        Map<String, Object> arguments = new HashMap<String, Object>();

        arguments.put("username", user.getName());
        arguments.put("serverId", serverId);
        if (address != null) {
            arguments.put("ip", address.getHostAddress());
        }
        URL url = HttpAuthenticationService.concatenateURL(CHECK_URL, HttpAuthenticationService.buildQuery(arguments));

        try {
            HasJoinedMinecraftServerResponse response = getAuthenticationService().makeRequest(url, null, HasJoinedMinecraftServerResponse.class);

            if (response != null && response.getId() != null) {
                GameProfile result = new GameProfile(response.getId(), user.getName());

                if (response.getProperties() != null) {
                    result.getProperties().putAll(response.getProperties());
                }

                return result;
            } else {
                return null;
            }
        } catch (AuthenticationUnavailableException e) {
            throw e;
        } catch (AuthenticationException e) {
            return null;
        }
    }

    @Override
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure) {
        Property textureProperty = Iterables.getFirst(profile.getProperties().get("textures"), null);

        if (textureProperty == null) {
            return new HashMap<>();
        }

        if (requireSecure) {
            if (!textureProperty.hasSignature()) {
                LOGGER.error("Signature is missing from textures payload");
                throw new InsecureTextureException("Signature is missing from textures payload");
            }

            if (GuiMultiplayer.authType) {
                if (!textureProperty.isSignatureValid(publicKeyNetease)) {
                    LOGGER.error("Textures payload has been tampered with (signature invalid)");
                    throw new InsecureTextureException("Textures payload has been tampered with (signature invalid)");
                }
            } else {
                if (!textureProperty.isSignatureValid(publicKeyMojang)) {
                    LOGGER.error("Textures payload has been tampered with (signature invalid)");
                    throw new InsecureTextureException("Textures payload has been tampered with (signature invalid)");
                }
            }
        }

        MinecraftTexturesPayload result;
        try {
            String json = new String(Base64.decodeBase64(textureProperty.getValue()), Charsets.UTF_8);
            result = gson.fromJson(json, MinecraftTexturesPayload.class);
        } catch (JsonParseException e) {
            LOGGER.error("Could not decode textures payload", e);
            return new HashMap<>();
        }

        if (result.getTextures() == null) {
            return new HashMap<>();
        }

        for (Map.Entry<MinecraftProfileTexture.Type, MinecraftProfileTexture> entry : result.getTextures().entrySet()) {
            if (!isWhitelistedDomain(entry.getValue().getUrl())) {
                LOGGER.error("Textures payload has been tampered with (non-whitelisted domain)");
                return new HashMap<>();
            }
        }

        return result.getTextures();
    }

    @Override
    public GameProfile fillProfileProperties(GameProfile profile, boolean requireSecure) {
        if (profile.getId() == null) {
            return profile;
        }

        if (!requireSecure) {
            return insecureProfiles.getUnchecked(profile);
        }

        return fillGameProfile(profile, true);
    }

    protected GameProfile fillGameProfile(GameProfile profile, boolean requireSecure) {
        try {
            URL url = HttpAuthenticationService.constantURL(BASE_URL + "profile/" + UUIDTypeAdapter.fromUUID(profile.getId()));
            url = HttpAuthenticationService.concatenateURL(url, "unsigned=" + !requireSecure);
            MinecraftProfilePropertiesResponse response = getAuthenticationService().makeRequest(url, null, MinecraftProfilePropertiesResponse.class);

            if (response == null) {
                LOGGER.debug("Couldn't fetch profile properties for " + profile + " as the profile does not exist");
                return profile;
            } else {
                GameProfile result = new GameProfile(response.getId(), response.getName());
                result.getProperties().putAll(response.getProperties());
                profile.getProperties().putAll(response.getProperties());
                LOGGER.debug("Successfully fetched profile properties for " + profile);
                return result;
            }
        } catch (AuthenticationException e) {
            LOGGER.warn("Couldn't look up profile properties for " + profile, e);
            return profile;
        }
    }

    @Override
    public YggdrasilAuthenticationService getAuthenticationService() {
        return (YggdrasilAuthenticationService) super.getAuthenticationService();
    }

    private static boolean isWhitelistedDomain(String url) {
        URI uri = null;

        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL '" + url + "'");
        }

        String domain = uri.getHost();

        for (int i = 0; i < WHITELISTED_DOMAINS.length; i++) {
            if (domain.endsWith(WHITELISTED_DOMAINS[i])) {
                return true;
            }
        }
        return false;
    }
}
