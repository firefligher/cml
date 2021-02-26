package org.fir3.cml.api.model;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * A model is a named structure of attributes.
 */
public interface Model {
    /**
     * The flags that may be specified for a model.
     */
    enum Flag {
        /**
         * Indicates that the target model is provided by the compiler.
         */
        Builtin
    }

    /**
     * Returns the name of this model.
     *
     * @return  The name of this model.
     */
    String getName();

    /**
     * Returns the flags that have been specified for this model.
     *
     * @return  The flags that have been specified for this model.
     */
    EnumSet<Flag> getFlags();

    /**
     * Returns the type parameters that are valid for this model.
     *
     * @return  The type parameters of this model.
     */
    List<TypeParameter> getTypeParameters();

    /**
     * Returns the attributes that this model consists of.
     *
     * @return  The attributes that this model consists of.
     */
    Set<Attribute> getAttributes();
}
