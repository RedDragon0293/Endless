package com.mojangorigin.authlib;

public interface GameProfileRepository {
    public void findProfilesByNames(String[] names, Agent agent, ProfileLookupCallback callback);
}
