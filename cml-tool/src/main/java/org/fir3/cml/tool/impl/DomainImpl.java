package org.fir3.cml.tool.impl;

import org.fir3.cml.api.model.Domain;
import org.fir3.cml.api.model.Model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public final class DomainImpl implements Domain {
    private final String name;
    private final EnumSet<Flag> flags;
    private final Set<Model> models;

    public DomainImpl(
            String name,
            EnumSet<Flag> flags,
            Set<Model> models
    ) {
        this.name = name;
        this.flags = EnumSet.copyOf(flags);
        this.models = Collections.unmodifiableSet(new HashSet<>(models));
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
    public Set<Model> getModels() {
        return this.models;
    }
}
