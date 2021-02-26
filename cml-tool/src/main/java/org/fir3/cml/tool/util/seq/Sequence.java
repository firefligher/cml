package org.fir3.cml.tool.util.seq;

import java.io.Closeable;
import java.io.IOException;

/**
 * A sequence is a readable stream of objects that supports gradual rollbacks.
 */
public interface Sequence<TElement> extends Closeable {
    /**
     * The generic interface of the instance that will be returned when calling
     * {@link Sequence#mark()}.
     */
    interface Mark extends AutoCloseable {
        /**
         * Resets the corresponding {@link Sequence} to the marked state.
         */
        void reset();

        @Override
        void close();
    }

    /**
     * Reads the next element from the sequence.
     *
     * @return  Either the next element of the sequence or <code>null</code>,
     *          if the end of the sequence has been reached.
     *
     * @throws IOException  If an input-/output-error occurs while reading the
     *                      next element.
     */
    TElement read() throws IOException;

    /**
     * Marks the current state of the {@link Sequence} and enables the caller
     * to reset this {@link Sequence} instance to the current state by calling
     * {@link Mark#reset()} of the returned {@link Mark} instance.
     *
     * @return  The mark that has been created for this {@link Sequence}
     *          instance.
     */
    Mark mark();
}
