package org.fir3.cml.tool.impl;

import org.fir3.cml.api.model.TypeParameter;

public final class TypeParameterImpl implements TypeParameter {
    private final String name;

    public TypeParameterImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
