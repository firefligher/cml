package org.fir3.cml.api.model;

import java.util.Set;

/**
 * An environment is a set of different domains that may depend on each other.
 */
public interface Environment {
    /**
     * Returns all domains of this environment.
     *
     * @return  All domains of this environment.
     */
    Set<Domain> getDomains();
}
