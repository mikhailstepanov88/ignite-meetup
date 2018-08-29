package com.github.mikhailstepanov88.ignite_meetup.converter;

import org.apache.ignite.lang.IgniteFuture;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.annotation.NonNull;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.*;

@Component
public class FluxConverter {
    /**
     * Convert future of Ignite to flux.
     *
     * @param igniteFuture future of Ignite for convert.
     * @param <Item>       type of result item.
     * @return converted flux.
     */
    @NonNull
    public <Item> Flux<Item> igniteFutureCollectionToFlux(@NonNull IgniteFuture<Collection<Item>> igniteFuture) {
        return Flux.create(emitter -> igniteFuture.listen(future -> {
            Collection<Item> result = Optional.ofNullable(future.get()).orElse(new ArrayList<>());
            result.forEach(emitter::next);
            emitter.complete();
        }));
    }

    /**
     * Convert future of Ignite to flux.
     *
     * @param igniteFuture future of Ignite for convert.
     * @param <Id>         type of result item identifier.
     * @param <Item>       type of result item.
     * @return converted flux.
     */
    @NonNull
    public <Id, Item> Flux<Tuple2<Id, Item>> igniteFutureMapToFlux(@NonNull IgniteFuture<Map<Id, Item>> igniteFuture) {
        return Flux.create(emitter -> igniteFuture.listen(future -> {
            Map<Id, Item> result = Optional.ofNullable(future.get()).orElse(new HashMap<>());
            result.entrySet().stream()
                    .map(it -> Tuples.of(it.getKey(), it.getValue()))
                    .forEach(emitter::next);
            emitter.complete();
        }));
    }
}