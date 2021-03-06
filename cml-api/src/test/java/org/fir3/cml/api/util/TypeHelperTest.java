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

    @Test
    public void testComputeDerivationScoreNullArgument() {
        assertThrows(
                NullPointerException.class,
                () -> TypeHelper.computeDerivationScore(
                        null,
                        TypeHelper.fromString("M:test.SimpleModel")
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> TypeHelper.computeDerivationScore(
                        TypeHelper.fromString("M:test.SimpleModel"),
                        null
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> TypeHelper.computeDerivationScore(null, null)
        );
    }

    @Test
    public void testComputeDerivationScoreNoDerivation() {
        assertEquals(-1, TypeHelper.computeDerivationScore(
                TypeHelper.fromString("M:test.FirstModel"),
                TypeHelper.fromString("M:test.SecondModel")
        ));

        assertEquals(-1, TypeHelper.computeDerivationScore(
                TypeHelper.fromString("GM:test.ComplexModel<M:prim.Int32>"),
                TypeHelper.fromString("GM:test.ComplexModel<M:prim.String>")
        ));
    }

    @Test
    public void testComputeDerivationScoreEqual() {
        assertEquals(0, TypeHelper.computeDerivationScore(
                TypeHelper.fromString("M:test.SimpleModel"),
                TypeHelper.fromString("M:test.SimpleModel")
        ));

        assertEquals(0, TypeHelper.computeDerivationScore(
                TypeHelper.fromString("GM:t.ComplexModel<M:p.Int32,P:P1>"),
                TypeHelper.fromString("GM:t.ComplexModel<M:p.Int32,P:P1>")
        ));
    }

    @Test
    public void testComputeDerivationScoreSophisticated() {
        assertEquals(1, TypeHelper.computeDerivationScore(
                TypeHelper.fromString("M:t.SimpleModel"),
                TypeHelper.fromString("P:P1")
        ));

        assertEquals(1, TypeHelper.computeDerivationScore(
                TypeHelper.fromString("GM:t.List<M:p.Int32>"),
                TypeHelper.fromString("GM:t.List<P:P1>")
        ));

        assertEquals(2, TypeHelper.computeDerivationScore(
                TypeHelper.fromString(
                        "GM:t.Map<GM:t.List<M:p.Int32>,GM:t.List<P:P1>>"
                ),
                TypeHelper.fromString("GM:t.Map<P:P1,P:P2>")
        ));

        assertEquals(3, TypeHelper.computeDerivationScore(
                TypeHelper.fromString(
                        "GM:t.Map<GM:t.Map<GM:t.List<M:p.Int32>," +
                                "GM:t.List<M:p.Int32>>,M:p.Int32>"
                ),
                TypeHelper.fromString(
                        "GM:t.Map<GM:t.Map<GM:t.List<P:P1>,GM:t.List<P:P2>>," +
                                "P:P3>"
                )
        ));
    }
}
