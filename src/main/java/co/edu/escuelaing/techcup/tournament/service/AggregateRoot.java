// domain/model/AggregateRoot.java
package co.edu.escuelaing.techcup.tournament.service;

import java.util.Objects;

public abstract class AggregateRoot {

    protected String id;

    protected AggregateRoot(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        AggregateRoot that = (AggregateRoot) other;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}