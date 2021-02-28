package org.fir3.cml.api.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An environment is a set of different domains that may depend on each other.
 *
 * <p>
 *     If two environment instances <code>a</code> and <code>b</code> consist
 *     of the same domains, both domains are considered to be equal and both,
 *     <code>a.equals(b)</code> and <code>b.equals(a)</code>, must return
 *     <code>true</code>.
 * </p>
 */
public final class Environment {
    private final Set<Domain> domains;

    /**
     * Initializes a new instance of <code>Environment</code> that consists of
     * the specified <code>domains</code>.
     *
     * @param domains   The domains that the new environment instance consists
     *                  of
     *
     * @throws NullPointerException     If <code>domains</code> is
     *                                  <code>null</code>.
     *
     * @throws IllegalArgumentException If the specified <code>domains</code>
     *                                  set contains different domain instances
     *                                  with the same name.
     */
    public Environment(Set<Domain> domains) {
        Objects.requireNonNull(domains, "domains is null");

        // Validating that there are no colliding domain names in the domains
        // set

        if (domains.stream().map(
                Domain::getName
        ).distinct().count() != domains.size()) {
            throw new IllegalArgumentException(
                    "Colliding domain instances in domains set"
            );
        }

        this.domains = Collections.unmodifiableSet(new HashSet<>(domains));
    }

    /**
     * Returns all domains of this environment.
     *
     * @return  All domains of this environment.
     */
    public Set<Domain> getDomains() {
        return this.domains;
    }

    @Override
    public int hashCode() {
        return this.domains.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Environment) {
            Environment env = (Environment) obj;

            return Objects.equals(this.domains, env.getDomains());
        }

        return false;
    }
}
