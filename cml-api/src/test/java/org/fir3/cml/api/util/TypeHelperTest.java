package org.fir3.cml.api.util;

import org.fir3.cml.api.model.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class TypeHelperTest {
    @Test
    public void testFromStringInvalidGeneric() {
        assertThrows(
                IllegalArgumentException.class,
                () -> TypeHelper.fromString("GM:SomeModel>P:P1<")
        );
    }

    @Test
    public void testFromStringEmptyGeneric() {
        assertThrows(
                IllegalArgumentException.class,
                () -> TypeHelper.fromString("GM:SomeModel<>")
        );
    }

    @Test
    public void testGenericIsDerivation() {
        assertTrue(TypeHelper.isDerivation(
                TypeHelper.fromString("GM:SomeModel<P:P1>"),
                TypeHelper.fromString("P:SomeParameter")
        ));
    }

    @Test
    public void testComplexIsDerivation() {
        assertTrue(TypeHelper.isDerivation(
                TypeHelper.fromString(
                        "GM:ComplexModel<" +
                                "GM:ComplexModel<" +
                                "GM:ComplexModel<P:P1,P:P2>," +
                                "M:SimpleModel>,M:SimpleModel>"
                ),
                TypeHelper.fromString(
                        "GM:ComplexModel<GM:ComplexModel<P:P1,P:P2>,P:P3>"
                )
        ));
    }

    @Test
    public void testFrom() {
        // Setting up an environment

        Model model = new Model(
                "ComplexModel",
                EnumSet.noneOf(Model.Flag.class),
                Arrays.asList(
                        new TypeParameter("Param1"),
                        new TypeParameter("SecondPar"),
                        new TypeParameter("Par3"),
                        new TypeParameter("_4thPar")
                ),
                Collections.emptySet()
        );

        Domain domain = new Domain(
                "__test_domain__",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.singleton(model)
        );

        Environment environment = new Environment(Collections.singleton(
                domain
        ));

        // Deriving the most generic type from model

        Type type = TypeHelper.from(environment, domain, model);

        // Asserting that the correct and normalized type was returned

        assertEquals(TypeHelper.fromString(
                "GM:__test_domain__.ComplexModel<P:P1,P:P2,P:P3,P:P4>"
        ),  type);
    }
}
