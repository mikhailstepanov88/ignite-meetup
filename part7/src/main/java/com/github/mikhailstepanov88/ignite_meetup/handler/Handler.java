package com.github.mikhailstepanov88.ignite_meetup.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

@Component
public class Handler {
    private final UserHandler userHandler;
    private final FriendsOfUserHandler friendsOfUserHandler;

    //<editor-fold desc="constructors">
    /**
     * Constructor.
     *
     * @param userHandler          handler for working with users.
     * @param friendsOfUserHandler handler for working with friends of user.
     */
    public Handler(@NonNull UserHandler userHandler,
                   @NonNull FriendsOfUserHandler friendsOfUserHandler) {
        this.userHandler = userHandler;
        this.friendsOfUserHandler = friendsOfUserHandler;
    }
    //</editor-fold>

    /**
     * Handle create user operation.
     *
     * @param request request for handle.
     * @return response of create user operation.
     */
    @NonNull
    public Mono<ServerResponse> handleCreateUser(@NonNull ServerRequest request) {
        return userHandler.handleCreate(request);
    }

    /**
     * Handle read all users operation.
     *
     * @param request request for handle.
     * @return response of read all users operation.
     */
    @NonNull
    public Mono<ServerResponse> handleReadAllUsers(@NonNull ServerRequest request) {
        return userHandler.handleReadAll(request);
    }

    /**
     * Handle read user by his identifier operation.
     *
     * @param request request for handle.
     * @return response of read user by his identifier operation.
     */
    @NonNull
    public Mono<ServerResponse> handleReadUserById(@NonNull ServerRequest request) {
        return userHandler.handleReadById(request);
    }

    /**
     * Handle update user operation.
     *
     * @param request request for handle.
     * @return response of update user operation.
     */
    @NonNull
    public Mono<ServerResponse> handleUpdateUser(@NonNull ServerRequest request) {
        return userHandler.handleUpdate(request);
    }

    /**
     * Handle delete user operation.
     *
     * @param request request for handle.
     * @return response of delete user operation.
     */
    @NonNull
    public Mono<ServerResponse> handleDeleteUser(@NonNull ServerRequest request) {
        return userHandler.handleDelete(request);
    }

    /**
     * Handle create friend of user operation.
     *
     * @param request request for handle.
     * @return response of create friend of user operation.
     */
    @NonNull
    public Mono<ServerResponse> handleCreateFriendOfUser(@NonNull ServerRequest request) {
        return friendsOfUserHandler.handleCreate(request);
    }

    /**
     * Handle read all friends of user operation.
     *
     * @param request request for handle.
     * @return response of read all friends of user operation.
     */
    @NonNull
    public Mono<ServerResponse> handleReadAllFriendsOfUser(@NonNull ServerRequest request) {
        return friendsOfUserHandler.handleReadAll(request);
    }

    /**
     * Handle read friend of user by his identifier operation.
     *
     * @param request request for handle.
     * @return response of read friend of user by his identifier operation.
     */
    @NonNull
    public Mono<ServerResponse> handleReadFriendOfUserById(@NonNull ServerRequest request) {
        return friendsOfUserHandler.handleReadById(request);
    }

    /**
     * Handle delete friend of user operation.
     *
     * @param request request for handle.
     * @return response of delete friend of user operation.
     */
    @NonNull
    public Mono<ServerResponse> handleDeleteFriendOfUser(@NonNull ServerRequest request) {
        return friendsOfUserHandler.handleDelete(request);
    }
}