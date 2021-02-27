package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.TypeParameter;
import org.fir3.cml.tool.impl.TypeParameterImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ModelEnvironmentTest {
    @Test
    public void testCopyInConstructor() {
        Set<TypeParameter> typeParameters = new HashSet<>(Arrays.asList(
                new TypeParameterImpl("Param1"),
                new TypeParameterImpl("Param2")
        ));

        Environment env = new Environment(typeParameters);

        // Modifying the typeParameters

        typeParameters.clear();
        typeParameters.add(new TypeParameterImpl("ForeignParam"));

        // Asserting that the type parameters of env did not change

        Set<TypeParameter> actualTypeParameters = env.getTypeParameters();
        assertEquals(2, actualTypeParameters.size());
        assertTrue(
                actualTypeParameters.stream()
                        .anyMatch(p -> Objects.equals("Param1", p.getName()))
        );

        assertTrue(
                actualTypeParameters.stream()
                        .anyMatch(p -> Objects.equals("Param2", p.getName()))
        );
    }

    @Test
    public void testImmutability() {
        Environment env = new Environment(new HashSet<>(
                Arrays.asList(
                        new TypeParameterImpl("Param1"),
                        new TypeParameterImpl("Param2")
                )
        ));

        // Attempting to modify the type parameters set

        Set<TypeParameter> typeParameters = env.getTypeParameters();

        try {
            typeParameters.clear();
            typeParameters.add(new TypeParameterImpl("ForeignParam"));
        } catch (Throwable ignored) { }

        // Asserting that env has not been changed

        Set<TypeParameter> actualTypeParameters = env.getTypeParameters();
        assertEquals(2, actualTypeParameters.size());
        assertTrue(
                actualTypeParameters.stream()
                        .anyMatch(p -> Objects.equals("Param1", p.getName()))
        );

        assertTrue(
                actualTypeParameters.stream()
                        .anyMatch(p -> Objects.equals("Param2", p.getName()))
        );
    }
}
