package org.fir3.cml.impl.java.type;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class JavaTypeHelperTest {
    @Test
    public void testFromStringTypeVariable() {
        JavaType type = JavaTypeHelper.fromString("P:Variable1");

        assertEquals(JavaType.Category.TypeVariable, type.getCategory());
        assertEquals("Variable1", ((TypeVariableType) type).getName());
    }

    @Test
    public void testFromStringSimpleClass() {
        JavaType type = JavaTypeHelper.fromString("java.lang.Boolean");

        assertEquals(JavaType.Category.ClassType, type.getCategory());

        ClassType classType = (ClassType) type;
        assertEquals(
                "java.lang.Boolean",
                classType.getFullyQualifiedClassName()
        );

        assertTrue(classType.getTypeParameters().isEmpty());
    }

    @Test
    public void testFromStringGenericClass() {
        JavaType type = JavaTypeHelper.fromString(
                "java.util.Map<java.util.List<java.lang.Boolean>,P:TValue>"
        );

        assertEquals(
                new ClassType("java.util.Map", Arrays.asList(
                        new ClassType(
                                "java.util.List",
                                Collections.singletonList(new ClassType(
                                        "java.lang.Boolean",
                                        Collections.emptyList()
                                ))
                        ),
                        new TypeVariableType("TValue")
                )),
                type
        );
    }

    @Test
    public void testFromStringWrongGeneric() {
        assertThrows(
                IllegalArgumentException.class,
                () -> JavaTypeHelper.fromString("java.util.List>String<")
        );
    }

    @Test
    public void testFromStringEmptyGeneric() {
        assertThrows(
                IllegalArgumentException.class,
                () -> JavaTypeHelper.fromString("java.util.List<>")
        );
    }
}
