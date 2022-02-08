package com.mojangorigin.authlib.legacy;

import com.mojangorigin.authlib.GameProfile;
import com.mojangorigin.authlib.HttpAuthenticationService;
import com.mojangorigin.authlib.HttpUserAuthentication;
import com.mojangorigin.authlib.UserType;
import com.mojangorigin.authlib.exceptions.AuthenticationException;
import com.mojangorigin.authlib.exceptions.InvalidCredentialsException;
import com.mojangorigin.util.UUIDTypeAdapter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LegacyUserAuthentication extends HttpUserAuthentication {
    private static final URL AUTHENTICATION_URL = HttpAuthenticationService.constantURL("https://login.minecraft.net");
    private static final int AUTHENTICATION_VERSION = 14;

    // 0          1          2            3          4
    // deprecated,deprecated,profile name,session id,profile id
    private static final int RESPONSE_PART_PROFILE_NAME = 2;
    private static final int RESPONSE_PART_SESSION_TOKEN = 3;
    private static final int RESPONSE_PART_PROFILE_ID = 4;

    private String sessionToken;

    protected LegacyUserAuthentication(LegacyAuthenticationService authenticationService) {
        super(authenticationService);
    }

    @Override
    public void logIn() throws AuthenticationException {
        if (StringUtils.isBlank(getUsername())) {
            throw new InvalidCredentialsException("Invalid username");
        }
        if (StringUtils.isBlank(getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        Map<String, Object> args = new HashMap<String, Object>();
        args.put("user", getUsername());
        args.put("password", getPassword());
        args.put("version", AUTHENTICATION_VERSION);
        String response;

        try {
            response = getAuthenticationService().performPostRequest(AUTHENTICATION_URL, HttpAuthenticationService.buildQuery(args), "application/x-www-form-urlencoded").trim();
        } catch (IOException e) {
            throw new AuthenticationException("Authentication server is not responding", e);
        }

        String[] split = response.split(":");

        if (split.length == 5) {
            String profileId = split[RESPONSE_PART_PROFILE_ID];
            String profileName = split[RESPONSE_PART_PROFILE_NAME];
            String sessionToken = split[RESPONSE_PART_SESSION_TOKEN];

            if (StringUtils.isBlank(profileId) || StringUtils.isBlank(profileName) || StringUtils.isBlank(sessionToken)) {
                throw new AuthenticationException("Unknown response from authentication server: " + response);
            }

            setSelectedProfile(new GameProfile(UUIDTypeAdapter.fromString(profileId), profileName));
            this.sessionToken = sessionToken;
            setUserType(UserType.LEGACY);
        } else {
            throw new InvalidCredentialsException(response);
        }
    }

    @Override
    public void logOut() {
        super.logOut();
        sessionToken = null;
    }

    @Override
    public boolean canPlayOnline() {
        return isLoggedIn() && getSelectedProfile() != null && getAuthenticatedToken() != null;
    }

    @Override
    public GameProfile[] getAvailableProfiles() {
        if (getSelectedProfile() != null) {
            return new GameProfile[] {getSelectedProfile()};
        } else {
            return new GameProfile[0];
        }
    }

    /**
     * This method is not supported in the Legacy authentication service.
     * <p />
     * Attempts to call this method will fail.
     */
    @Override
    public void selectGameProfile(GameProfile profile) throws AuthenticationException {
        throw new UnsupportedOperationException("Game profiles cannot be changed in the legacy authentication service");
    }

    @Override
    public String getAuthenticatedToken() {
        return sessionToken;
    }

    @Override
    public String getUserID() {
        return getUsername();
    }

    @Override
    public LegacyAuthenticationService getAuthenticationService() {
        return (LegacyAuthenticationService) super.getAuthenticationService();
    }
}
