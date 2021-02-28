package org.fir3.cml.api.model;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EnvironmentTest {
    private static final Domain TEST_DOMAIN_1 = new Domain(
            "test.domain1",
            EnumSet.noneOf(Domain.Flag.class),
            Collections.emptySet()
    );

    private static final Domain TEST_DOMAIN_2 = new Domain(
            "test.domain2",
            EnumSet.of(Domain.Flag.Ubiquitous),
            Collections.emptySet()
    );

    @Test
    public void testCopyInConstructor() {
        // Setting up a set that can modify we later on

        Set<Domain> domains = new HashSet<>();
        domains.add(EnvironmentTest.TEST_DOMAIN_1);

        // Creating the environment

        Environment env = new Environment(domains);

        // Modifying the domain set

        domains.remove(EnvironmentTest.TEST_DOMAIN_1);
        domains.add(EnvironmentTest.TEST_DOMAIN_2);

        // Asserting that the env still provides the old state of the set.

        Set<Domain> actualDomains = env.getDomains();
        assertEquals(1, actualDomains.size());
        assertTrue(actualDomains.contains(EnvironmentTest.TEST_DOMAIN_1));
    }

    @Test
    public void testUnmodifiability() {
        // Setting up a new environment

        Environment env = new Environment(Collections.singleton(
                EnvironmentTest.TEST_DOMAIN_1
        ));

        // Attempting to adjust the domain set.

        Set<Domain> domains = env.getDomains();

        try {
            domains.remove(EnvironmentTest.TEST_DOMAIN_1);
            domains.add(EnvironmentTest.TEST_DOMAIN_2);
        } catch (Throwable ignored) {
            // Not mandatory for this test.
        }

        // Asserting that nothing changed inside the Environment instance.

        Set<Domain> actualDomains = env.getDomains();
        assertEquals(1, actualDomains.size());
        assertTrue(actualDomains.contains(EnvironmentTest.TEST_DOMAIN_1));
    }

    @Test
    public void testEqualsAndHashCode() {
        Environment env1 = new Environment(new HashSet<>(
                Collections.singletonList(new Domain(
                        "test_domain",
                        EnumSet.of(Domain.Flag.Ubiquitous),
                        Collections.emptySet()
                ))
        ));

        Environment env2 = new Environment(new HashSet<>(
                Collections.singletonList(new Domain(
                        "test_domain",
                        EnumSet.of(Domain.Flag.Ubiquitous),
                        Collections.emptySet()
                ))
        ));

        assertEquals(env1, env2);
        assertEquals(env2, env1);
        assertEquals(env1.hashCode(), env2.hashCode());
    }

    @Test
    public void testConstructorCollidingDomainCheck() {
        Set<Domain> domains = new HashSet<>();

        domains.add(new Domain(
                "CollidingDomain",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.singleton(new Model(
                        "FirstModel",
                        EnumSet.noneOf(Model.Flag.class),
                        Collections.emptyList(),
                        Collections.emptySet()
                ))
        ));

        domains.add(new Domain(
                "CollidingDomain",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.singleton(new Model(
                        "SecondModel",
                        EnumSet.noneOf(Model.Flag.class),
                        Collections.emptyList(),
                        Collections.emptySet()
                ))
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> new Environment(domains)
        );
    }

    @Test
    public void testResolveDomain() {
        // Preparing the test environment

        Set<Domain> domains = new HashSet<>();

        domains.add(new Domain(
                "org.example.domain1",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.emptySet()
        ));

        domains.add(new Domain(
                "org.example.domain2",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.emptySet()
        ));

        domains.add(new Domain(
                "org.example.__requested__",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.emptySet()
        ));

        Environment env = new Environment(domains);

        // Looking up the different domains

        Optional<Domain> nullableDomain1 = env.resolveDomain(
                "org.example.domain1"
        );

        Optional<Domain> nullableDomain2 = env.resolveDomain(
                "org.example.domain2"
        );

        Optional<Domain> nullableRequest = env.resolveDomain(
                "org.example.__requested__"
        );

        // Asserting that the resolution succeeded

        assertTrue(nullableDomain1.isPresent());
        assertEquals("org.example.domain1", nullableDomain1.get().getName());

        assertTrue(nullableDomain2.isPresent());
        assertEquals("org.example.domain2", nullableDomain2.get().getName());

        assertTrue(nullableRequest.isPresent());
        assertEquals(
                "org.example.__requested__",
                nullableRequest.get().getName()
        );
    }

    @Test
    public void testResolveModel() {
        // Preparing the test environment

        Model testModel1 = new Model(
                "TestModel",
                EnumSet.of(Model.Flag.Builtin),
                Collections.emptyList(),
                Collections.emptySet()
        );

        Model testModel2 = new Model(
                "TestModel",
                EnumSet.noneOf(Model.Flag.class),
                Collections.singletonList(new TypeParameter("Param")),
                Collections.emptySet()
        );

        Set<Domain> domains = new HashSet<>();

        domains.add(new Domain(
                "org.example.1",
                EnumSet.of(Domain.Flag.Ubiquitous),
                Collections.singleton(testModel1)
        ));

        Domain domain2 = new Domain(
                "org.example.2",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.singleton(testModel2)
        );

        domains.add(domain2);

        Domain extern = new Domain(
                "extern",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.emptySet()
        );

        domains.add(extern);

        Environment env = new Environment(domains);

        // Resolving the same model name from different contexts

        Optional<Model> nullContext = env.resolveModel("TestModel", null);
        Optional<Model> twoContext = env.resolveModel("TestModel", domain2);
        Optional<Model> externContext = env.resolveModel("TestModel", extern);

        // Resolving both models by their fully qualified name from domain2

        Optional<Model> optionalModel1 = env.resolveModel(
                "org.example.1.TestModel",
                domain2
        );

        Optional<Model> optionalModel2 = env.resolveModel(
                "org.example.2.TestModel",
                domain2
        );

        // Asserting that only the expected models have been resolved

        assertTrue(nullContext.isPresent());
        assertEquals(testModel1, nullContext.get());

        assertTrue(twoContext.isPresent());
        assertEquals(testModel2, twoContext.get());

        assertTrue(externContext.isPresent());
        assertEquals(testModel1, externContext.get());

        assertTrue(optionalModel1.isPresent());
        assertEquals(testModel1, optionalModel1.get());

        assertTrue(optionalModel2.isPresent());
        assertEquals(testModel2, optionalModel2.get());
    }

    @Test
    public void testConstructorUbiquitousCollidingCheck() {
        Set<Domain> domains = new HashSet<>();

        domains.add(new Domain(
                "org.example.1",
                EnumSet.of(Domain.Flag.Ubiquitous),
                Collections.singleton(new Model(
                        "CollidingModel",
                        EnumSet.noneOf(Model.Flag.class),
                        Collections.emptyList(),
                        Collections.emptySet()
                ))
        ));

        domains.add(new Domain(
                "org.example.2",
                EnumSet.of(Domain.Flag.Ubiquitous),
                Collections.singleton(new Model(
                        "CollidingModel",
                        EnumSet.noneOf(Model.Flag.class),
                        Collections.emptyList(),
                        Collections.emptySet()
                ))
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> new Environment(domains)
        );
    }
}
