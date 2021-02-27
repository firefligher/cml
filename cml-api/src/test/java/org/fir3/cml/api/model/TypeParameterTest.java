package org.fir3.cml.api.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeParameterTest {
    @Test
    public void testEqualsAndHashCode() {
        TypeParameter parameter1 = new TypeParameter("Param");
        TypeParameter parameter2 = new TypeParameter("Param");

        assertEquals(parameter1, parameter2);
        assertEquals(parameter2, parameter1);
        assertEquals(parameter1.hashCode(), parameter2.hashCode());
    }
}
