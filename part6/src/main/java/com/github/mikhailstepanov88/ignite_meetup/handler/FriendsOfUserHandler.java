package com.github.mikhailstepanov88.ignite_meetup.handler;

import com.github.mikhailstepanov88.ignite_meetup.converter.NumberConverter;
import com.github.mikhailstepanov88.ignite_meetup.converter.PersonConverter;
import com.github.mikhailstepanov88.ignite_meetup.data.dto.PersonDTO;
import com.github.mikhailstepanov88.ignite_meetup.service.FriendsOfUserService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import reactor.util.function.Tuples;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static java.util.function.Function.identity;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class FriendsOfUserHandler {
    private final NumberConverter numberConverter;
    private final PersonConverter personConverter;
    private final FriendsOfUserService service;

    //<editor-fold desc="constructors">
    /**
     * Constructor.
     *
     * @param numberConverter converter from/to number.
     * @param personConverter converter from/to person data transfer object.
     * @param service         service for working with friends of user.
     */
    public FriendsOfUserHandler(@NonNull NumberConverter numberConverter,
                                @NonNull PersonConverter personConverter,
                                @NonNull FriendsOfUserService service) {
        this.numberConverter = numberConverter;
        this.personConverter = personConverter;
        this.service = service;
    }
    //</editor-fold>

    /**
     * Handle create operation.
     *
     * @param request request for handle.
     * @return response of create operation.
     */
    @NonNull
    public Mono<ServerResponse> handleCreate(@NonNull ServerRequest request) {
        return Mono.just(getUserIdFromRequest(request))
                .zipWith(request.bodyToMono(PersonDTO.class))
                .filter(it -> nonNull(it.getT2().getId()))
                .map(it -> service.createFriendOfUser(it.getT1(), it.getT2().getId()))
                .flatMap(Mono::justOrEmpty)
                .flatMap(it -> created(request.uriBuilder()
                        .path("/")
                        .path(it.toString())
                        .build()).build())
                .switchIfEmpty(notFound().build())
                .onErrorResume(this::exceptionToResponse);
    }

    /**
     * Handle read all operation.
     *
     * @param request request for handle.
     * @return response of read all operation.
     */
    @NonNull
    public Mono<ServerResponse> handleReadAll(@NonNull ServerRequest request) {
        return ok().body(
                Mono.just(getUserIdFromRequest(request))
                        .map(service::readAllFriendsOfUser)
                        .flatMapIterable(identity())
                        .map(it -> personConverter.entityToDTO(it.getT1(), it.getT2())),
                PersonDTO.class
        ).onErrorResume(this::exceptionToResponse);
    }

    /**
     * Handle read by identifier operation.
     *
     * @param request request for handle.
     * @return response of read by identifier operation.
     */
    @NonNull
    public Mono<ServerResponse> handleReadById(@NonNull ServerRequest request) {
        return Mono.just(Tuples.of(getUserIdFromRequest(request), getFriendIdFromRequest(request)))
                .map(it -> service.readFriendOfUserById(it.getT1(), it.getT2()))
                .flatMap(Mono::justOrEmpty)
                .map(it -> personConverter.entityToDTO(it.getT1(), it.getT2()))
                .flatMap(it -> ok().syncBody(it))
                .switchIfEmpty(notFound().build())
                .onErrorResume(this::exceptionToResponse);
    }

    /**
     * Handle delete operation.
     *
     * @param request request for handle.
     * @return response of delete operation.
     */
    @NonNull
    public Mono<ServerResponse> handleDelete(@NonNull ServerRequest request) {
        return Mono.just(Tuples.of(getUserIdFromRequest(request), getFriendIdFromRequest(request)))
                .map(it -> service.deleteFriendOfUser(it.getT1(), it.getT2()))
                .then(noContent().build())
                .onErrorResume(this::exceptionToResponse);
    }

    //<editor-fold desc="private additional methods">
    /**
     * Get identifier of user from request.
     *
     * @param request request for read.
     * @return identifier of user from request.
     */
    @NonNull
    private Long getUserIdFromRequest(@NonNull ServerRequest request) {
        return Optional.of(request.pathVariable("userId"))
                .flatMap(numberConverter::stringToLong)
                .orElseThrow(() -> new IllegalArgumentException("Path variable with name \"userId\" is not valid"));
    }

    /**
     * Get identifier of user friend from request.
     *
     * @param request request for read.
     * @return identifier of user friend from request.
     */
    @NonNull
    private Long getFriendIdFromRequest(@NonNull ServerRequest request) {
        return Optional.of(request.pathVariable("friendId"))
                .flatMap(numberConverter::stringToLong)
                .orElseThrow(() -> new IllegalArgumentException("Path variable with name \"friendId\" is not valid"));
    }

    /**
     * Convert exception to server response.
     *
     * @param ex exception for convert.
     * @return converted server response.
     */
    @NonNull
    private Mono<ServerResponse> exceptionToResponse(@NonNull Throwable ex) {
        if (ex instanceof IllegalArgumentException)
            return badRequest().body(Mono.just(ex.getMessage()), String.class);
        else
            return status(501).body(Mono.just("Something goes wrong"), String.class);
    }
    //</editor-fold>
}