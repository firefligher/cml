package org.fir3.cml.api.model;

import org.fir3.cml.api.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An environment is a set of different domains that may depend on each other.
 *
 * <p>
 *     If two environment instances <code>a</code> and <code>b</code> consist
 *     of the same domains, both domains are considered to be equal and both,
 *     <code>a.equals(b)</code> and <code>b.equals(a)</code>, must return
 *     <code>true</code>.
 * </p>
 */
public final class Environment {
    private static Optional<Pair<Domain, Model>> directlyResolveModel(
            Domain domain,
            String modelName
    ) {
        return domain.resolveModel(modelName).map(m -> new Pair<>(domain, m));
    }

    private final Set<Domain> domains;

    /**
     * Initializes a new instance of <code>Environment</code> that consists of
     * the specified <code>domains</code>.
     *
     * @param domains   The domains that the new environment instance consists
     *                  of
     *
     * @throws NullPointerException     If <code>domains</code> is
     *                                  <code>null</code>.
     *
     * @throws IllegalArgumentException Either if the specified
     *                                  <code>domains</code> set contains
     *                                  different domain instances with the
     *                                  same name, or if the same set contains
     *                                  multiple ubiquitous domains that
     *                                  declare at least one model with the
     *                                  same name.
     */
    public Environment(Set<Domain> domains) {
        Objects.requireNonNull(domains, "domains is null");

        // Validating that there are no colliding domain names in the domains
        // set

        if (domains.stream().map(
                Domain::getName
        ).distinct().count() != domains.size()) {
            throw new IllegalArgumentException(
                    "Colliding domain instances in domains set"
            );
        }

        // Validating that there are no colliding model names of ubiquitous
        // domains

        List<Model> ubiquitousModels = domains.stream()
                .filter(d -> d.getFlags().contains(Domain.Flag.Ubiquitous))
                .flatMap(d -> d.getModels().stream())
                .collect(Collectors.toList());

        if (ubiquitousModels.stream().map(
                Model::getName
        ).distinct().count() != ubiquitousModels.size()) {
            throw new IllegalArgumentException(
                    "Colliding models of ubiquitous domains"
            );
        }

        this.domains = Collections.unmodifiableSet(new HashSet<>(domains));
    }

    /**
     * Returns all domains of this environment.
     *
     * @return  All domains of this environment.
     */
    public Set<Domain> getDomains() {
        return this.domains;
    }

    /**
     * Resolves the domain instance with the specified <code>name</code> from
     * this environment.
     *
     * @param name  The name of the domain that will be resolved.
     *
     * @return  An {@link Optional} container that either contains the domain
     *          instance with the specified <code>name</code>, or
     *          <code>null</code>, if there is no such domain instance.
     *
     * @throws NullPointerException If <code>name</code> is <code>null</code>.
     */
    public Optional<Domain> resolveDomain(String name) {
        Objects.requireNonNull(name, "name is null");

        return this.domains.stream()
                .filter(d -> Objects.equals(name, d.getName()))
                .findAny();
    }

    /**
     * Resolves the domain instance and the model instance from the model's
     * name <code>name</code>.
     *
     * @param name      The name of the model that will be resolved.
     *                  This name may be prefixed with the name of the domain,
     *                  that the requested model was defined in, and a
     *                  separating dot.
     *
     * @param context   The context from which the resolution request
     *                  originates.
     *                  If the specified model <code>name</code> is not
     *                  prefixed with its domain name, the <code>context</code>
     *                  may have an impact on the result, as models of the same
     *                  domain have a higher resolution priority than models of
     *                  ubiquitous domains.
     *                  The value of this parameter may be <code>null</code>,
     *                  in case there is no better choice.
     *
     * @return  An {@link Optional} container that either contains the
     *          corresponding domain-model-pair for the specified
     *          <code>name</code>, or <code>null</code>, if there is no domain
     *          in this environment, that provides a model instance with the
     *          specified <code>name</code>.
     *
     * @throws NullPointerException     If <code>name</code> is
     *                                  <code>null</code>.
     *
     * @throws IllegalArgumentException If <code>name</code> starts or ends
     *                                  with a dot.
     */
    public Optional<Pair<Domain, Model>> resolveModel(
            String name,
            Domain context
    ) {
        Objects.requireNonNull(name, "name is null");

        if (name.startsWith(".") || name.endsWith(".")) {
            throw new IllegalArgumentException(
                    "name starts and/or ends with a dot"
            );
        }

        // If name contains at least one dot, we assume that it is the fully
        // qualified name of the requested model. In this case, we need to
        // split the name into its domain name part and into its model name
        // part.

        int lastDotIndex = name.lastIndexOf('.');

        if (lastDotIndex != -1) {
            String domainName = name.substring(0, lastDotIndex);
            String modelName = name.substring(lastDotIndex + 1);

            // Resolving the domain of the requested model by its name. If no
            // such domain exists, the requested model cannot exist either and
            // we're done.
            // Otherwise, we resolve the model directly from its domain.

            return this.resolveDomain(domainName)
                    .flatMap(d -> directlyResolveModel(d, modelName));
        }

        // First, we attempt to resolve the model from the specified context.
        // If the context lacks a model instance with the specified name, we
        // try to resolve the model from the ubiquitous domains.

        Optional<Pair<Domain, Model>> nullableModel =
                Optional.ofNullable(context)
                        .flatMap(d -> directlyResolveModel(d, name));

        if (nullableModel.isPresent()) {
            return nullableModel;
        }

        return this.domains.stream()
                .filter(d -> d.getFlags().contains(Domain.Flag.Ubiquitous))
                .map(d -> directlyResolveModel(d, name))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();
    }

    @Override
    public int hashCode() {
        return this.domains.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Environment) {
            Environment env = (Environment) obj;

            return Objects.equals(this.domains, env.getDomains());
        }

        return false;
    }
}
