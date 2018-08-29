package com.github.mikhailstepanov88.ignite_meetup.config;

import com.github.mikhailstepanov88.ignite_meetup.config.properties.IgniteDatabaseProperties;
import org.apache.ignite.Ignition;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.annotation.NonNull;

import static java.util.Objects.requireNonNull;

@Configuration
public class IgniteDatabaseConfig {
    /**
     * Get client of Ignite.
     *
     * @param properties properties of Ignite database.
     * @return client of Ignite.
     */
    @NonNull
    @Bean(destroyMethod = "close")
    public IgniteClient igniteClient(@NonNull IgniteDatabaseProperties properties) {
        return Ignition.startClient(igniteConfiguration(properties));
    }

    //<editor-fold desc="private additional methods">
    /**
     * Get configuration of Ignite.
     *
     * @param properties properties of Ignite database.
     * @return configuration of Ignite.
     */
    @NonNull
    private ClientConfiguration igniteConfiguration(@NonNull IgniteDatabaseProperties properties) {
        requireNonNull(properties.getRemoteNodes());

        return new ClientConfiguration().setAddresses(properties.getRemoteNodes().stream()
                .map(it -> it.getHost() + ":" + it.getPort())
                .toArray(String[]::new));
    }
    //</editor-fold>
}