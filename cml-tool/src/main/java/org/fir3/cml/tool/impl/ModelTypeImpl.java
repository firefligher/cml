package org.fir3.cml.tool.impl;

import org.fir3.cml.api.model.Type;

public final class ModelTypeImpl implements Type.ModelType {
    private final String modelName;

    public ModelTypeImpl(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public String getModelName() {
        return this.modelName;
    }

    @Override
    public Category getCategory() {
        return Category.Model;
    }
}
