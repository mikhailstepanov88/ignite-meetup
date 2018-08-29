package com.github.mikhailstepanov88.ignite_meetup.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

@Configuration
@ConfigurationProperties("database")
public class IgniteDatabaseProperties {
    @Nullable
    private Collection<IgniteDatabaseNode> remoteNodes;

    //<editor-fold desc="constructors">
    /**
     * Constructor.
     */
    public IgniteDatabaseProperties() {}
    //</editor-fold>

    //<editor-fold desc="getters and setters">
    @NonNull
    public Collection<IgniteDatabaseNode> getRemoteNodes() {
        return Optional.ofNullable(remoteNodes).orElse(new HashSet<>());
    }
    public void setRemoteNodes(@Nullable Collection<IgniteDatabaseNode> remoteNodes) {this.remoteNodes = remoteNodes;}
    //</editor-fold>

    //<editor-fold desc="equals and hashCode">
    @Override
    public boolean equals(@Nullable Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        IgniteDatabaseProperties igniteDatabaseProperties = (IgniteDatabaseProperties) that;
        return Objects.equals(remoteNodes, igniteDatabaseProperties.remoteNodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(remoteNodes);
    }
    //</editor-fold>
}
