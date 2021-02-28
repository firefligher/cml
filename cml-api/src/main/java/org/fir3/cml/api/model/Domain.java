package org.fir3.cml.api.model;

import org.fir3.cml.api.exception.CombinationException;

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
     *
     * @throws IllegalArgumentException If <code>models</code> contains two
     *                                  different model instances with the same
     *                                  name.
     */
    public Domain(
            String name,
            EnumSet<Flag> flags,
            Set<Model> models
    ) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(flags);
        Objects.requireNonNull(models);

        // Validating that there are no two model instances in models that have
        // the same name.

        if (models.stream().map(
                Model::getName
        ).distinct().count() != models.size()) {
            throw new IllegalArgumentException(
                    "Duplicate model name in specified models"
            );
        }

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

    /**
     * Combines this domain instance with the specified <code>domain</code>
     * instance and returns the resulting combined domain instance.
     *
     * Combining two different domain instances is only possible, if both
     * instances have the same name, the same flags and only declare models
     * with non-colliding names.
     *
     * @param domain    The other domain instance that will be combined with
     *                  this domain instance.
     *
     * @return  A new domain instance that has the same name and flags as both,
     *          this domain and the specified other <code>domain</code>
     *          instance, and consists of all models that the two original
     *          domain instances expose.
     *
     * @throws CombinationException If the name and/or flags of this domain
     *                              instance and the specified
     *                              <code>domain</code> instance do not match.
     */
    public Domain combine(Domain domain) throws CombinationException {
        // Validating that the name and the flags of both original domain
        // instances match

        if (!Objects.equals(this.name, domain.name)) {
            throw new CombinationException(
                    "The names of the two domain instances do not match"
            );
        }

        if (!Objects.equals(this.flags, domain.flags)) {
            throw new CombinationException(
                    "The flags of the two domain instances do not match"
            );
        }

        // Building a combined set of all models
        //
        // NOTE:    Although the Domain-constructor verifies that there are no
        //          models with the same name in a single domain instance, we
        //          also need to check for collisions here, because otherwise
        //          two equal models (of two different domain instances) would
        //          be merged into one, but redeclaration is not allowed in any
        //          form.

        Set<Model> models = new HashSet<>(this.models);

        for (Model model : domain.models) {
            if (models.add(model)) {
                continue;
            }

            throw new CombinationException(String.format(
                    "Both domains declare the model '%s'",
                    model.getName()
            ));
        }

        return new Domain(this.name, this.flags, models);
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
