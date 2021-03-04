package org.fir3.cml.impl.java.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An implementation of the {@link JavaType} interface, which represents a
 * {@link org.fir3.cml.impl.java.type.JavaType.Category#ClassType}.
 */
public final class ClassType implements JavaType {
    private final String className;
    private final List<JavaType> typeParameters;

    public ClassType(String className, List<JavaType> typeParameters) {
        Objects.requireNonNull(className);
        Objects.requireNonNull(typeParameters);

        this.className = className;
        this.typeParameters = Collections.unmodifiableList(new ArrayList<>(
                typeParameters
        ));
    }

    @Override
    public Category getCategory() {
        return Category.ClassType;
    }

    public String getClassName() {
        return this.className;
    }

    public List<JavaType> getTypeParameters() {
        return this.typeParameters;
    }

    @Override
    public int hashCode() {
        return Category.ClassType.hashCode() ^
                this.className.hashCode() ^
                this.typeParameters.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassType) {
            ClassType classType = (ClassType) obj;

            return Objects.equals(this.className, classType.className) &&
                    Objects.equals(
                            this.typeParameters,
                            classType.typeParameters
                    );
        }

        return false;
    }
}
