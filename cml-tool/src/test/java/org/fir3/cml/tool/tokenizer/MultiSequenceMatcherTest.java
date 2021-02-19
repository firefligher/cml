package org.fir3.cml.tool.tokenizer;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MultiSequenceMatcherTest {
    private static final byte[] SAMPLE_SEQUENCE_1 = {
            (byte) 0xBA, (byte) 0xAD
    };

    private static final byte[] SAMPLE_SEQUENCE_2 = {
            (byte) 0xBA, (byte) 0xAD, (byte) 0xF0, (byte) 0x0D
    };

    private static final byte[] SAMPLE_SEQUENCE_3 = {
            (byte) 0xBA, (byte) 0xAD, (byte) 0xFE, (byte) 0xED
    };

    private static final byte[] SAMPLE_BYTE_SOURCE_1 = {
            (byte) 0xBA, (byte) 0xAD,
            (byte) 0xBA, (byte) 0xAD, (byte) 0xF0, (byte) 0x0D,
            (byte) 0xBA, (byte) 0xAD,
            (byte) 0xBA, (byte) 0xAD,
            (byte) 0xBA, (byte) 0xAD, (byte) 0xFE, (byte) 0xED,
            (byte) 0xBA, (byte) 0xAD
    };

    @Test
    public void testSorting() throws IOException {
        // Setup

        SequenceMatcher seq1 = new SequenceMatcher(
                MultiSequenceMatcherTest.SAMPLE_SEQUENCE_1
        );

        SequenceMatcher seq2 = new SequenceMatcher(
                MultiSequenceMatcherTest.SAMPLE_SEQUENCE_2
        );

        SequenceMatcher seq3 = new SequenceMatcher(
                MultiSequenceMatcherTest.SAMPLE_SEQUENCE_3
        );

        MultiSequenceMatcher matcher = new MultiSequenceMatcher(
                seq1, seq2, seq3
        );

        // Assertions

        try (InputStream src = new ByteArrayInputStream(
                MultiSequenceMatcherTest.SAMPLE_BYTE_SOURCE_1
        )) {
            assertSame(seq1, matcher.skip(src).orElse(null));
            assertSame(seq2, matcher.skip(src).orElse(null));
            assertSame(seq1, matcher.skip(src).orElse(null));
            assertSame(seq1, matcher.skip(src).orElse(null));
            assertSame(seq3, matcher.skip(src).orElse(null));
            assertSame(seq1, matcher.skip(src).orElse(null));
        }
    }

    @Test
    public void testNonEqualityVerified() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new MultiSequenceMatcher(
                        new SequenceMatcher(
                                MultiSequenceMatcherTest.SAMPLE_SEQUENCE_1
                        ),
                        new SequenceMatcher(
                                MultiSequenceMatcherTest.SAMPLE_SEQUENCE_1
                        )
                )
        );
    }
}
