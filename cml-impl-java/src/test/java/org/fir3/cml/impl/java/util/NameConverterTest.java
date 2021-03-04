package org.fir3.cml.impl.java.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NameConverterTest {
    @Test
    public void testConvertDomainToPackage() {
        String pkgName = NameConverter.convertDomainToPackage(
                "lol.int.boolean.true.test"
        );

        assertEquals("lol._int._boolean._true.test", pkgName);
    }

    @Test
    public void testConvertModelToClass() {
        String clsName = NameConverter.convertModelToClass(
                "9._a.char.int.strictfp"
        );

        assertEquals("9._a._char._int._strictfp", clsName);
    }

    @Test
    public void testConvertTypeParameterToTypeVariable() {
        String tvName = NameConverter.convertTypeParameterToTypeVariable(
                "double"
        );

        assertEquals("_double", tvName);
    }

    @Test
    public void testConvertAttributeToField() {
        String fieldName = NameConverter.convertAttributeToField("protected");

        assertEquals("_protected", fieldName);
    }
}
