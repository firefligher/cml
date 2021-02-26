package org.fir3.cml.tool.tokenizer;

import org.fir3.cml.tool.util.seq.InputStreamSequence;
import org.fir3.cml.tool.util.seq.Sequence;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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

        SequenceMatcher<Byte> seq1 = new SequenceMatcher<>(
                Helper.fromPrimitive(
                        MultiSequenceMatcherTest.SAMPLE_SEQUENCE_1
                )
        );

        SequenceMatcher<Byte> seq2 = new SequenceMatcher<>(
                Helper.fromPrimitive(
                        MultiSequenceMatcherTest.SAMPLE_SEQUENCE_2
                )
        );

        SequenceMatcher<Byte> seq3 = new SequenceMatcher<>(
                Helper.fromPrimitive(
                        MultiSequenceMatcherTest.SAMPLE_SEQUENCE_3
                )
        );

        MultiSequenceMatcher<Byte> matcher = new MultiSequenceMatcher<>(
                seq1, seq2, seq3
        );

        // Assertions

        try (Sequence<Byte> src = new InputStreamSequence(
                new ByteArrayInputStream(
                        MultiSequenceMatcherTest.SAMPLE_BYTE_SOURCE_1
                )
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
                () -> new MultiSequenceMatcher<>(
                        new SequenceMatcher<>(Helper.fromPrimitive(
                                MultiSequenceMatcherTest.SAMPLE_SEQUENCE_1
                        )),
                        new SequenceMatcher<>(Helper.fromPrimitive(
                                MultiSequenceMatcherTest.SAMPLE_SEQUENCE_1
                        ))
                )
        );
    }
}
