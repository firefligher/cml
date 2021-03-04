package org.fir3.cml.impl.java.type;

import java.util.Objects;

/**
 * An implementation of the {@link JavaType} interface, which represents a
 * {@link org.fir3.cml.impl.java.type.JavaType.Category#TypeVariable}.
 */
public final class TypeVariableType implements JavaType {
    private final String name;

    public TypeVariableType(String name) {
        this.name = name;
    }

    @Override
    public Category getCategory() {
        return Category.TypeVariable;
    }

    @Override
    public String toJavaType() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return Category.TypeVariable.hashCode() ^ this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypeVariableType) {
            TypeVariableType tvType = (TypeVariableType) obj;

            return Objects.equals(this.name, tvType.name);
        }

        return false;
    }
}
