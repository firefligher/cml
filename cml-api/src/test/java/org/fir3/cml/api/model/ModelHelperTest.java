package org.fir3.cml.api.model;

import org.fir3.cml.api.util.ModelHelper;
import org.fir3.cml.api.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ModelHelperTest {
    @Test
    public void testReversibility() {
        // Setting up the model, domain and environment

        Model model = new Model(
                "TestModel",
                EnumSet.noneOf(Model.Flag.class),
                Collections.emptyList(),
                Collections.emptySet()
        );

        Domain domain = new Domain(
                "__test_domain__",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.singleton(model)
        );

        Environment environment = new Environment(
                Collections.singleton(domain)
        );

        // Performing the reversion

        Optional<Pair<Domain, Model>> optionalPair = ModelHelper.fromString(
                ModelHelper.toString(domain, model),
                environment
        );

        // Asserting that the reversion succeeded

        assertTrue(optionalPair.isPresent());

        Pair<Domain, Model> pair = optionalPair.get();
        assertEquals(domain, pair.getFirstComponent());
        assertEquals(model, pair.getSecondComponent());
    }
}
