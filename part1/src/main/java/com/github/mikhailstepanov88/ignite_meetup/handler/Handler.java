package com.github.mikhailstepanov88.ignite_meetup.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

@Component
public class Handler {
    private final UserHandler userHandler;

    //<editor-fold desc="constructors">
    /**
     * Constructor.
     *
     * @param userHandler handler for working with users.
     */
    public Handler(@NonNull UserHandler userHandler) {
        this.userHandler = userHandler;
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
}