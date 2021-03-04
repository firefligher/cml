package org.fir3.cml.api.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypeHelperTest {
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
}
