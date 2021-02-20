package org.fir3.cml.tool.tokenizer;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * A utility to test if the next bytes of a markable {@link InputStream} match
 * a certain byte sequence.
 */
final class SequenceMatcher {
    private final int[] sequence;

    /**
     * Initializes the matcher with the specified <code>sequence</code> of
     * bytes.
     *
     * @param sequence  The byte sequence that this instance will be able to
     *                  match in {@link InputStream}s. This is required to
     *                  consist of at least one byte.
     *
     * @throws IllegalArgumentException If the specified <code>sequence</code>
     *                                  is empty.
     */
    SequenceMatcher(byte... sequence) {
        if (sequence.length < 1) {
            throw new IllegalArgumentException("sequence too short");
        }

        this.sequence = new int[sequence.length];

        for (int index = 0; index < sequence.length; index++) {
            this.sequence[index] = sequence[index] & 0xFF;
        }
    }

    /**
     * Tests, if the next bytes of the specified <code>src</code> stream match
     * with the byte sequence that this matcher has been initialized with.
     *
     * @param src           The markable source stream.
     * @param skipIfMatches If this flag is enabled, the method will not rewind
     *                      the <code>src</code> stream to its initial state,
     *                      if the byte sequence of this matcher was found.
     *
     * @return  Either <code>true</code>, if the byte sequence of this matcher
     *          was found (and skipped, if <code>skipIfMatches</code> was
     *          enabled), otherwise <code>false</code>.
     *
     * @throws IOException  If an {@link IOException} occurs when dealing with
     *                      the <code>src</code> stream.
     *
     * @throws IllegalArgumentException If the specified <code>src</code> does
     *                                  not support marks.
     */
    public boolean matches(InputStream src, boolean skipIfMatches)
            throws IOException {
        if (!src.markSupported()) {
            throw new IllegalArgumentException("src does not support marks");
        }

        src.mark(this.sequence.length);

        // Read until a non-matching byte occurs or the end of the sequence
        // is reached.

        for (int seqByte : this.sequence) {
            try {
                if (seqByte == src.read()) {
                    continue;
                }
            } catch (EOFException ignored) { }

            src.reset();
            return false;
        }

        if (!skipIfMatches) {
            src.reset();
        }

        return true;
    }

    /**
     * Tests, if the next couple of bytes of the <code>src</code> stream match
     * the byte sequence that this matcher was initialized with.
     *
     * @param src   The stream that shall be analyzed and that needs to support
     *              marks.
     *
     * @return  Either <code>true</code>, if the next couple of bytes match the
     *          byte sequence of this matcher, otherwise <code>false</code>.
     *
     * @throws IOException  If an {@link IOException} occurs when dealing with
     *                      the <code>src</code> stream.
     *
     * @throws IllegalArgumentException If the specified <code>src</code> does
     *                                  not support marks.
     */
    public boolean matches(InputStream src) throws IOException {
        return this.matches(src, false);
    }

    /**
     * Skips the next couple of bytes, if they match the byte sequence of this
     * matcher.
     *
     * @param src   The stream, whose next couple of bytes shall be skipped, if
     *              they match the byte sequence of this matcher. It is
     *              required to support marks.
     *
     * @return  Either <code>true</code>, if the next couple of bytes matched
     *          this matcher's byte sequence and have been skipped, otherwise
     *          <code>false</code>.
     *
     * @throws IOException  If an {@link IOException} occurs when dealing with
     *                      the <code>src</code> stream.
     *
     * @throws IllegalArgumentException If the specified <code>src</code> does
     *                                  not support marks.
     */
    public boolean skip(InputStream src) throws IOException {
        return this.matches(src, true);
    }

    /**
     * Returns the byte sequence that this matcher was initialized with.
     *
     * @return  The byte sequence of this matcher instance
     */
    public byte[] getByteSequence() {
        byte[] byteSequence = new byte[this.sequence.length];

        for (int index = 0; index < byteSequence.length; index++) {
            byteSequence[index] = (byte) this.sequence[index];
        }

        return byteSequence;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.sequence);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SequenceMatcher) {
            return Arrays.equals(
                    ((SequenceMatcher) obj).sequence,
                    this.sequence
            );
        }

        return false;
    }
}
