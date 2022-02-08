package com.mojangorigin.authlib.minecraft;

import com.mojangorigin.authlib.AuthenticationService;

public abstract class BaseMinecraftSessionService implements MinecraftSessionService {
    private final AuthenticationService authenticationService;

    protected BaseMinecraftSessionService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }
}
