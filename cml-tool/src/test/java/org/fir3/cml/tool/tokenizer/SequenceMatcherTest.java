package org.fir3.cml.tool.tokenizer;

import org.fir3.cml.tool.util.seq.InputStreamSequence;
import org.fir3.cml.tool.util.seq.Sequence;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class SequenceMatcherTest {
    private static final byte[] SAMPLE_SEQUENCE_1 = {
            (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE
    };

    private static final byte[] SAMPLE_BYTE_SOURCE_1 = {
            (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE,
            (byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF
    };

    private static final byte[] SAMPLE_BYTE_SOURCE_2 = {
            (byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF,
            (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE
    };

    @Test
    @SuppressWarnings("unchecked")
    public void testEmptyConstructorFails() {
        assertThrows(IllegalArgumentException.class, SequenceMatcher::new);
    }

    @Test
    public void testMatching() throws IOException {
        SequenceMatcher<Integer> matcher = new SequenceMatcher<>(
                Helper.fromByteArray(SequenceMatcherTest.SAMPLE_SEQUENCE_1)
        );

        try (Sequence<Integer> src = new InputStreamSequence(
                new ByteArrayInputStream(
                        SequenceMatcherTest.SAMPLE_BYTE_SOURCE_1
                )
        )) {
            assertTrue(matcher.matches(src, false));
            assertTrue(matcher.matches(src, false));
        }
    }

    @Test
    public void testShorthandMatching() throws IOException {
        SequenceMatcher<Integer> matcher = new SequenceMatcher<>(
                Helper.fromByteArray(SequenceMatcherTest.SAMPLE_SEQUENCE_1)
        );

        try (Sequence<Integer> src = new InputStreamSequence(
                new ByteArrayInputStream(
                        SequenceMatcherTest.SAMPLE_BYTE_SOURCE_1
                )
        )) {
            assertTrue(matcher.matches(src));
            assertTrue(matcher.matches(src));
        }
    }

    @Test
    public void testSkipping() throws IOException {
        SequenceMatcher<Integer> matcher = new SequenceMatcher<>(
                Helper.fromByteArray(SequenceMatcherTest.SAMPLE_SEQUENCE_1)
        );

        try (Sequence<Integer> src = new InputStreamSequence(
                new ByteArrayInputStream(
                        SequenceMatcherTest.SAMPLE_BYTE_SOURCE_1
                )
        )) {
            assertTrue(matcher.matches(src, true));
            assertFalse(matcher.matches(src, true));
        }
    }

    @Test
    public void testShorthandSkipping() throws IOException {
        SequenceMatcher<Integer> matcher = new SequenceMatcher<>(
                Helper.fromByteArray(SequenceMatcherTest.SAMPLE_SEQUENCE_1)
        );

        try (Sequence<Integer> src = new InputStreamSequence(
                new ByteArrayInputStream(
                        SequenceMatcherTest.SAMPLE_BYTE_SOURCE_1
                )
        )) {
            assertTrue(matcher.skip(src));
            assertFalse(matcher.skip(src));
        }
    }

    @Test
    public void testCopyInConstructor() {
        Integer[] seq = Helper.fromByteArray(new byte[] {
                (byte) 0xF0, (byte) 0x0D
        });

        Integer[] seqCopy = Arrays.copyOf(seq, seq.length);
        SequenceMatcher<Integer> matcher = new SequenceMatcher<>(seq);

        seq[0] = 0xC0;
        seq[1] = 0x1A;

        assertArrayEquals(seqCopy, matcher.getSequence());
    }

    @Test
    public void testUnmodifiability() {
        Integer[] seq = Helper.fromByteArray(new byte[] {
                (byte) 0xF0, (byte) 0x0D
        });

        SequenceMatcher<Integer> matcher = new SequenceMatcher<>(seq);

        Integer[] byteSeq = matcher.getSequence();
        byteSeq[0] = 0xC0;
        byteSeq[1] = 0x1A;

        assertArrayEquals(seq, matcher.getSequence());
    }

    @Test
    public void testRewindOnNotMatching() throws IOException {
        SequenceMatcher<Integer> matcher = new SequenceMatcher<>(
                Helper.fromByteArray(SequenceMatcherTest.SAMPLE_SEQUENCE_1)
        );

        byte[] remaining;

        try (Sequence<Integer> src = new InputStreamSequence(
                new ByteArrayInputStream(
                        SequenceMatcherTest.SAMPLE_BYTE_SOURCE_2
                )
        )) {
            assertFalse(matcher.matches(src, true));
            assertFalse(matcher.matches(src, false));
            assertFalse(matcher.matches(src));
            assertFalse(matcher.skip(src));

            try (ByteArrayOutputStream dst = new ByteArrayOutputStream()) {
                Integer next;

                while ((next = src.read()) != null) {
                    dst.write(next);
                }

                remaining = dst.toByteArray();
            }
        }

        // Assertions

        assertArrayEquals(SequenceMatcherTest.SAMPLE_BYTE_SOURCE_2, remaining);
    }
}
