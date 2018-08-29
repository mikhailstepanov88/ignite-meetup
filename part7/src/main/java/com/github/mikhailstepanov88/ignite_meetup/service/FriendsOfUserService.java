package com.github.mikhailstepanov88.ignite_meetup.service;

import com.github.mikhailstepanov88.ignite_meetup.data.entity.PersonEntity;
import com.github.mikhailstepanov88.ignite_meetup.repository.PersonRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import static java.lang.Boolean.TRUE;
import static org.apache.ignite.transactions.TransactionConcurrency.OPTIMISTIC;
import static org.apache.ignite.transactions.TransactionIsolation.SERIALIZABLE;

@Service
public class FriendsOfUserService {
    private final PersonRepository repository;

    //<editor-fold desc="constructors">
    /**
     * Constructor.
     *
     * @param repository repository for working with persons.
     */
    public FriendsOfUserService(@NonNull PersonRepository repository) {
        this.repository = repository;
    }
    //</editor-fold>

    /**
     * Create friend of user.
     *
     * @param userId   identifier of user for update.
     * @param friendId identifier of user friend for create.
     * @return identifier of created friend of user.
     */
    @NonNull
    public Mono<Long> createFriendOfUser(long userId, long friendId) {
        return repository.executeInTransaction(OPTIMISTIC, SERIALIZABLE,
                () -> repository.createFriendOfPerson(userId, friendId)
                        .filter(TRUE::equals)
                        .flatMap(it -> repository.createFriendOfPerson(friendId, userId))
                        .filter(TRUE::equals)
                        .map(it -> friendId));
    }

    /**
     * Read list of all friends of user.
     *
     * @param userId identifier of user for read.
     * @return list of all friends of user.
     */
    @NonNull
    public Flux<Tuple2<Long, PersonEntity>> readAllFriendsOfUser(long userId) {
        return repository.readAllFriendIdsOfPerson(userId).collectList()
                .flatMapMany(repository::readPersonByIds);
    }

    /**
     * Read friend of user by his identifier.
     *
     * @param userId   identifier of user for read.
     * @param friendId identifier of friend for read.
     * @return friend of user by his identifier.
     */
    @NonNull
    public Mono<Tuple2<Long, PersonEntity>> readFriendOfUserById(long userId, long friendId) {
        return repository.containPersonFriendWithId(userId, friendId)
                .filter(TRUE::equals)
                .flatMap(it -> repository.readPersonById(friendId))
                .map(it -> Tuples.of(friendId, it));
    }

    /**
     * Delete friend of user.
     *
     * @param userId   identifier of user for update.
     * @param friendId identifier of friend for delete.
     * @return operation complete successfully or not.
     */
    @NonNull
    public Mono<Boolean> deleteFriendOfUser(long userId, long friendId) {
        return repository.executeInTransaction(OPTIMISTIC, SERIALIZABLE,
                () -> repository.deleteFriendOfPerson(userId, friendId)
                        .filter(TRUE::equals)
                        .flatMap(it -> repository.deleteFriendOfPerson(friendId, userId))
                        .filter(TRUE::equals)
                        .map(it -> true)
        ).defaultIfEmpty(false);
    }
}