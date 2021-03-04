package org.fir3.cml.impl.java.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link JavaType} interface, which represents a
 * {@link org.fir3.cml.impl.java.type.JavaType.Category#ClassType}.
 */
public final class ClassType implements JavaType {
    private final String fqClassName;
    private final List<JavaType> typeParameters;

    public ClassType(String fqClassName, List<JavaType> typeParameters) {
        Objects.requireNonNull(fqClassName);
        Objects.requireNonNull(typeParameters);

        this.fqClassName = fqClassName;
        this.typeParameters = Collections.unmodifiableList(new ArrayList<>(
                typeParameters
        ));
    }

    @Override
    public Category getCategory() {
        return Category.ClassType;
    }

    @Override
    public String toJavaType() {
        if (this.typeParameters.isEmpty()) {
            return this.fqClassName;
        }

        return String.format(
                "%s<%s>",
                this.fqClassName,
                this.typeParameters.stream()
                        .map(JavaType::toJavaType)
                        .collect(Collectors.joining(", "))
        );
    }

    public String getFullyQualifiedClassName() {
        return this.fqClassName;
    }

    public List<JavaType> getTypeParameters() {
        return this.typeParameters;
    }

    @Override
    public int hashCode() {
        return Category.ClassType.hashCode() ^
                this.fqClassName.hashCode() ^
                this.typeParameters.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassType) {
            ClassType classType = (ClassType) obj;

            return Objects.equals(this.fqClassName, classType.fqClassName) &&
                    Objects.equals(
                            this.typeParameters,
                            classType.typeParameters
                    );
        }

        return false;
    }
}
