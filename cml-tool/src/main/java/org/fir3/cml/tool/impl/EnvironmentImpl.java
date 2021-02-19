package org.fir3.cml.tool.impl;

import org.fir3.cml.api.model.Domain;
import org.fir3.cml.api.model.Environment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class EnvironmentImpl implements Environment {
    private final Set<Domain> domains;

    public EnvironmentImpl(Set<Domain> domains) {
        this.domains = Collections.unmodifiableSet(new HashSet<>(domains));
    }

    @Override
    public Set<Domain> getDomains() {
        return this.domains;
    }
}
