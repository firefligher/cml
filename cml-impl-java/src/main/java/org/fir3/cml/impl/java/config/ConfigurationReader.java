package org.fir3.cml.impl.java.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.fir3.cml.api.model.Type;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * The reader for JSON-serialized instances of {@link Configuration}.
 */
public final class ConfigurationReader {
    private static ConfigurationReader INSTANCE;

    /**
     * Returns the singleton instance of this class.
     *
     * @return  The singleton instance.
     */
    public static ConfigurationReader getInstance() {
        if (ConfigurationReader.INSTANCE == null) {
            ConfigurationReader.INSTANCE = new ConfigurationReader();
        }

        return ConfigurationReader.INSTANCE;
    }

    private final Gson gson;

    private ConfigurationReader() {
        GsonBuilder builder = new GsonBuilder();

        // Registering the deserialization implementations for the
        // Configuration type and its dependencies.

        builder.registerTypeAdapter(
                Configuration.class,
                new Configuration.Deserializer()
        );

        builder.registerTypeAdapter(
                TypeMapping.class,
                new TypeMapping.Deserializer()
        );

        builder.registerTypeAdapter(Type.class, new TypeDeserializer());
        builder.registerTypeAdapter(
                JavaTypeInfo.class,
                new JavaTypeInfo.Deserializer()
        );

        // Building the new Gson instance

        this.gson = builder.create();
    }

    /**
     * Attempts to read and deserialize a {@link Configuration} object from the
     * specified <code>src</code>.
     *
     * <p>
     *     The specified <code>src</code> stream will be closed when this
     *     method returns.
     * </p>
     *
     * @param src   The source stream that contains the serialized
     *              {@link Configuration} instance.
     *
     * @return  The deserialized {@link Configuration} instance.
     *
     * @throws IOException  If <code>src</code> is invalid or contains invalid
     *                      data.
     */
    public Configuration read(InputStream src) throws IOException {
        try (Reader r = new InputStreamReader(src)) {
            return this.gson.fromJson(r, Configuration.class);
        } catch (JsonSyntaxException | JsonIOException ex) {
            throw new IOException(ex);
        }
    }
}
