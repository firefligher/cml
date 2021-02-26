package org.fir3.cml.api.model;

import java.util.List;

/**
 * A type specifies the way that a corresponding value shall be interpreted.
 */
public interface Type {
    /**
     * The category of a type specifies whether the type was directly derived
     * from a defined model or if depends on the value of a type parameter.
     */
    enum Category {
        /**
         * The type was directly derived from a defined model.
         */
        Model,

        /**
         * The type depends on the value of a type parameter.
         */
        Parameter
    }

    /**
     * A model type is directly derived from a defined model and must always
     * fill all type parameters of this model.
     */
    interface ModelType extends Type {
        /**
         * Returns the name of the model that this type was derived from.
         *
         * @return  The name of the model that this type was derived from.
         */
        String getModelName();

        /**
         * Returns the list of types that fill the type parameters of the model
         * that is referenced by this instance.
         *
         * @return  The list of types that fill the type parameters of the
         *          referenced model
         */
        List<Type> getTypeParameters();
    }

    /**
     * A parameter type depends on the value of a type parameter.
     */
    interface ParameterType extends Type {
        /**
         * Returns the name of the type parameter that this type depends on.
         *
         * @return  The name of the type parameter that this type depends on.
         */
        String getTypeParameterName();
    }

    /**
     * Returns the category of this type.
     *
     * @return  The category of this type
     */
    Category getCategory();
}
