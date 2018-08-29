package com.github.mikhailstepanov88.ignite_meetup.repository;

import com.github.mikhailstepanov88.ignite_meetup.converter.FluxConverter;
import com.github.mikhailstepanov88.ignite_meetup.converter.MonoConverter;
import com.github.mikhailstepanov88.ignite_meetup.data.entity.PersonEntity;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteAtomicSequence;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.cache.Cache;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.ignite.transactions.TransactionConcurrency.PESSIMISTIC;
import static org.apache.ignite.transactions.TransactionIsolation.SERIALIZABLE;

@Repository
public class PersonRepository {
    //<editor-fold desc="constants">
    private static final String CACHE_NAME = "persons";
    private static final String SEQUENCE_NAME = "persons_sequence";
    //</editor-fold>

    private final MonoConverter monoConverter;
    private final FluxConverter fluxConverter;
    private final IgniteTransactions transactions;
    private final IgniteAtomicSequence personsSequence;
    private final IgniteCache<Long, PersonEntity> personsCache;
    private final IgniteCache<Long, BinaryObject> personsBinaryCache;

    //<editor-fold desc="constructors">
    /**
     * Constructor.
     *
     * @param client        client to database.
     * @param monoConverter converter for mono.
     * @param fluxConverter converter for flux.
     */
    public PersonRepository(@NonNull Ignite client,
                            @NonNull MonoConverter monoConverter,
                            @NonNull FluxConverter fluxConverter) {
        this.monoConverter = monoConverter;
        this.fluxConverter = fluxConverter;
        this.transactions = client.transactions();
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
    @NonNull
    public Mono<Long> createPerson(@NonNull PersonEntity person) {
        Long id = personsSequence.incrementAndGet();
        return monoConverter.igniteFutureToMono(personsCache.putAsync(id, person)).thenReturn(id);
    }

    /**
     * Create friend of person.
     *
     * @param personId identifier of person for update.
     * @param friendId identifier of friend for add.
     * @return operation complete successfully or not.
     */
    @NonNull
    public Mono<Boolean> createFriendOfPerson(long personId, long friendId) {
        return monoConverter.igniteFutureToMono(personsBinaryCache.invokeAsync(personId, (entry, arguments) -> {
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
        }, friendId));
    }

    /**
     * Read person with entered identifier.
     *
     * @param id identifier of person for read.
     * @return person with entered identifier.
     */
    @NonNull
    public Mono<PersonEntity> readPersonById(long id) {
        return monoConverter.igniteFutureToMono(personsCache.getAsync(id));
    }

    /**
     * Read persons with entered identifiers.
     *
     * @param ids identifiers of persons for read.
     * @return persons with entered identifiers.
     */
    @NonNull
    public Flux<Tuple2<Long, PersonEntity>> readPersonByIds(@NonNull Collection<Long> ids) {
        if (ids.isEmpty()) return Flux.empty();
        return fluxConverter.igniteFutureMapToFlux(personsCache.getAllAsync(new HashSet<>(ids)));
    }

    /**
     * Read all persons by entered query.
     *
     * @param firstName first name of person for read.
     * @param lastName  last name of person for read.
     * @return all persons by entered query.
     */
    @NonNull
    public Flux<Tuple2<Long, PersonEntity>> readAllPersonsByQuery(@Nullable String firstName,
                                                                  @Nullable String lastName) {
        Collection<Cache.Entry<Long, BinaryObject>> result = personsBinaryCache.query(
                new ScanQuery<Long, BinaryObject>((key, value) -> {
                    String personFirstName = value.field("firstName");
                    String personLastName = value.field("lastName");
                    return (isNull(firstName) || personFirstName.contains(firstName)) &&
                           (isNull(lastName)  || personLastName.contains(lastName));
                })
        ).getAll();
        return Flux.fromIterable(result.stream()
                .map(it -> Tuples.of(it.getKey(), it.getValue().<PersonEntity>deserialize()))
                .collect(Collectors.toList()));
    }

    /**
     * Read all identifiers of person friends.
     *
     * @param personId identifier of person for read.
     * @return all identifiers of person friends.
     */
    @NonNull
    public Flux<Long> readAllFriendIdsOfPerson(long personId) {
        return fluxConverter.igniteFutureCollectionToFlux(personsBinaryCache.invokeAsync(personId, (entry, arguments) -> {
            if (isNull(entry.getValue())) return new HashSet<>();

            BinaryObject personEntity = entry.getValue();

            return Optional.of(personEntity)
                    .filter(it -> it.hasField("friendIds"))
                    .map(it -> it.<Set<Long>>field("friendIds"))
                    .map(HashSet::new)
                    .orElse(new HashSet<>());
        }));
    }

    /**
     * Check that person contain friend with entered identifier.
     *
     * @param personId identifier of person for check.
     * @param friendId identifier of friend for check.
     * @return person contain friend with entered identifier or not.
     */
    @NonNull
    public Mono<Boolean> containPersonFriendWithId(long personId, long friendId) {
        return monoConverter.igniteFutureToMono(personsBinaryCache.invokeAsync(personId, (entry, arguments) -> {
            if (isNull(entry.getValue())) return false;

            long externalFriendId = (long) arguments[0];

            BinaryObject personEntity = entry.getValue();
            Collection<Long> personFriendIds = Optional.of(personEntity)
                    .filter(it -> it.hasField("friendIds"))
                    .map(it -> it.<Set<Long>>field("friendIds"))
                    .map(HashSet::new)
                    .orElse(new HashSet<>());

            return personFriendIds.contains(externalFriendId);
        }, friendId));
    }

    /**
     * Update person with entered identifier.
     *
     * @param id     identifier of person for update.
     * @param person updated person.
     * @return operation complete successfully or not.
     */
    @NonNull
    public Mono<Boolean> updatePerson(long id, @NonNull PersonEntity person) {
        return monoConverter.igniteFutureToMono(personsCache.replaceAsync(id, person));
    }

    /**
     * Delete person with entered identifier.
     *
     * @param id identifier of person for delete.
     * @return operation complete successfully or not.
     */
    @NonNull
    public Mono<Boolean> deletePerson(long id) {
        return monoConverter.igniteFutureToMono(personsCache.removeAsync(id));
    }

    /**
     * Delete friend of person.
     *
     * @param personId identifier of person for update.
     * @param friendId identifier of friend for delete.
     * @return operation complete successfully or not.
     */
    @NonNull
    public Mono<Boolean> deleteFriendOfPerson(long personId, long friendId) {
        return monoConverter.igniteFutureToMono(personsBinaryCache.invokeAsync(personId, (entry, arguments) -> {
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
        }, friendId));
    }

    /**
     * Execute action in transaction.
     *
     * @param concurrency    concurrency of transaction.
     * @param isolation      isolation of transaction.
     * @param action         action for execution.
     * @param <TypeOfResult> type of action result.
     * @return result of action execution.
     */
    @NonNull
    public <TypeOfResult> Mono<TypeOfResult> executeInTransaction(@Nullable TransactionConcurrency concurrency,
                                                                  @Nullable TransactionIsolation isolation,
                                                                  @NonNull Supplier<Mono<TypeOfResult>> action) {
        return executeInTransaction(concurrency, isolation, null, null, action);
    }

    /**
     * Execute action in transaction.
     *
     * @param concurrency    concurrency of transaction.
     * @param isolation      isolation of transaction.
     * @param timeout        timeout of transaction execution.
     * @param size           size of entries in transaction.
     * @param action         action for execution.
     * @param <TypeOfResult> type of action result.
     * @return result of action execution.
     */
    @NonNull
    public <TypeOfResult> Mono<TypeOfResult> executeInTransaction(@Nullable TransactionConcurrency concurrency,
                                                                  @Nullable TransactionIsolation isolation,
                                                                  @Nullable Long timeout,
                                                                  @Nullable Integer size,
                                                                  @NonNull Supplier<Mono<TypeOfResult>> action) {
        return Mono.using(
                () -> transactions.txStart(
                        Optional.ofNullable(concurrency).orElse(PESSIMISTIC),
                        Optional.ofNullable(isolation).orElse(SERIALIZABLE),
                        Optional.ofNullable(timeout).orElse(0L),
                        Optional.ofNullable(size).orElse(0)),
                transaction -> action.get().doOnSuccess(it -> {
                    if (it != null) transaction.commit();
                    else transaction.rollback();
                }),
                Transaction::close
        );
    }
}