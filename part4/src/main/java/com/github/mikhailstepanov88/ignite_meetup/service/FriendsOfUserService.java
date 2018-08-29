package com.github.mikhailstepanov88.ignite_meetup.service;

import com.github.mikhailstepanov88.ignite_meetup.data.entity.PersonEntity;
import com.github.mikhailstepanov88.ignite_meetup.repository.PersonRepository;
import org.springframework.stereotype.Service;
import reactor.util.annotation.NonNull;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Collection;
import java.util.Optional;

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
    public Optional<Long> createFriendOfUser(long userId, long friendId) {
        if (repository.containPersonFriendWithId(userId, friendId))
            return Optional.empty();
        if (repository.containPersonFriendWithId(friendId, userId))
            return Optional.empty();

        repository.createFriendOfPerson(userId, friendId);
        repository.createFriendOfPerson(friendId, userId);
        return Optional.of(friendId);
    }

    /**
     * Read list of all friends of user.
     *
     * @param userId identifier of user for read.
     * @return list of all friends of user.
     */
    @NonNull
    public Collection<Tuple2<Long, PersonEntity>> readAllFriendsOfUser(long userId) {
        return repository.readPersonByIds(repository.readAllFriendIdsOfPerson(userId));
    }

    /**
     * Read friend of user by his identifier.
     *
     * @param userId   identifier of user for read.
     * @param friendId identifier of friend for read.
     * @return friend of user by his identifier.
     */
    @NonNull
    public Optional<Tuple2<Long, PersonEntity>> readFriendOfUserById(long userId, long friendId) {
        return (repository.containPersonFriendWithId(userId, friendId)) ?
                repository.readPersonById(friendId)
                        .map(it -> Tuples.of(friendId, it)) :
                Optional.empty();
    }

    /**
     * Delete friend of user.
     *
     * @param userId   identifier of user for update.
     * @param friendId identifier of friend for delete.
     * @return operation complete successfully or not.
     */
    public boolean deleteFriendOfUser(long userId, long friendId) {
        if (!repository.containPersonFriendWithId(userId, friendId))
            return false;
        if (!repository.containPersonFriendWithId(friendId, userId))
            return false;

        repository.deleteFriendOfPerson(userId, friendId);
        repository.deleteFriendOfPerson(friendId, userId);
        return true;
    }
}