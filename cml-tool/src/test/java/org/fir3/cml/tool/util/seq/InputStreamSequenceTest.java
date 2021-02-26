package org.fir3.cml.tool.util.seq;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InputStreamSequenceTest {
    private static final byte[] TEST_SEQUENCE = new byte[] {
            (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE,
            (byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF,
            (byte) 0xBA, (byte) 0xAD, (byte) 0xC0, (byte) 0xDE
    };

    @Test
    public void testRead() throws IOException {
        // Creating the sequence

        InputStream src = new ByteArrayInputStream(
                InputStreamSequenceTest.TEST_SEQUENCE);

        Sequence<Integer> seq = new InputStreamSequence(src);

        // Asserting that the sequence matches the original bytes

        for (byte value : InputStreamSequenceTest.TEST_SEQUENCE) {
            assertEquals(value, (byte) (int) seq.read());
        }

        // Asserting that EOF is indicated by 'null'

        assertNull(seq.read());

        // Cleaning up

        seq.close();
        src.close();
    }
}
