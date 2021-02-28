package org.fir3.cml.api.util;

import org.fir3.cml.api.model.Domain;
import org.fir3.cml.api.model.Environment;
import org.fir3.cml.api.model.Model;

import java.util.Objects;
import java.util.Optional;

/**
 * A collection of utility methods for model instances.
 */
public final class ModelHelper {
    /**
     * Returns the (unique) fully qualified name of the specified
     * <code>model</code>.
     *
     * @param domain    The domain of the specified <code>model</code>.
     * @param model     The model whose name will be returned.
     *
     * @return  The (unique) fully qualified name of the specified
     *          <code>model</code>.
     *
     * @throws NullPointerException If one of the parameters is
     *                              <code>null</code>.
     */
    public static String toString(Domain domain, Model model) {
        Objects.requireNonNull(domain, "domain is null");
        Objects.requireNonNull(model, "model is null");

        return String.format("%s.%s", domain.getName(), model.getName());
    }

    /**
     * Resolves the corresponding model instance from its
     * <code>fullyQualifiedModelName</code>.
     *
     * @param fullyQualifiedModelName   The (unique) fully qualified name of
     *                                  the model that will be resolved from
     *                                  the specified <code>environment</code>.
     *
     * @param environment               The environment that contains the model
     *                                  that will be resolved.
     *
     * @return  An {@link Optional} container that either contains the resolved
     *          model instance, or <code>null</code>, if there is no model with
     *          the specified <code>fullyQualifiedModelName</code>.
     *
     * @throws NullPointerException If one of the parameters is
     *                              <code>null</code>.
     */
    public static Optional<Pair<Domain, Model>> fromString(
            String fullyQualifiedModelName,
            Environment environment
    ) {
        Objects.requireNonNull(
                fullyQualifiedModelName,
                "fullyQualifiedModelName is null"
        );

        Objects.requireNonNull(environment, "environment is null");

        return environment.resolveModel(fullyQualifiedModelName, null);
    }

    private ModelHelper() {
        throw new IllegalStateException("Do not instantiate this class");
    }
}
