package com.mojangorigin.authlib.yggdrasil.request;

import com.mojangorigin.authlib.GameProfile;
import com.mojangorigin.authlib.yggdrasil.YggdrasilUserAuthentication;

public class RefreshRequest {
    private String clientToken;
    private String accessToken;
    private GameProfile selectedProfile;
    private boolean requestUser = true;

    public RefreshRequest(YggdrasilUserAuthentication authenticationService) {
        this(authenticationService, null);
    }

    public RefreshRequest(YggdrasilUserAuthentication authenticationService, GameProfile profile) {
        this.clientToken = authenticationService.getAuthenticationService().getClientToken();
        this.accessToken = authenticationService.getAuthenticatedToken();
        this.selectedProfile = profile;
    }
}
