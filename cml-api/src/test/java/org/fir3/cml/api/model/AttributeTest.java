package org.fir3.cml.api.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttributeTest {
    @Test
    public void testEqualsAndHashCode() {
        Attribute attr1 = new Attribute(
                "_TestAttribute",
                new ParameterType("ParamType")
        );

        Attribute attr2 = new Attribute(
                "_TestAttribute",
                new ParameterType("ParamType")
        );

        assertEquals(attr1, attr2);
        assertEquals(attr2, attr1);
        assertEquals(attr1.hashCode(), attr2.hashCode());
    }
}
