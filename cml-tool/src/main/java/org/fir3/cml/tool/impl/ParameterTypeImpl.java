package org.fir3.cml.tool.impl;

import org.fir3.cml.api.model.Type;

public final class ParameterTypeImpl implements Type.ParameterType {
    private final String typeParameterName;

    public ParameterTypeImpl(String typeParameterName) {
        this.typeParameterName = typeParameterName;
    }

    @Override
    public String getTypeParameterName() {
        return this.typeParameterName;
    }

    @Override
    public Category getCategory() {
        return Category.Parameter;
    }
}
