package org.fir3.cml.api.model;

/**
 * A type parameter is a placeholder for an actual type that may be specified
 * at the definition of a model and then may be used for specifying types
 * attributes inside that particular model.
 */
public interface TypeParameter {
    /**
     * Returns the name of this type parameter.
     *
     * @return  The name of this type parameter.
     */
    String getName();
}
