package com.mojangorigin.authlib.yggdrasil;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mojangorigin.authlib.*;
import com.mojangorigin.authlib.exceptions.AuthenticationException;
import com.mojangorigin.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;

public class YggdrasilGameProfileRepository implements GameProfileRepository {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String BASE_URL = "https://api.mojang.com/";
    private static final String SEARCH_PAGE_URL = BASE_URL + "profiles/";
    private static final int ENTRIES_PER_PAGE = 2;
    private static final int MAX_FAIL_COUNT = 3;
    private static final int DELAY_BETWEEN_PAGES = 100;
    private static final int DELAY_BETWEEN_FAILURES = 750;

    private final YggdrasilAuthenticationService authenticationService;

    public YggdrasilGameProfileRepository(YggdrasilAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void findProfilesByNames(String[] names, Agent agent, ProfileLookupCallback callback) {
        Set<String> criteria = Sets.newHashSet();

        for (String name : names) {
            if (!Strings.isNullOrEmpty(name)) {
                criteria.add(name.toLowerCase());
            }
        }

        int page = 0;

        for (List<String> request : Iterables.partition(criteria, ENTRIES_PER_PAGE)) {
            int failCount = 0;
            boolean failed;

            do {
                failed = false;

                try {
                    ProfileSearchResultsResponse response = authenticationService.makeRequest(HttpAuthenticationService.constantURL(SEARCH_PAGE_URL + agent.getName().toLowerCase()), request, ProfileSearchResultsResponse.class);
                    failCount = 0;

                    LOGGER.debug("Page {} returned {} results, parsing", page, response.getProfiles().length);

                    Set<String> missing = Sets.newHashSet(request);
                    for (GameProfile profile : response.getProfiles()) {
                        LOGGER.debug("Successfully looked up profile {}", profile);
                        missing.remove(profile.getName().toLowerCase());
                        callback.onProfileLookupSucceeded(profile);
                    }

                    for (String name : missing) {
                        LOGGER.debug("Couldn't find profile {}", name);
                        callback.onProfileLookupFailed(new GameProfile(null, name), new ProfileNotFoundException("Server did not find the requested profile"));
                    }

                    try {
                        Thread.sleep(DELAY_BETWEEN_PAGES);
                    } catch (InterruptedException ignored) {
                    }
                } catch (AuthenticationException e) {
                    failCount++;

                    if (failCount == MAX_FAIL_COUNT) {
                        for (String name : request) {
                            LOGGER.debug("Couldn't find profile {} because of a server error", name);
                            callback.onProfileLookupFailed(new GameProfile(null, name), e);
                        }
                    } else {
                        try {
                            Thread.sleep(DELAY_BETWEEN_FAILURES);
                        } catch (InterruptedException ignored) {
                        }
                        failed = true;
                    }
                }
            } while (failed);
        }
    }
}
