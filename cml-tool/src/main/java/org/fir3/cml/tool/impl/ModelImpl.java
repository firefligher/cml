package org.fir3.cml.tool.impl;

import org.fir3.cml.api.model.Attribute;
import org.fir3.cml.api.model.TypeParameter;
import org.fir3.cml.api.model.Model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public final class ModelImpl implements Model {
    private final String name;
    private final EnumSet<Flag> flags;
    private final Set<TypeParameter> typeParameters;
    private final Set<Attribute> attributes;

    public ModelImpl(
            String name,
            EnumSet<Flag> flags,
            Set<TypeParameter> typeParameters,
            Set<Attribute> attributes
    ) {
        this.name = name;
        this.flags = EnumSet.copyOf(flags);
        this.typeParameters = Collections.unmodifiableSet(
                new HashSet<>(typeParameters)
        );

        this.attributes = Collections.unmodifiableSet(
                new HashSet<>(attributes)
        );
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public EnumSet<Flag> getFlags() {
        return EnumSet.copyOf(this.flags);
    }

    @Override
    public Set<TypeParameter> getTypeParameters() {
        return this.typeParameters;
    }

    @Override
    public Set<Attribute> getAttributes() {
        return this.attributes;
    }
}
