package org.fir3.cml.tool.impl;

import org.fir3.cml.api.model.Attribute;
import org.fir3.cml.api.model.Type;

public final class AttributeImpl implements Attribute {
    private final String name;
    private final Type type;

    public AttributeImpl(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Type getType() {
        return this.type;
    }
}
