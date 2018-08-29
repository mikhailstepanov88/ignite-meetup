package com.github.mikhailstepanov88.ignite_meetup.repository;

import com.github.mikhailstepanov88.ignite_meetup.data.entity.PersonEntity;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteAtomicSequence;
import org.apache.ignite.IgniteCache;
import org.springframework.stereotype.Repository;
import reactor.util.annotation.NonNull;

import java.util.Optional;

@Repository
public class PersonRepository {
    //<editor-fold desc="constants">
    private static final String CACHE_NAME = "persons";
    private static final String SEQUENCE_NAME = "persons_sequence";
    //</editor-fold>

    private final IgniteAtomicSequence personsSequence;
    private final IgniteCache<Long, PersonEntity> personsCache;

    //<editor-fold desc="constructors">
    /**
     * Constructor.
     *
     * @param client client to database.
     */
    public PersonRepository(@NonNull Ignite client) {
        this.personsCache = client.getOrCreateCache(CACHE_NAME);
        this.personsSequence = client.atomicSequence(SEQUENCE_NAME, 0, true);
    }
    //</editor-fold>

    /**
     * Create person.
     *
     * @param person person for create.
     * @return identifier of created person.
     */
    public long createPerson(@NonNull PersonEntity person) {
        long id = personsSequence.incrementAndGet();
        personsCache.put(id, person);
        return id;
    }

    /**
     * Read person with entered identifier.
     *
     * @param id identifier of person for read.
     * @return person with entered identifier.
     */
    @NonNull
    public Optional<PersonEntity> readPersonById(long id) {
        return Optional.ofNullable(personsCache.get(id));
    }

    /**
     * Update person with entered identifier.
     *
     * @param id     identifier of person for update.
     * @param person updated person.
     * @return operation complete successfully or not.
     */
    public boolean updatePerson(long id, @NonNull PersonEntity person) {
        return personsCache.replace(id, person);
    }

    /**
     * Delete person with entered identifier.
     *
     * @param id identifier of person for delete.
     * @return operation complete successfully or not.
     */
    public boolean deletePerson(long id) {
        return personsCache.remove(id);
    }
}