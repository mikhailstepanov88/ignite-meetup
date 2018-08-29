package com.github.mikhailstepanov88.ignite_meetup.config.properties;

import reactor.util.annotation.Nullable;

import java.util.Objects;

public class IgniteDatabaseNode {
    @Nullable
    private String name;
    @Nullable
    private String host;
    @Nullable
    private Integer port;

    //<editor-fold desc="constructors">
    /**
     * Constructor
     */
    public IgniteDatabaseNode() {}
    //</editor-fold>

    //<editor-fold desc="getters and setters">
    @Nullable public String getName() {return name;}
    @Nullable public String getHost() {return host;}
    @Nullable public Integer getPort() {return port;}
    public void setName(@Nullable String name) {this.name = name;}
    public void setHost(@Nullable String host) {this.host = host;}
    public void setPort(@Nullable Integer port) {this.port = port;}
    //</editor-fold>

    //<editor-fold desc="equals and hashCode">
    @Override
    public boolean equals(@Nullable Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        IgniteDatabaseNode igniteDatabaseNode = (IgniteDatabaseNode) that;
        return Objects.equals(name, igniteDatabaseNode.name) &&
                Objects.equals(host, igniteDatabaseNode.host) &&
                Objects.equals(port, igniteDatabaseNode.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, host, port);
    }
    //</editor-fold>
}
