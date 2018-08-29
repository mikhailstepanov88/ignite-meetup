package com.github.mikhailstepanov88.ignite_meetup.config;

import com.github.mikhailstepanov88.ignite_meetup.config.properties.IgniteDatabaseProperties;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.annotation.NonNull;

import java.util.stream.Collectors;

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
    public Ignite igniteClient(@NonNull IgniteDatabaseProperties properties) {
        return Ignition.start(igniteConfiguration(properties));
    }

    //<editor-fold desc="private additional methods">
    /**
     * Get configuration of Ignite.
     *
     * @param properties properties of Ignite database.
     * @return configuration of Ignite.
     */
    @NonNull
    private IgniteConfiguration igniteConfiguration(@NonNull IgniteDatabaseProperties properties) {
        requireNonNull(properties.getLocalNode());
        requireNonNull(properties.getLocalNode().getName());

        return new IgniteConfiguration()
                .setClientMode(true)
                .setPeerClassLoadingEnabled(true)
                .setDiscoverySpi(igniteDiscoveryConfiguration(properties))
                .setIgniteInstanceName(properties.getLocalNode().getName());
    }

    /**
     * Get configuration of Ignite discovery.
     *
     * @param properties properties of Ignite database.
     * @return configuration of Ignite discovery.
     */
    @NonNull
    private DiscoverySpi igniteDiscoveryConfiguration(@NonNull IgniteDatabaseProperties properties) {
        requireNonNull(properties.getLocalNode());
        requireNonNull(properties.getRemoteNodes());
        requireNonNull(properties.getLocalNode().getHost());
        requireNonNull(properties.getLocalNode().getPort());
        requireNonNull(properties.getLocalNode().getName());

        return new TcpDiscoverySpi()
                .setLocalPort(properties.getLocalNode().getPort())
                .setLocalAddress(properties.getLocalNode().getHost())
                .setIpFinder(new TcpDiscoveryVmIpFinder()
                        .setAddresses(properties.getRemoteNodes().stream()
                                .map(it -> it.getHost() + ":" + it.getPort())
                                .collect(Collectors.toList()))
                        .setShared(true));
    }
    //</editor-fold>
}