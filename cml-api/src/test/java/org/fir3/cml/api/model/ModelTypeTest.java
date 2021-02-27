package org.fir3.cml.api.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelTypeTest {
    @Test
    public void testCopyInConstructor() {
        List<Type> typeParameters = new ArrayList<>(Arrays.asList(
                new ParameterType("FirstParam"),
                new ParameterType("SecondParam")
        ));

        ModelType modelType = new ModelType(
                "TestModel",
                typeParameters
        );

        // Modifying the typeParameters list

        typeParameters.clear();
        typeParameters.add(new ParameterType("ForeignParam"));

        // Validating that the type parameters of the modelType have not been
        // modified.

        List<Type> actualTypeParameters = modelType.getTypeParameters();

        assertEquals(2, actualTypeParameters.size());
        assertEquals(
                "FirstParam",
                ((ParameterType) actualTypeParameters.get(0))
                        .getTypeParameterName()
        );

        assertEquals(
                "SecondParam",
                ((ParameterType) actualTypeParameters.get(1))
                        .getTypeParameterName()
        );
    }

    @Test
    public void testUnmodifiability() {
        ModelType modelType = new ModelType(
                "TestModel",
                Arrays.asList(
                        new ParameterType("FirstParam"),
                        new ParameterType("SecondParam")
                )
        );

        // Attempting to modify the list of type parameters

        List<Type> typeParameters = modelType.getTypeParameters();

        try {
            typeParameters.clear();
            typeParameters.add(new ParameterType("ForeignParam"));
        } catch (Throwable ignored) {
            // Not mandatory for this test
        }

        // Asserting that the type parameters of the modelType have not been
        // changed

        List<Type> actualTypeParameters = modelType.getTypeParameters();

        assertEquals(2, actualTypeParameters.size());
        assertEquals(
                "FirstParam",
                ((ParameterType) actualTypeParameters.get(0))
                        .getTypeParameterName()
        );

        assertEquals(
                "SecondParam",
                ((ParameterType) actualTypeParameters.get(1))
                        .getTypeParameterName()
        );
    }
}
