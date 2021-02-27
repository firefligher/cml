package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Domain;

import java.util.EnumSet;

/**
 * This class represents the initial domain declaration of a CML-file.
 */
final class DomainDeclaration {
    private final String name;
    private final EnumSet<Domain.Flag> flags;

    /**
     * Initializes a new <code>DomainDeclaration</code> that represents the
     * domain of the passed <code>name</code> with the provided
     * <code>flags</code>.
     *
     * @param name  The name of the domain that is declared by this
     *              declaration.
     *
     * @param flags The flags of the declared domain.
     */
    public DomainDeclaration(String name, EnumSet<Domain.Flag> flags) {
        this.name = name;
        this.flags = EnumSet.copyOf(flags);
    }

    /**
     * Returns the name of the declared domain.
     *
     * @return  The name of the declared domain.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the flags of the declared domain.
     *
     * @return  The flags of the declared domain.
     */
    public EnumSet<Domain.Flag> getFlags() {
        return EnumSet.copyOf(this.flags);
    }
}
