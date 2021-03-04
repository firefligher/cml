package org.fir3.cml.impl.java.config;

import org.fir3.cml.api.util.TypeHelper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigurationReaderTest {
    @Test
    public void testReadEmpty() throws IOException {
        try (InputStream src =
                     ConfigurationReaderTest.class.getResourceAsStream(
                             "/config/empty.json"
                     )
        ) {
            Configuration cfg = ConfigurationReader.getInstance().read(src);

            // Asserting that the deserialized configuration instance is empty

            assertTrue(cfg.getTypeMappings().isEmpty());
        }
    }

    @Test
    public void testReadSample1() throws IOException {
        try (InputStream src =
                     ConfigurationReaderTest.class.getResourceAsStream(
                             "/config/sample1.json"
                     )
        ) {
            Configuration cfg = ConfigurationReader.getInstance().read(src);

            // Asserting that the details, that have been specified in the
            // JSON-file, have been deserialized successfully.

            // typeMappings

            Set<TypeMapping> typeMappings = cfg.getTypeMappings();

            assertEquals(2, typeMappings.size());
            assertTrue(typeMappings.contains(new TypeMapping(
                    TypeHelper.fromString("M:org.example.Int32"),
                    new JavaType("int", "java.lang.Integer")
            )));

            assertTrue(typeMappings.contains(new TypeMapping(
                    TypeHelper.fromString("M:org.example.String"),
                    new JavaType(null, "java.lang.String")
            )));

            // outputDirectory

            assertEquals(new File("."), cfg.getOutputDirectory());
        }
    }
}
