package com.github.mikhailstepanov88.ignite_meetup.service;

import com.github.mikhailstepanov88.ignite_meetup.data.entity.PersonEntity;
import com.github.mikhailstepanov88.ignite_meetup.repository.PersonRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Collection;

import static java.lang.Boolean.TRUE;

@Service
public class UserService {
    private final PersonRepository repository;

    //<editor-fold desc="constructors">
    /**
     * Constructor.
     *
     * @param repository repository for working with persons.
     */
    public UserService(@NonNull PersonRepository repository) {
        this.repository = repository;
    }
    //</editor-fold>

    /**
     * Create user.
     *
     * @param user user for create.
     * @return identifier of created user.
     */
    @NonNull
    public Mono<Long> createUser(@NonNull PersonEntity user) {
        return repository.createPerson(user);
    }

    /**
     * Read list of all users by entered query.
     *
     * @param firstName first name of person for read.
     * @param lastName  last name of person for read.
     * @return list of all users by entered query.
     */
    @NonNull
    public Flux<Tuple2<Long, PersonEntity>> readAllUsersByQuery(@Nullable String firstName,
                                                                @Nullable String lastName) {
        return repository.readAllPersonsByQuery(firstName, lastName);
    }

    /**
     * Read user by his identifier.
     *
     * @param id identifier of user for read.
     * @return user by his identifier.
     */
    @NonNull
    public Mono<Tuple2<Long, PersonEntity>> readUserById(long id) {
        return repository.readPersonById(id).map(it -> Tuples.of(id, it));
    }

    /**
     * Read users by his identifiers.
     *
     * @param ids identifiers of users for read.
     * @return users by his identifiers.
     */
    @NonNull
    public Flux<Tuple2<Long, PersonEntity>> readUsersByIds(Collection<Long> ids) {
        return repository.readPersonByIds(ids);
    }

    /**
     * Update user by his identifier.
     *
     * @param id   identifier of user for update.
     * @param user updated user.
     * @return updated user.
     */
    @NonNull
    public Mono<Tuple2<Long, PersonEntity>> updateUser(long id, @NonNull PersonEntity user) {
        return repository.updatePerson(id, user)
                .filter(TRUE::equals)
                .map(it -> Tuples.of(id, user));
    }

    /**
     * Delete user by his identifier.
     *
     * @param id identifier of user for delete.
     * @return operation complete successfully or not.
     */
    @NonNull
    public Mono<Boolean> deleteUser(long id) {
        return repository.deletePerson(id);
    }
}