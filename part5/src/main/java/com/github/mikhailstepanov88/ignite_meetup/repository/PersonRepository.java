package com.github.mikhailstepanov88.ignite_meetup.repository;

import com.github.mikhailstepanov88.ignite_meetup.data.entity.PersonEntity;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteAtomicSequence;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.query.ScanQuery;
import org.springframework.stereotype.Repository;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.cache.Cache;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Repository
public class PersonRepository {
    //<editor-fold desc="constants">
    private static final String CACHE_NAME = "persons";
    private static final String SEQUENCE_NAME = "persons_sequence";
    //</editor-fold>

    private final IgniteAtomicSequence personsSequence;
    private final IgniteCache<Long, PersonEntity> personsCache;
    private final IgniteCache<Long, BinaryObject> personsBinaryCache;

    //<editor-fold desc="constructors">
    /**
     * Constructor.
     *
     * @param client client to database.
     */
    public PersonRepository(@NonNull Ignite client) {
        this.personsCache = client.getOrCreateCache(CACHE_NAME);
        this.personsSequence = client.atomicSequence(SEQUENCE_NAME, 0, true);
        this.personsBinaryCache = client.getOrCreateCache(CACHE_NAME).withKeepBinary();
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
     * Create friend of person.
     *
     * @param personId identifier of person for update.
     * @param friendId identifier of friend for add.
     * @return operation complete successfully or not.
     */
    public boolean createFriendOfPerson(long personId, long friendId) {
        return personsBinaryCache.invoke(personId, (entry, arguments) -> {
            if (isNull(entry.getValue())) return false;

            long externalFriendId = (long) arguments[0];

            BinaryObject personEntity = entry.getValue();
            Collection<Long> personFriendIds = Optional.of(personEntity)
                    .filter(it -> it.hasField("friendIds"))
                    .map(it -> it.<Set<Long>>field("friendIds"))
                    .map(HashSet::new)
                    .orElse(new HashSet<>());

            if (!personFriendIds.add(externalFriendId)) return false;

            entry.setValue(personEntity.toBuilder()
                    .setField("friendIds", personFriendIds, Object.class)
                    .build());
            return true;
        }, friendId);
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
     * Read persons with entered identifiers.
     *
     * @param ids identifiers of persons for read.
     * @return persons with entered identifiers.
     */
    @NonNull
    public Collection<Tuple2<Long, PersonEntity>> readPersonByIds(@NonNull Collection<Long> ids) {
        if (ids.isEmpty()) return new HashSet<>();

        return personsCache.getAll(new HashSet<>(ids)).entrySet().stream()
                .map(it -> Tuples.of(it.getKey(), it.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Read all persons by entered query.
     *
     * @param firstName first name of person for read.
     * @param lastName  last name of person for read.
     * @return all persons by entered query.
     */
    @NonNull
    public Collection<Tuple2<Long, PersonEntity>> readAllPersonsByQuery(@Nullable String firstName,
                                                                        @Nullable String lastName) {
        Collection<Cache.Entry<Long, BinaryObject>> result = personsBinaryCache.query(
                new ScanQuery<Long, BinaryObject>((key, value) -> {
                    String personFirstName = value.field("firstName");
                    String personLastName = value.field("lastName");
                    return (isNull(firstName) || personFirstName.contains(firstName)) &&
                            (isNull(lastName) || personLastName.contains(lastName));
                })
        ).getAll();
        return result.stream()
                .map(it -> Tuples.of(it.getKey(), it.getValue().<PersonEntity>deserialize()))
                .collect(Collectors.toList());
    }

    /**
     * Read all identifiers of person friends.
     *
     * @param personId identifier of person for read.
     * @return all identifiers of person friends.
     */
    @NonNull
    public Collection<Long> readAllFriendIdsOfPerson(long personId) {
        return personsBinaryCache.invoke(personId, (entry, arguments) -> {
            if (isNull(entry.getValue())) return new HashSet<>();

            BinaryObject personEntity = entry.getValue();

            return Optional.of(personEntity)
                    .filter(it -> it.hasField("friendIds"))
                    .map(it -> it.<Set<Long>>field("friendIds"))
                    .map(HashSet::new)
                    .orElse(new HashSet<>());
        });
    }

    /**
     * Check that person contain friend with entered identifier.
     *
     * @param personId identifier of person for check.
     * @param friendId identifier of friend for check.
     * @return person contain friend with entered identifier or not.
     */
    public boolean containPersonFriendWithId(long personId, long friendId) {
        return personsBinaryCache.invoke(personId, (entry, arguments) -> {
            if (isNull(entry.getValue())) return false;

            long externalFriendId = (long) arguments[0];

            BinaryObject personEntity = entry.getValue();
            Collection<Long> personFriendIds = Optional.of(personEntity)
                    .filter(it -> it.hasField("friendIds"))
                    .map(it -> it.<Set<Long>>field("friendIds"))
                    .map(HashSet::new)
                    .orElse(new HashSet<>());

            return personFriendIds.contains(externalFriendId);
        }, friendId);
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

    /**
     * Delete friend of person.
     *
     * @param personId identifier of person for update.
     * @param friendId identifier of friend for delete.
     * @return operation complete successfully or not.
     */
    public boolean deleteFriendOfPerson(long personId, long friendId) {
        return personsBinaryCache.invoke(personId, (entry, arguments) -> {
            if (isNull(entry.getValue())) return false;

            long externalFriendId = (long) arguments[0];

            BinaryObject personEntity = entry.getValue();
            Collection<Long> personFriendIds = Optional.of(personEntity)
                    .filter(it -> it.hasField("friendIds"))
                    .map(it -> it.<Set<Long>>field("friendIds"))
                    .map(HashSet::new)
                    .orElse(new HashSet<>());

            if (!personFriendIds.remove(externalFriendId)) return false;

            entry.setValue(personEntity.toBuilder()
                    .setField("friendIds", personFriendIds, Object.class)
                    .build());
            return true;
        }, friendId);
    }

    /**
     * Get lock for person with entered identifier.
     *
     * @param id identifier of person for lock.
     * @return lock for person with entered identifier.
     */
    @NonNull
    public Lock lock(@NonNull Long id) {
        return personsCache.lock(id);
    }
}