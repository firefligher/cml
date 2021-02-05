package org.fir3.cml.api.model;

/**
 * An attribute is a property of a model that has a name and a type.
 */
public interface Attribute {
    /**
     * Returns the name of this attribute.
     *
     * @return  The attribute's name
     */
    String getName();

    /**
     * Returns the type of this attribute.
     *
     * @return  The attribute's type
     */
    Type getType();
}
