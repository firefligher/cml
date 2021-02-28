package org.fir3.cml.api.model;

import org.fir3.cml.api.exception.CombinationException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void testConstructorModelCollisionCheck() {
        Set<Model> models = new HashSet<>();

        models.add(new Model(
                "CollidingModel",
                EnumSet.noneOf(Model.Flag.class),
                Collections.emptyList(),
                Collections.emptySet()
        ));

        models.add(new Model(
                "CollidingModel",
                EnumSet.of(Model.Flag.Builtin),
                Collections.emptyList(),
                Collections.emptySet()
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> new Domain(
                        "BadDomain",
                        EnumSet.noneOf(Domain.Flag.class),
                        models
                )
        );
    }

    @Test
    public void testCombine() throws CombinationException {
        Model firstModel = new Model(
                "FirstModel",
                EnumSet.noneOf(Model.Flag.class),
                Collections.emptyList(),
                Collections.emptySet()
        );

        Domain domain1 = new Domain(
                "PartialDomain",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.singleton(firstModel)
        );

        Model secondModel = new Model(
                "SecondModel",
                EnumSet.noneOf(Model.Flag.class),
                Collections.emptyList(),
                Collections.emptySet()
        );

        Domain domain2 = new Domain(
                "PartialDomain",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.singleton(secondModel)
        );

        // Combination

        Domain combinedDomain1 = domain1.combine(domain2);
        Domain combinedDomain2 = domain2.combine(domain1);

        // Assert commutativity

        assertEquals(combinedDomain1, combinedDomain2);

        // Assert the properties of the new domain

        assertEquals("PartialDomain", combinedDomain1.getName());
        assertTrue(combinedDomain1.getFlags().isEmpty());

        Set<Model> combinedModels = combinedDomain1.getModels();
        assertTrue(combinedModels.contains(firstModel));
        assertTrue(combinedModels.contains(secondModel));
    }

    @Test
    public void testCombineDifferentName() {
        Domain domain1 = new Domain(
                "domain.first",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.emptySet()
        );

        Domain domain2 = new Domain(
                "domain.second",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.emptySet()
        );

        assertThrows(
                CombinationException.class,
                () -> domain1.combine(domain2)
        );
    }

    @Test
    public void testCombineDifferentFlags() {
        Domain domain1 = new Domain(
                "domain.simple",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.emptySet()
        );

        Domain domain2 = new Domain(
                "domain.simple",
                EnumSet.of(Domain.Flag.Ubiquitous),
                Collections.emptySet()
        );

        assertThrows(
                CombinationException.class,
                () -> domain1.combine(domain2)
        );
    }

    @Test
    public void testCombineEqualModels() {
        Domain domain1 = new Domain(
                "domain.equal",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.singleton(new Model(
                        "EqualModel",
                        EnumSet.noneOf(Model.Flag.class),
                        Collections.emptyList(),
                        Collections.emptySet()
                ))
        );

        Domain domain2 = new Domain(
                "domain.equal",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.singleton(new Model(
                        "EqualModel",
                        EnumSet.noneOf(Model.Flag.class),
                        Collections.emptyList(),
                        Collections.emptySet()
                ))
        );

        assertThrows(
                CombinationException.class,
                () -> domain1.combine(domain2)
        );
    }
}
