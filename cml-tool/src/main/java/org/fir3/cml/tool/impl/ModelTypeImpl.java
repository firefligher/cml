package org.fir3.cml.tool.impl;

import org.fir3.cml.api.model.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModelTypeImpl implements Type.ModelType {
    private final String modelName;
    private final List<Type> typeParameters;

    public ModelTypeImpl(String modelName, List<Type> typeParameters) {
        this.modelName = modelName;
        this.typeParameters = Collections.unmodifiableList(new ArrayList<>(
                typeParameters
        ));
    }

    @Override
    public String getModelName() {
        return this.modelName;
    }

    @Override
    public List<Type> getTypeParameters() {
        return this.typeParameters;
    }

    @Override
    public Category getCategory() {
        return Category.Model;
    }
}
