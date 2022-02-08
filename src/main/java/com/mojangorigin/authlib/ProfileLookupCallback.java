package com.mojangorigin.authlib;

public interface ProfileLookupCallback {
    public void onProfileLookupSucceeded(GameProfile profile);

    public void onProfileLookupFailed(GameProfile profile, Exception exception);
}
