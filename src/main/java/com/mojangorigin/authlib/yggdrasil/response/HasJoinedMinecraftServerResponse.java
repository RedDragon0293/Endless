package com.mojangorigin.authlib.yggdrasil.response;

import com.mojangorigin.authlib.properties.PropertyMap;

import java.util.UUID;

public class HasJoinedMinecraftServerResponse extends Response {
    private UUID id;
    private PropertyMap properties;

    public UUID getId() {
        return id;
    }

    public PropertyMap getProperties() {
        return properties;
    }
}
