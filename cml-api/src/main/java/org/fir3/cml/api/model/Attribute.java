package org.fir3.cml.api.model;

import java.util.Objects;

/**
 * An attribute is a property of a model that has a name and a type.
 *
 * <p>
 *     If two attribute instances <code>a</code> and <code>b</code> have an
 *     equal name and an equal type, both instances are considered to be equal
 *     and <code>a.equals(b)</code>, respectively <code>b.equals(a)</code>,
 *     must return <code>true</code>.
 * </p>
 */
public final class Attribute {
    private final String name;
    private final Type type;

    /**
     * Initializes a new instance of <code>Attribute</code> that represents an
     * attribute with the specified <code>name</code> and the specified
     * <code>type</code>.
     *
     * @param name  The name of the new attribute
     * @param type  The type of the new attribute
     *
     * @throws NullPointerException If the value of any passed parameter is
     *                              <code>null</code>.
     */
    public Attribute(String name, Type type) {
        Objects.requireNonNull(name, "name is null");
        Objects.requireNonNull(type, "type is null");

        this.name = name;
        this.type = type;
    }

    /**
     * Returns the name of this attribute.
     *
     * @return  The attribute's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the type of this attribute.
     *
     * @return  The attribute's type
     */
    public Type getType() {
        return this.type;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() ^ this.type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Attribute) {
            Attribute attr = (Attribute) obj;

            return Objects.equals(this.name, attr.name) &&
                    Objects.equals(this.type, attr.type);
        }

        return false;
    }
}
