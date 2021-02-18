package org.fir3.cml.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InfoTest {
    @Translator.Info(name = "dummy")
    private static class DummyClass1 { }

    @Translator.Info(name = "dummy")
    private static class DummyClass2 { }

    @Test
    public void testEquality() {
        // This test verifies that two different instances of the Info
        // annotation with equal information are considered to be equal (in
        // terms of hashCode and equals).

        Translator.Info annotation1 = DummyClass1.class.getAnnotation(
                Translator.Info.class
        );

        Translator.Info annotation2 = DummyClass2.class.getAnnotation(
                Translator.Info.class
        );

        assertEquals(annotation2.hashCode(), annotation1.hashCode());
        assertEquals(annotation2, annotation1);
    }
}
