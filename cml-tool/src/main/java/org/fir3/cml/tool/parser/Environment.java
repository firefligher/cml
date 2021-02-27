package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.TypeParameter;

import java.util.*;

/**
 * The environment that is visible to the parser that parses the next entity.
 */
final class Environment {
    public static final Environment EMPTY_ENVIRONMENT = new Environment(
            Collections.emptySet()
    );

    private final Set<TypeParameter> typeParameters;

    public Environment(Set<TypeParameter> typeParameters) {
        this.typeParameters = Collections.unmodifiableSet(new HashSet<>(
                typeParameters
        ));
    }

    public Set<TypeParameter> getTypeParameters() {
        return this.typeParameters;
    }

    public Environment extend(Collection<TypeParameter> additionalParameters) {
        Set<TypeParameter> typeParameters = new HashSet<>(this.typeParameters);
        typeParameters.addAll(additionalParameters);

        return new Environment(typeParameters);
    }
}
