package org.fir3.cml.api.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * A domain is a named group of models, annotations and type definitions that
 * form a logical unit.
 */
public interface Domain {
    /**
     * The flags that may be specified for a domain.
     */
    enum Flag {
        /**
         * The Ubiquitous flag indicates that the entities of the target domain
         * shall be visible to all other domains, even without importing them
         * in the first place.
         */
        Ubiquitous
    }

    /**
     * Returns the name of this domain.
     *
     * @return  The name of this domain.
     */
    String getName();

    /**
     * Returns the flags that have been specified for this domain.
     *
     * @return  The flags that have been specified for this domain.
     */
    EnumSet<Flag> getFlags();

    /**
     * Returns all models of this domain.
     *
     * @return  The models of this domain.
     */
    Set<Model> getModels();
}
