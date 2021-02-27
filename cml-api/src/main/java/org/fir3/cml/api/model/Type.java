package org.fir3.cml.api.model;

/**
 * A type specifies the way that a corresponding value shall be interpreted.
 *
 * <p>
 *     If two instances <code>a</code> and <code>b</code> of <code>Type</code>
 *     belong to the same category and have the same category-specific details,
 *     both instances are considered to be equal and both,
 *     <code>a.equals(b)</code> and <code>b.equals(a)</code>, must return
 *     <code>true</code>.
 * </p>
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
     * Returns the category of this type.
     *
     * @return  The category of this type
     */
    Category getCategory();
}
