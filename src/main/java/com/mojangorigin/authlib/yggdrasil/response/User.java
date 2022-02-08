package com.mojangorigin.authlib.yggdrasil.response;

import com.mojangorigin.authlib.properties.PropertyMap;

public class User {
    private String id;
    private PropertyMap properties;

    public String getId() {
        return id;
    }

    public PropertyMap getProperties() {
        return properties;
    }
}
