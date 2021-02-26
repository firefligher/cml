package org.fir3.cml.tool.impl;

import org.fir3.cml.api.model.Type;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelTypeImplTest {
    @Test
    public void testCopyInConstructor() {
        List<Type> typeParameters = new ArrayList<>(Arrays.asList(
                new ParameterTypeImpl("FirstParam"),
                new ParameterTypeImpl("SecondParam")
        ));

        Type.ModelType modelType = new ModelTypeImpl(
                "TestModel",
                typeParameters
        );

        // Modifying the typeParameters list

        typeParameters.clear();
        typeParameters.add(new ParameterTypeImpl("ForeignParam"));

        // Validating that the type parameters of the modelType have not been
        // modified.

        List<Type> actualTypeParameters = modelType.getTypeParameters();

        assertEquals(2, actualTypeParameters.size());
        assertEquals(
                "FirstParam",
                ((ParameterTypeImpl) actualTypeParameters.get(0))
                        .getTypeParameterName()
        );

        assertEquals(
                "SecondParam",
                ((ParameterTypeImpl) actualTypeParameters.get(1))
                        .getTypeParameterName()
        );
    }

    @Test
    public void testUnmodifiability() {
        Type.ModelType modelType = new ModelTypeImpl(
                "TestModel",
                Arrays.asList(
                        new ParameterTypeImpl("FirstParam"),
                        new ParameterTypeImpl("SecondParam")
                )
        );

        // Attempting to modify the list of type parameters

        List<Type> typeParameters = modelType.getTypeParameters();

        try {
            typeParameters.clear();
            typeParameters.add(new ParameterTypeImpl("ForeignParam"));
        } catch (Throwable ignored) {
            // Not mandatory for this test
        }

        // Asserting that the type parameters of the modelType have not been
        // changed

        List<Type> actualTypeParameters = modelType.getTypeParameters();

        assertEquals(2, actualTypeParameters.size());
        assertEquals(
                "FirstParam",
                ((ParameterTypeImpl) actualTypeParameters.get(0))
                        .getTypeParameterName()
        );

        assertEquals(
                "SecondParam",
                ((ParameterTypeImpl) actualTypeParameters.get(1))
                        .getTypeParameterName()
        );
    }
}
