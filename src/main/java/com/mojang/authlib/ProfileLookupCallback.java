package com.mojang.authlib;

public interface ProfileLookupCallback {
    public void onProfileLookupSucceeded(GameProfile profile);

    public void onProfileLookupFailed(GameProfile profile, Exception exception);
}
