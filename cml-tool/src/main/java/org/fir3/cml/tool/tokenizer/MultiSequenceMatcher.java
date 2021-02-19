package org.fir3.cml.tool.tokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * A utility that attempts to match multiple byte sequences within a single
 * call.
 */
final class MultiSequenceMatcher {
    /**
     * A comparator that may be used for sorting {@link SequenceMatcher}s by
     * their byte sequence length: The longer their byte sequence is, the
     * smaller their index will be in a sorted collection.
     */
    private static final Comparator<SequenceMatcher> MATCHER_COMPARATOR =
            (a, b) -> {
                byte[] aSeq = a.getByteSequence();
                byte[] bSeq = b.getByteSequence();

                if (aSeq.length != bSeq.length) {
                    return bSeq.length - aSeq.length;
                }

                return 0;
            };

    private final SequenceMatcher[] matchers;

    /**
     * Creates a new instance and initializes it with the specified
     * <code>matchers</code>.
     *
     * @param matchers  The matchers that this instance will consist of. It is
     *                  required that there are no two matchers that match the
     *                  same byte sequence.
     *
     * @throws IllegalArgumentException If <code>matchers</code> contains at
     *                                  least two matchers that match the very
     *                                  same byte sequence.
     */
    public MultiSequenceMatcher(SequenceMatcher... matchers) {
        // Validating that there are no two matchers that match the exact same
        // byte sequence.
        //
        // NOTE:    If two SequenceMatchers match the same byte sequence, they
        //          are equal.

        for (int i1 = 0; i1 < matchers.length; i1++) {
            for (int i2 = i1 + 1; i2 < matchers.length; i2++) {
                if (Objects.equals(matchers[i1], matchers[i2])) {
                    throw new IllegalArgumentException(
                            "Equal SequenceMatchers"
                    );
                }
            }
        }

        this.matchers = Arrays.copyOf(matchers, matchers.length);
        Arrays.sort(this.matchers, MultiSequenceMatcher.MATCHER_COMPARATOR);
    }

    /**
     * Tests, if the next couple of bytes of <code>src</code> match one of this
     * instance's {@link SequenceMatcher}s.
     *
     * @param src           The markable source of bytes.
     * @param skipIfMatches Whether the bytes that match shall be skipped or
     *                      not.
     *
     * @return  An {@link Optional} container that either contains the
     *          {@link SequenceMatcher} that matched the next couple of bytes,
     *          or <code>null</code>, if no bytes matched.
     *
     * @throws IOException  If an error occurs while dealing with the specified
     *                      <code>src</code>.
     *
     * @throws IllegalArgumentException If <code>src</code> does not support
     *                                  marks.
     */
    public Optional<SequenceMatcher> matches(
            InputStream src,
            boolean skipIfMatches
    ) throws IOException {
        for (SequenceMatcher matcher : this.matchers) {
            if (!matcher.matches(src, skipIfMatches)) {
                continue;
            }

            return Optional.of(matcher);
        }

        return Optional.empty();
    }

    /**
     * Tests, if the next couple of bytes of <code>src</code> match one of this
     * instance's {@link SequenceMatcher}s.
     *
     * @param src   The markable source of bytes.
     * @return  An {@link Optional} container that either contains the
     *          {@link SequenceMatcher} that matched the next couple of bytes,
     *          or <code>null</code>, if no bytes matched.
     *
     * @throws IOException  If an error occurs while dealing with the specified
     *                      <code>src</code>.
     *
     * @throws IllegalArgumentException If <code>src</code> does not support
     *                                  marks.
     */
    public Optional<SequenceMatcher> matches(InputStream src)
            throws IOException {
        for (SequenceMatcher matcher : this.matchers) {
            if (!matcher.matches(src)) {
                continue;
            }

            return Optional.of(matcher);
        }

        return Optional.empty();
    }

    /**
     * Skips the next couple of bytes, if one of this instance's matchers match
     * those bytes.
     *
     * @param src   The markable stream, whose next couple of bytes shall be
     *              skipped, if they match one of this instance's matchers.
     *
     * @return  A {@link Optional} container that either contains the
     *          {@link SequenceMatcher} instance that matched bytes, or
     *          <code>null</code>, if no matcher matched, and thus, no bytes
     *          have been skipped.
     *
     * @throws IOException  If an error occurs while dealing with the specified
     *                      <code>src</code>.
     *
     * @throws IllegalArgumentException If <code>src</code> does not support
     *                                  marks.
     */
    public Optional<SequenceMatcher> skip(InputStream src) throws IOException {
        for (SequenceMatcher matcher : this.matchers) {
            if (!matcher.skip(src)) {
                continue;
            }

            return Optional.of(matcher);
        }

        return Optional.empty();
    }
}
