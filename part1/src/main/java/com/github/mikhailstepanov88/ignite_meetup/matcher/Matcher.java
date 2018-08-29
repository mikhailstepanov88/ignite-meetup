package com.github.mikhailstepanov88.ignite_meetup.matcher;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.util.annotation.NonNull;

@Component
public class Matcher {
    private final UserMatcher userMatcher;

    //<editor-fold desc="constructors">
    /**
     * Constructor.
     *
     * @param userMatcher matcher for working with users.
     */
    public Matcher(@NonNull UserMatcher userMatcher) {
        this.userMatcher = userMatcher;
    }
    //</editor-fold>

    /**
     * Check that the request matched to the create user operation.
     *
     * @param request request for check.
     * @return the request matched to the create user operation or not.
     */
    public boolean matchCreateUser(@NonNull ServerRequest request) {
        return userMatcher.matchCreate(request);
    }

    /**
     * Check that the request matched to the read user by his identifier operation.
     *
     * @param request request for check.
     * @return the request matched to the read user by his identifier operation or not.
     */
    public boolean matchReadUserById(@NonNull ServerRequest request) {
        return userMatcher.matchReadById(request);
    }

    /**
     * Check that the request matched to the update user operation.
     *
     * @param request request for check.
     * @return the request matched to the update user operation or not.
     */
    public boolean matchUpdateUser(@NonNull ServerRequest request) {
        return userMatcher.matchUpdate(request);
    }

    /**
     * Check that the request matched to the delete user operation.
     *
     * @param request request for check.
     * @return the request matched to the delete user operation or not.
     */
    public boolean matchDeleteUser(@NonNull ServerRequest request) {
        return userMatcher.matchDelete(request);
    }
}