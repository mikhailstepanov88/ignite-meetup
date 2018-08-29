package com.github.mikhailstepanov88.ignite_meetup.service;

import com.github.mikhailstepanov88.ignite_meetup.data.entity.PersonEntity;
import com.github.mikhailstepanov88.ignite_meetup.repository.PersonRepository;
import org.springframework.stereotype.Service;
import reactor.util.annotation.NonNull;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Optional;

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
    public long createUser(@NonNull PersonEntity user) {
        return repository.createPerson(user);
    }

    /**
     * Read user by his identifier.
     *
     * @param id identifier of user for read.
     * @return user by his identifier.
     */
    @NonNull
    public Optional<Tuple2<Long, PersonEntity>> readUserById(long id) {
        return repository.readPersonById(id).map(it -> Tuples.of(id, it));
    }

    /**
     * Update user by his identifier.
     *
     * @param id   identifier of user for update.
     * @param user updated user.
     * @return updated user.
     */
    @NonNull
    public Optional<Tuple2<Long, PersonEntity>> updateUser(long id, @NonNull PersonEntity user) {
        return repository.updatePerson(id, user) ?
                Optional.of(Tuples.of(id, user)) :
                Optional.empty();
    }

    /**
     * Delete user by his identifier.
     *
     * @param id identifier of user for delete.
     * @return operation complete successfully or not.
     */
    public boolean deleteUser(long id) {
        return repository.deletePerson(id);
    }
}