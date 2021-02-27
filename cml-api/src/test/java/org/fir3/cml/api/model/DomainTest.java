package org.fir3.cml.api.model;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DomainTest {
    private static final Model TEST_MODEL_1 = new Model(
            "TestModel1",
            EnumSet.noneOf(Model.Flag.class),
            Collections.emptyList(),
            Collections.emptySet()
    );

    private static final Model TEST_MODEL_2 = new Model(
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
        models.add(DomainTest.TEST_MODEL_1);

        // Instantiating Domain

        Domain domain = new Domain(
                "org.example.test",
                flags,
                models
        );

        // Modifying the objects that we have passed to the Domain constructor

        flags.remove(Domain.Flag.Ubiquitous);

        models.remove(DomainTest.TEST_MODEL_1);
        models.add(DomainTest.TEST_MODEL_2);

        // The Domain instance must not have changed.

        EnumSet<Domain.Flag> actualFlags = domain.getFlags();
        assertEquals(1, actualFlags.size());
        assertTrue(actualFlags.contains(Domain.Flag.Ubiquitous));

        Set<Model> actualModels = domain.getModels();
        assertEquals(1, actualModels.size());
        assertTrue(actualModels.contains(DomainTest.TEST_MODEL_1));
    }

    @Test
    public void testUnmodifiability() {
        Domain domain = new Domain(
                "org.example.test",
                EnumSet.of(Domain.Flag.Ubiquitous),
                Collections.singleton(DomainTest.TEST_MODEL_1)
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
            models.remove(DomainTest.TEST_MODEL_1);
            models.add(DomainTest.TEST_MODEL_2);
        } catch (Throwable ignored) {
            // Not mandatory for this test.
        }

        Set<Model> actualModels = domain.getModels();
        assertEquals(1, actualModels.size());
        assertTrue(actualModels.contains(DomainTest.TEST_MODEL_1));
    }

    @Test
    public void testEqualsAndHashCode() {
        Domain domain1 = new Domain(
                "test_domain",
                EnumSet.of(Domain.Flag.Ubiquitous),
                Collections.singleton(new Model(
                        "TestModel1",
                        EnumSet.noneOf(Model.Flag.class),
                        Collections.emptyList(),
                        Collections.emptySet()
                ))
        );

        Domain domain2 = new Domain(
                "test_domain",
                EnumSet.of(Domain.Flag.Ubiquitous),
                Collections.singleton(new Model(
                        "TestModel1",
                        EnumSet.noneOf(Model.Flag.class),
                        Collections.emptyList(),
                        Collections.emptySet()
                ))
        );

        assertEquals(domain1, domain2);
        assertEquals(domain2, domain1);
        assertEquals(domain1.hashCode(), domain2.hashCode());
    }
}
