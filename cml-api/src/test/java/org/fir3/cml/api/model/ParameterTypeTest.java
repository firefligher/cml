package org.fir3.cml.api.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParameterTypeTest {
    @Test
    public void testEqualsAndHashCode() {
        ParameterType type1 = new ParameterType("Param");
        ParameterType type2 = new ParameterType("Param");

        assertEquals(type1, type2);
        assertEquals(type2, type1);
        assertEquals(type1.hashCode(), type2.hashCode());
    }
}
