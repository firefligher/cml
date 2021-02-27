package org.fir3.cml.api.model;

import java.util.*;

/**
 * A model is a named structure of attributes.
 *
 * <p>
 *     If two model instances <code>a</code> and <code>b</code> have the same
 *     name, flags, type parameters and consist of the same attributes, both
 *     instances are considered to be equal and both, <code>a.equals(b)</code>
 *     and <code>b.equals(a)</code>, must return <code>true</code>.
 * </p>
 */
public final class Model {
    /**
     * The flags that may be specified for a model.
     */
    public enum Flag {
        /**
         * Indicates that the target model is provided by the compiler.
         */
        Builtin
    }

    private final String name;
    private final EnumSet<Flag> flags;
    private final List<TypeParameter> typeParameters;
    private final Set<Attribute> attributes;

    /**
     * Initializes a new instance of <code>Model</code>.
     *
     * @param name              The name of the new model instance
     * @param flags             The flags of the new model instance
     * @param typeParameters    The type parameters of the new model instance
     * @param attributes        The attributes of the new model instance
     *
     * @throws NullPointerException If any of the passed parameters is
     *                              <code>null</code>
     */
    public Model(
            String name,
            EnumSet<Flag> flags,
            List<TypeParameter> typeParameters,
            Set<Attribute> attributes
    ) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(flags);
        Objects.requireNonNull(typeParameters);
        Objects.requireNonNull(attributes);

        this.name = name;
        this.flags = EnumSet.copyOf(flags);
        this.typeParameters = Collections.unmodifiableList(
                new ArrayList<>(typeParameters)
        );

        this.attributes = Collections.unmodifiableSet(
                new HashSet<>(attributes)
        );
    }

    /**
     * Returns the name of this model.
     *
     * @return  The name of this model.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the flags that have been specified for this model.
     *
     * @return  The flags that have been specified for this model.
     */
    public EnumSet<Flag> getFlags() {
        return EnumSet.copyOf(this.flags);
    }

    /**
     * Returns the type parameters that are valid for this model.
     *
     * @return  The type parameters of this model.
     */
    public List<TypeParameter> getTypeParameters() {
        return this.typeParameters;
    }

    /**
     * Returns the attributes that this model consists of.
     *
     * @return  The attributes that this model consists of.
     */
    public Set<Attribute> getAttributes() {
        return this.attributes;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() ^
                this.flags.hashCode() ^
                this.typeParameters.hashCode() ^
                this.attributes.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Model) {
            Model model = (Model) obj;

            return Objects.equals(this.name, model.getName()) &&
                    Objects.equals(this.flags, model.getFlags()) &&
                    Objects.equals(
                            this.typeParameters,
                            model.getTypeParameters()
                    ) &&
                    Objects.equals(this.attributes, model.getAttributes());
        }

        return false;
    }
}
