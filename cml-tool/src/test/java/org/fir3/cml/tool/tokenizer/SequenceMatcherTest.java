package org.fir3.cml.tool.tokenizer;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private static final class UnmarkableInputStream extends InputStream {
        @Override
        public int read() {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public boolean markSupported() {
            return false;
        }
    }

    @Test
    public void testEmptyConstructorFails() {
        assertThrows(IllegalArgumentException.class, SequenceMatcher::new);
    }

    @Test
    public void testMarkabilityVerified() {
        SequenceMatcher matcher = new SequenceMatcher((byte) 0x00);
        InputStream stream = new UnmarkableInputStream();

        assertThrows(
                IllegalArgumentException.class,
                () -> matcher.matches(stream, false)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> matcher.matches(stream, true)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> matcher.matches(stream)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> matcher.skip(stream)
        );
    }

    @Test
    public void testMatching() throws IOException {
        SequenceMatcher matcher = new SequenceMatcher(
                SequenceMatcherTest.SAMPLE_SEQUENCE_1
        );

        try (InputStream src = new ByteArrayInputStream(
                SequenceMatcherTest.SAMPLE_BYTE_SOURCE_1
        )) {
            assertTrue(matcher.matches(src, false));
            assertTrue(matcher.matches(src, false));
        }
    }

    @Test
    public void testShorthandMatching() throws IOException {
        SequenceMatcher matcher = new SequenceMatcher(
                SequenceMatcherTest.SAMPLE_SEQUENCE_1
        );

        try (InputStream src = new ByteArrayInputStream(
                SequenceMatcherTest.SAMPLE_BYTE_SOURCE_1
        )) {
            assertTrue(matcher.matches(src));
            assertTrue(matcher.matches(src));
        }
    }

    @Test
    public void testSkipping() throws IOException {
        SequenceMatcher matcher = new SequenceMatcher(
                SequenceMatcherTest.SAMPLE_SEQUENCE_1
        );

        try (InputStream src = new ByteArrayInputStream(
                SequenceMatcherTest.SAMPLE_BYTE_SOURCE_1
        )) {
            assertTrue(matcher.matches(src, true));
            assertFalse(matcher.matches(src, true));
        }
    }

    @Test
    public void testShorthandSkipping() throws IOException {
        SequenceMatcher matcher = new SequenceMatcher(
                SequenceMatcherTest.SAMPLE_SEQUENCE_1
        );

        try (InputStream src = new ByteArrayInputStream(
                SequenceMatcherTest.SAMPLE_BYTE_SOURCE_1
        )) {
            assertTrue(matcher.skip(src));
            assertFalse(matcher.skip(src));
        }
    }

    @Test
    public void testCopyInConstructor() {
        byte[] seq = new byte[] { (byte) 0xF0, (byte) 0x0D };
        byte[] seqCopy = Arrays.copyOf(seq, seq.length);

        SequenceMatcher matcher = new SequenceMatcher(seq);
        seq[0] = (byte) 0xC0;
        seq[1] = (byte) 0x1A;

        assertArrayEquals(seqCopy, matcher.getByteSequence());
    }

    @Test
    public void testUnmodifiability() {
        byte[] seq = new byte[] { (byte) 0xF0, (byte) 0x0D };

        SequenceMatcher matcher = new SequenceMatcher(seq);
        byte[] byteSeq = matcher.getByteSequence();
        byteSeq[0] = (byte) 0xC0;
        byteSeq[1] = (byte) 0x1A;

        assertArrayEquals(seq, matcher.getByteSequence());
    }

    @Test
    public void testRewindOnNotMatching() throws IOException {
        SequenceMatcher matcher = new SequenceMatcher(
                SequenceMatcherTest.SAMPLE_SEQUENCE_1
        );

        byte[] remaining;

        try (InputStream src = new ByteArrayInputStream(
                SequenceMatcherTest.SAMPLE_BYTE_SOURCE_2
        )) {
            matcher.matches(src, true);
            matcher.matches(src, false);
            matcher.matches(src);
            matcher.skip(src);

            try (ByteArrayOutputStream dst = new ByteArrayOutputStream()) {
                int next;

                while ((next = src.read()) > -1) {
                    dst.write(next);
                }

                remaining = dst.toByteArray();
            }
        }

        // Assertions

        assertArrayEquals(SequenceMatcherTest.SAMPLE_BYTE_SOURCE_2, remaining);
    }
}
