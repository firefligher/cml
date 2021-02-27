package org.fir3.cml.api.model;

import java.util.*;

/**
 * A domain is a named group of models, annotations and type definitions that
 * form a logical unit.
 *
 * <p>
 *     If two domain instances <code>a</code> and <code>b</code> have an equal
 *     name, the same flags and consist of equal models, both domain instances
 *     are considered to be equal and both, <code>a.equals(b)</code> and
 *     <code>b.equals(a)</code>, must return <code>true</code>.
 * </p>
 */
public final class Domain {
    /**
     * The flags that may be specified for a domain.
     */
    public enum Flag {
        /**
         * The Ubiquitous flag indicates that the entities of the target domain
         * shall be visible to all other domains, even without importing them
         * in the first place.
         */
        Ubiquitous
    }

    private final String name;
    private final EnumSet<Flag> flags;
    private final Set<Model> models;

    /**
     * Initializes a new instance of <code>Domain</code>, which has the
     * specified <code>name</code> and <code>flags</code> and consists of the
     * specified <code>models</code>.
     *
     * @param name      The name of the new domain instance
     * @param flags     The flags of the new domain instance
     * @param models    The models of the new domain instance
     *
     * @throws  NullPointerException    If any passed parameter is
     *                                  <code>null</code>.
     */
    public Domain(
            String name,
            EnumSet<Flag> flags,
            Set<Model> models
    ) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(flags);
        Objects.requireNonNull(models);

        this.name = name;
        this.flags = EnumSet.copyOf(flags);
        this.models = Collections.unmodifiableSet(new HashSet<>(models));
    }

    /**
     * Returns the name of this domain.
     *
     * @return  The name of this domain.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the flags that have been specified for this domain.
     *
     * @return  The flags that have been specified for this domain.
     */
    public EnumSet<Flag> getFlags() {
        return EnumSet.copyOf(this.flags);
    }

    /**
     * Returns all models of this domain.
     *
     * @return  The models of this domain.
     */
    public Set<Model> getModels() {
        return this.models;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() ^
                this.flags.hashCode() ^
                this.models.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Domain) {
            Domain domain = (Domain) obj;

            return Objects.equals(this.name, domain.getName()) &&
                    Objects.equals(this.flags, domain.getFlags()) &&
                    Objects.equals(this.models, domain.getModels());
        }

        return false;
    }
}
