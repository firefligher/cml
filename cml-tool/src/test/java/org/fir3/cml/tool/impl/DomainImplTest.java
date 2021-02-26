package org.fir3.cml.tool.impl;

import org.fir3.cml.api.model.*;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DomainImplTest {
    private static final Model TEST_MODEL_1 = new ModelImpl(
            "TestModel1",
            EnumSet.noneOf(Model.Flag.class),
            Collections.emptyList(),
            Collections.emptySet()
    );

    private static final Model TEST_MODEL_2 = new ModelImpl(
            "TestModel2",
            EnumSet.noneOf(Model.Flag.class),
            Collections.emptyList(),
            Collections.emptySet()
    );

    @Test
    public void testCopyInConstructor() {
        // Setting up the things that we need to instantiate Domain

        EnumSet<Domain.Flag> flags = EnumSet.of(Domain.Flag.Ubiquitous);

        Set<Model> models = new HashSet<>();
        models.add(DomainImplTest.TEST_MODEL_1);

        // Instantiating Domain

        Domain domain = new DomainImpl(
                "org.example.test",
                flags,
                models
        );

        // Modifying the objects that we have passed to the Domain constructor

        flags.remove(Domain.Flag.Ubiquitous);

        models.remove(DomainImplTest.TEST_MODEL_1);
        models.add(DomainImplTest.TEST_MODEL_2);

        // The Domain instance must not have changed.

        EnumSet<Domain.Flag> actualFlags = domain.getFlags();
        assertEquals(1, actualFlags.size());
        assertTrue(actualFlags.contains(Domain.Flag.Ubiquitous));

        Set<Model> actualModels = domain.getModels();
        assertEquals(1, actualModels.size());
        assertTrue(actualModels.contains(DomainImplTest.TEST_MODEL_1));
    }

    @Test
    public void testUnmodifiability() {
        Domain domain = new DomainImpl(
                "org.example.test",
                EnumSet.of(Domain.Flag.Ubiquitous),
                Collections.singleton(DomainImplTest.TEST_MODEL_1)
        );

        // Modifying the new Domain instance by modifying the collections that
        // we can obtain via its getters should not be possible.

        // Flags

        EnumSet<Domain.Flag> flags = domain.getFlags();

        try {
            flags.remove(Domain.Flag.Ubiquitous);
        } catch (Throwable ignored) {
            // Not mandatory for this test.
        }

        EnumSet<Domain.Flag> actualFlags = domain.getFlags();
        assertEquals(1, actualFlags.size());
        assertTrue(actualFlags.contains(Domain.Flag.Ubiquitous));

        // Models

        Set<Model> models = domain.getModels();

        try {
            models.remove(DomainImplTest.TEST_MODEL_1);
            models.add(DomainImplTest.TEST_MODEL_2);
        } catch (Throwable ignored) {
            // Not mandatory for this test.
        }

        Set<Model> actualModels = domain.getModels();
        assertEquals(1, actualModels.size());
        assertTrue(actualModels.contains(DomainImplTest.TEST_MODEL_1));
    }
}
