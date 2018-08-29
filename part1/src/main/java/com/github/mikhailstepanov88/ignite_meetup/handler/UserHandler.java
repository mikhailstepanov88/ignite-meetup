package com.github.mikhailstepanov88.ignite_meetup.handler;

import com.github.mikhailstepanov88.ignite_meetup.converter.NumberConverter;
import com.github.mikhailstepanov88.ignite_meetup.converter.PersonConverter;
import com.github.mikhailstepanov88.ignite_meetup.data.dto.PersonDTO;
import com.github.mikhailstepanov88.ignite_meetup.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class UserHandler {
    private final UserService service;
    private final NumberConverter numberConverter;
    private final PersonConverter personConverter;

    //<editor-fold desc="constructors">
    /**
     * Constructor.
     *
     * @param service         service for working with users.
     * @param numberConverter converter from/to number.
     * @param personConverter converter from/to person data transfer object.
     */
    public UserHandler(@NonNull UserService service,
                       @NonNull NumberConverter numberConverter,
                       @NonNull PersonConverter personConverter) {
        this.service = service;
        this.numberConverter = numberConverter;
        this.personConverter = personConverter;
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
        return request.bodyToMono(PersonDTO.class)
                .filter(it -> nonNull(it.getId()))
                .map(it -> service.createUser(it.getId(),
                        personConverter.dtoToEntity(it)))
                .flatMap(it -> created(request.uriBuilder()
                        .path("/")
                        .path(it.toString())
                        .build()).build())
                .switchIfEmpty(notFound().build())
                .onErrorResume(this::exceptionToResponse);
    }

    /**
     * Handle read by identifier operation.
     *
     * @param request request for handle.
     * @return response of read by identifier operation.
     */
    @NonNull
    public Mono<ServerResponse> handleReadById(@NonNull ServerRequest request) {
        return Mono.just(getUserIdFromRequest(request))
                .map(service::readUserById)
                .flatMap(Mono::justOrEmpty)
                .map(it -> personConverter.entityToDTO(it.getT1(), it.getT2()))
                .flatMap(it -> ok().syncBody(it))
                .switchIfEmpty(notFound().build())
                .onErrorResume(this::exceptionToResponse);
    }

    /**
     * Handle update operation.
     *
     * @param request request for handle.
     * @return response of update operation.
     */
    @NonNull
    public Mono<ServerResponse> handleUpdate(@NonNull ServerRequest request) {
        return Mono.just(getUserIdFromRequest(request))
                .zipWith(request.bodyToMono(PersonDTO.class))
                .map(it -> service.updateUser(it.getT1(),
                        personConverter.dtoToEntity(it.getT2())))
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
        return Mono.just(getUserIdFromRequest(request))
                .map(service::deleteUser)
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