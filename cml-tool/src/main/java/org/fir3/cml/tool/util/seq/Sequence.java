package org.fir3.cml.tool.util.seq;

import java.io.Closeable;
import java.io.IOException;

/**
 * A sequence is a limited-seekable stream of objects that has similarities to
 * Java's {@link java.io.InputStream}.
 */
public interface Sequence<TElement> extends Closeable {
    /**
     * Reads the next element from the sequence.
     *
     * @return  Either the next element of the sequence or <code>null</code>,
     *          if the end of the stream has been reached.
     *
     * @throws IOException  If an input-/output-error occurs while reading the
     *                      next element.
     */
    TElement read() throws IOException;

    /**
     * Remembers the current position in the sequence and buffers all elements,
     * that will be read from this sequence, until <code>elementCount</code> is
     * reached.
     *
     * Until <code>elementCount</code> is reached, the mark stays valid you may
     * rewind the sequence back to the last mark's state by calling
     * {@link #reset()}.
     *
     * @param elementCount  The number of elements that can be read safely
     *                      without invalidating the new mark.
     */
    void mark(int elementCount);

    /**
     * Rewinds the sequence to the last valid mark that has been created by
     * calling {@link #mark(int)}.
     *
     * If there is no valid mark (anymore), the behavior of this method is
     * undefined and the sequence may be in an unsafe state after the
     * method-call returns.
     */
    void reset();
}
