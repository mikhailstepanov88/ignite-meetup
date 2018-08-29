package com.github.mikhailstepanov88.ignite_meetup.converter;

import org.apache.ignite.lang.IgniteFuture;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import static java.util.Objects.nonNull;

@Component
public class MonoConverter {
    /**
     * Convert future of Ignite to mono.
     *
     * @param igniteFuture   future of Ignite for convert.
     * @param <TypeOfResult> type of result.
     * @return converted mono.
     */
    @NonNull
    public <TypeOfResult> Mono<TypeOfResult> igniteFutureToMono(@NonNull IgniteFuture<TypeOfResult> igniteFuture) {
        return Mono.create(emitter -> igniteFuture.listen(future -> {
            TypeOfResult result = future.get();
            if (nonNull(result))
                emitter.success(result);
            else
                emitter.success();
        }));
    }
}