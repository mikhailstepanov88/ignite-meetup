package com.github.mikhailstepanov88.ignite_meetup.matcher;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.util.annotation.NonNull;

@Component
public class Matcher {
    private final UserMatcher userMatcher;
    private final FriendsOfUserMatcher friendsOfUserMatcher;

    //<editor-fold desc="constructors">
    /**
     * Constructor.
     *
     * @param userMatcher          matcher for working with users.
     * @param friendsOfUserMatcher matcher for working with friends of user.
     */
    public Matcher(@NonNull UserMatcher userMatcher,
                   @NonNull FriendsOfUserMatcher friendsOfUserMatcher) {
        this.userMatcher = userMatcher;
        this.friendsOfUserMatcher = friendsOfUserMatcher;
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
     * Check that the request matched to the read all users operation.
     *
     * @param request request for check.
     * @return the request matched to the read all users operation or not.
     */
    public boolean matchReadAllUsers(@NonNull ServerRequest request) {
        return userMatcher.matchReadAll(request);
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

    /**
     * Check that the request matched to the create friend of user operation.
     *
     * @param request request for check.
     * @return the request matched to the create friend of user operation or not.
     */
    public boolean matchCreateFriendOfUser(@NonNull ServerRequest request) {
        return friendsOfUserMatcher.matchCreate(request);
    }

    /**
     * Check that the request matched to the read all friends of user operation.
     *
     * @param request request for check.
     * @return the request matched to the read all friends of user operation or not.
     */
    public boolean matchReadAllFriendsOfUser(@NonNull ServerRequest request) {
        return friendsOfUserMatcher.matchReadAll(request);
    }

    /**
     * Check that the request matched to the read friend of user by his identifier operation.
     *
     * @param request request for check.
     * @return the request matched to the read friend of user by his identifier operation or not.
     */
    public boolean matchReadFriendOfUserById(@NonNull ServerRequest request) {
        return friendsOfUserMatcher.matchReadById(request);
    }

    /**
     * Check that the request matched to the delete friend of user operation.
     *
     * @param request request for check.
     * @return the request matched to the delete friend of user operation or not.
     */
    public boolean matchDeleteFriendOfUser(@NonNull ServerRequest request) {
        return friendsOfUserMatcher.matchDelete(request);
    }
}