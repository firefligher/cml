package org.fir3.cml.api.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
