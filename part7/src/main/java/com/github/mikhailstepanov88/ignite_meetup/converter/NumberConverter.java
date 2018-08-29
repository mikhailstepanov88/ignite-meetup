package com.github.mikhailstepanov88.ignite_meetup.converter;

import org.springframework.stereotype.Component;
import reactor.util.annotation.NonNull;

import java.util.Optional;

@Component
public class NumberConverter {
    /**
     * Convert string value to long value.
     *
     * @param value string value for convert.
     * @return converted long value.
     */
    @NonNull
    public Optional<Long> stringToLong(@NonNull String value) {
        try {
            return Optional.of(Long.parseLong(value));
        } catch (final Throwable ex) {
            return Optional.empty();
        }
    }
}