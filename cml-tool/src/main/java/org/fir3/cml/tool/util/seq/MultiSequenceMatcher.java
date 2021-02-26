package org.fir3.cml.tool.util.seq;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * A utility that attempts to match multiple element sequences within a single
 * method-call.
 *
 * @param <TElement>    The type of elements that this instance of
 *                      <code>MultiSequenceMatcher</code> matches with.
 */
public final class MultiSequenceMatcher<TElement> {
    /**
     * A comparator that may be used for sorting {@link SequenceMatcher}s by
     * their byte sequence length: The longer their byte sequence is, the
     * smaller their index will be in a sorted collection.
     */
    private static final Comparator<SequenceMatcher<?>> MATCHER_COMPARATOR =
            (a, b) -> {
                Object[] aSeq = a.getSequence();
                Object[] bSeq = b.getSequence();

                if (aSeq.length != bSeq.length) {
                    return bSeq.length - aSeq.length;
                }

                return 0;
            };

    private final SequenceMatcher<TElement>[] matchers;

    /**
     * Creates a new instance and initializes it with the specified
     * <code>matchers</code>.
     *
     * @param matchers  The matchers that the new instance consist of. It is
     *                  required that there are no two matchers that match the
     *                  same element sequence.
     *
     * @throws IllegalArgumentException If <code>matchers</code> contains at
     *                                  least two matchers that match the very
     *                                  same element sequence.
     */
    @SafeVarargs
    public MultiSequenceMatcher(SequenceMatcher<TElement>... matchers) {
        // Validating that there are no two matchers that match the exact same
        // element sequence.
        //
        // NOTE:    If two SequenceMatchers match the same element sequence,
        //          they are equal.

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
     * Tests, if the next couple of elements of <code>src</code> match one of
     * this instance's {@link SequenceMatcher}s.
     *
     * @param src           The source sequence.
     * @param skipIfMatches Whether the elements that match shall be skipped or
     *                      not.
     *
     * @return  An {@link Optional} container that either contains the
     *          {@link SequenceMatcher} that matched the next couple of
     *          elements, or <code>null</code>, if no matcher matched.
     *
     * @throws IOException  If an error occurs while dealing with the specified
     *                      <code>src</code>.
     */
    public Optional<SequenceMatcher<TElement>> matches(
            Sequence<TElement> src,
            boolean skipIfMatches
    ) throws IOException {
        for (SequenceMatcher<TElement> matcher : this.matchers) {
            if (!matcher.matches(src, skipIfMatches)) {
                continue;
            }

            return Optional.of(matcher);
        }

        return Optional.empty();
    }

    /**
     * Tests, if the next couple of elements of <code>src</code> match one of
     * this instance's {@link SequenceMatcher}s.
     *
     * @param src   The source sequence.
     * @return  An {@link Optional} container that either contains the
     *          {@link SequenceMatcher} that matched the next couple of
     *          elements, or <code>null</code>, if no matcher matched.
     *
     * @throws IOException  If an error occurs while dealing with the specified
     *                      <code>src</code>.
     */
    public Optional<SequenceMatcher<TElement>> matches(Sequence<TElement> src)
            throws IOException {
        for (SequenceMatcher<TElement> matcher : this.matchers) {
            if (!matcher.matches(src)) {
                continue;
            }

            return Optional.of(matcher);
        }

        return Optional.empty();
    }

    /**
     * Skips the next couple of elements, if one of this instance's matchers
     * match those elements.
     *
     * @param src   The sequence, whose next couple of elements shall be
     *              skipped, if they match one of this instance's matchers.
     *
     * @return  A {@link Optional} container that either contains the
     *          {@link SequenceMatcher} instance that matched the elements, or
     *          <code>null</code>, if no matcher matched, and thus, no elements
     *          have been skipped.
     *
     * @throws IOException  If an error occurs while dealing with the specified
     *                      <code>src</code>.
     */
    public Optional<SequenceMatcher<TElement>> skip(Sequence<TElement> src)
            throws IOException {
        for (SequenceMatcher<TElement> matcher : this.matchers) {
            if (!matcher.skip(src)) {
                continue;
            }

            return Optional.of(matcher);
        }

        return Optional.empty();
    }
}
