package org.fir3.cml.tool.util.seq;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

/**
 * A utility to test if the next elements of a {@link Sequence} match a certain
 * subsequence.
 *
 * @param <TElement>    The type of the elements that this
 *                      <code>SequenceMatcher</code> matches with
 */
public final class SequenceMatcher<TElement> {
    private final TElement[] sequence;

    /**
     * Initializes the matcher with the specified <code>sequence</code> of
     * elements.
     *
     * @param sequence  The element subsequence that this instance will be able
     *                  to recognize in a {@link Sequence}. This is required to
     *                  consist of at least one element.
     *
     * @throws IllegalArgumentException If the specified <code>sequence</code>
     *                                  is empty.
     */
    @SafeVarargs
    public SequenceMatcher(TElement... sequence) {
        if (sequence.length < 1) {
            throw new IllegalArgumentException("sequence too short");
        }

        this.sequence = Arrays.copyOf(sequence, sequence.length);
    }

    /**
     * Tests, if the next elements of the specified <code>src</code> sequence
     * match the element subsequence that this matcher was initialized with.
     *
     * @param src           The source sequence.
     * @param skipIfMatches If this flag is enabled, the method will not rewind
     *                      the <code>src</code> sequence to its initial state,
     *                      if the element subsequence of this matcher was
     *                      recognized.
     *
     * @return  Either <code>true</code>, if the element subsequence of this
     *          matcher was recognized (and skipped, if
     *          <code>skipIfMatches</code> was enabled), otherwise
     *          <code>false</code>.
     *
     * @throws IOException  If an {@link IOException} occurs when dealing with
     *                      the <code>src</code> sequence.
     */
    public boolean matches(Sequence<TElement> src, boolean skipIfMatches)
            throws IOException {
        try (Sequence.Mark mark = src.mark()) {
            // Read until a non-matching byte occurs or the end of the sequence
            // is reached.

            for (TElement seqByte : this.sequence) {
                try {
                    if (seqByte == src.read()) {
                        continue;
                    }
                } catch (EOFException ignored) {
                }

                mark.reset();
                return false;
            }

            if (!skipIfMatches) {
                mark.reset();
            }
        }

        return true;
    }

    /**
     * Tests, if the next couple of bytes of the <code>src</code> sequence
     * match the element subsequence that this matcher was initialized with.
     *
     * @param src   The sequence that shall be analyzed.
     *
     * @return  Either <code>true</code>, if the next couple of elements match
     *          the element sequence of this matcher, otherwise
     *          <code>false</code>.
     *
     * @throws IOException  If an {@link IOException} occurs when dealing with
     *                      the <code>src</code> sequence.
     */
    public boolean matches(Sequence<TElement> src) throws IOException {
        return this.matches(src, false);
    }

    /**
     * Skips the next couple of elements, if they match the element subsequence
     * of this matcher.
     *
     * @param src   The sequence, whose next couple of elements shall be
     *              skipped, if they match the element subsequence of this
     *              matcher.
     *
     * @return  Either <code>true</code>, if the next couple of elements
     *          matched this matcher's element subsequence and have been
     *          skipped, otherwise <code>false</code>.
     *
     * @throws IOException  If an {@link IOException} occurs when dealing with
     *                      the <code>src</code> sequence.
     */
    public boolean skip(Sequence<TElement> src) throws IOException {
        return this.matches(src, true);
    }

    /**
     * Returns the element sequence that this matcher was initialized with.
     *
     * @return  The element sequence of this matcher instance
     */
    public TElement[] getSequence() {
        return Arrays.copyOf(this.sequence, this.sequence.length);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.sequence);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SequenceMatcher) {
            return Arrays.equals(
                    ((SequenceMatcher<?>) obj).sequence,
                    this.sequence
            );
        }

        return false;
    }
}
