package org.fir3.cml.api;

import org.fir3.cml.api.model.ModelType;
import org.fir3.cml.api.model.ParameterType;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuiltinTest {
    @Test
    public void testBuiltinTypes() {
        assertEquals(
                new ModelType(
                        "org.fir3.cml.__builtin__.Bit",
                        Collections.emptyList()
                ),
                Builtin.TYPE_BIT
        );

        assertEquals(
                new ModelType(
                        "org.fir3.cml.__builtin__.Sequence",
                        Collections.singletonList(new ParameterType("P1"))
                ),
                Builtin.TYPE_SEQUENCE
        );
    }
}
