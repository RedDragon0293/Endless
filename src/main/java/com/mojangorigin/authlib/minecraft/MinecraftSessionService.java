package com.mojangorigin.authlib.minecraft;

import com.mojangorigin.authlib.GameProfile;
import com.mojangorigin.authlib.exceptions.AuthenticationException;
import com.mojangorigin.authlib.exceptions.AuthenticationUnavailableException;

import java.util.Map;

public interface MinecraftSessionService {
    /**
     * Attempts to join the specified Minecraft server.
     * <p />
     * The {@link com.mojangorigin.authlib.GameProfile} used to join with may be partial, but the exact requirements will vary on
     * authentication service. If this method returns without throwing an exception, the join was successful and a subsequent call to
     * {@link #hasJoinedServer(com.mojangorigin.authlib.GameProfile, String)} will return true.
     *
     * @param profile Partial {@link com.mojangorigin.authlib.GameProfile} to join as
     * @param authenticationToken The {@link com.mojangorigin.authlib.UserAuthentication#getAuthenticatedToken() authenticated token} of the user
     * @param serverId The random ID of the server to join
     * @throws com.mojangorigin.authlib.exceptions.AuthenticationUnavailableException Thrown when the servers return a malformed response, or are otherwise unavailable
     * @throws com.mojangorigin.authlib.exceptions.InvalidCredentialsException Thrown when the specified authenticationToken is invalid
     * @throws com.mojangorigin.authlib.exceptions.AuthenticationException Generic exception indicating that we could not authenticate the user
     */
    public void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException;

    /**
     * Checks if the specified user has joined a Minecraft server.
     * <p />
     * The {@link com.mojangorigin.authlib.GameProfile} used to join with may be partial, but the exact requirements will vary on
     * authentication service.
     *
     * @param user Partial {@link com.mojangorigin.authlib.GameProfile} to check for
     * @param serverId The random ID of the server to check for
     * @throws com.mojangorigin.authlib.exceptions.AuthenticationUnavailableException Thrown when the servers return a malformed response, or are otherwise unavailable
     * @return Full game profile if the user had joined, otherwise null
     */
    public GameProfile hasJoinedServer(GameProfile user, String serverId) throws AuthenticationUnavailableException;

    /**
     * Gets a map of all known textures from a {@link com.mojangorigin.authlib.GameProfile}.
     * <p />
     * If a profile contains invalid textures, they will not be returned. If a profile contains no textures, an empty map will be returned.
     *
     * @param profile Game profile to return textures from.
     * @param requireSecure If true, requires the payload to be recent and securely fetched.
     * @return Map of texture types to textures.
     * @throws com.mojangorigin.authlib.minecraft.InsecureTextureException If requireSecure is true and the data is insecure
     */
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure);

    /**
     * Fills a profile with all known properties from the session service.
     * <p />
     * The profile must have an ID. If no information is found, nothing will be done.
     *
     * @param profile Game profile to fill with properties.
     * @param requireSecure If you require verifiable correct data.
     * @return Filled profile for the previous user.
     */
    public GameProfile fillProfileProperties(GameProfile profile, boolean requireSecure);
}
