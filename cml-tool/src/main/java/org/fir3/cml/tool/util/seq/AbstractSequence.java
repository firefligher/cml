package org.fir3.cml.tool.util.seq;

import java.io.IOException;
import java.util.Arrays;

/**
 * An abstract implementation of the {@link Sequence} interface that provides a
 * generic implementation of the mark-mechanism for derived classes.
 *
 * @param <TElement>    The type of the elements that can be read from this
 *                      sequence.
 */
public abstract class AbstractSequence<TElement>
        implements Sequence<TElement> {
    /**
     * The number of elements that have been buffered at the beginning of the
     * {@link #buffer} and shall be read from there before the next element is
     * directly read from the sequence by calling {@link #read0()}.
     */
    private int readBuffer;

    /**
     * The index of the slot of {@link #buffer} that the next element, which is
     * read by calling {@link #read0()}, shall be stored at.
     */
    private int bufferPointer;

    /**
     * An array that stores the buffered elements.
     */
    private TElement[] buffer;

    @SuppressWarnings("unchecked")
    protected AbstractSequence() {
        this.buffer = (TElement[]) new Object[0];
    }

    /**
     * Reads the next element from the sequence.
     *
     * @return  Either the next element of the sequence or <code>null</code>,
     *          if the the end of the sequence has been reached.
     *
     * @throws IOException  If reading from the sequence fails due to
     *                      input-/output-issues.
     */
    protected abstract TElement read0() throws IOException;

    /**
     * Closes the sequence and releases any allocated resources.
     *
     * @throws IOException  If closing the sequence fails due to
     *                      input-/output-issues.
     */
    protected abstract void close0() throws IOException;

    @Override
    public final TElement read() throws IOException {
        // If the readBuffer is greater than zero, the requested element will
        // be read from the beginning of the buffer.

        if (this.readBuffer > 0) {
            int elementIndex = this.bufferPointer - this.readBuffer;
            this.readBuffer--;
            return this.buffer[elementIndex];
        }

        // Otherwise, the next element will be read from the actual sequence
        // implementation by calling read0. Also, if the bufferPointer is
        // pointing to a valid index of buffer, the read element will be stored
        // there.

        TElement element = this.read0();

        if (this.bufferPointer < this.buffer.length) {
            this.buffer[this.bufferPointer] = element;
            this.bufferPointer++;
        }

        return element;
    }

    @Override
    public void mark(int elementCount) {
        // If elementCount is greater than the total length of the buffer
        // array, we need to enlarge the buffer (without loosing its current
        // data).

        if (elementCount > this.buffer.length) {
            this.buffer = Arrays.copyOf(this.buffer, elementCount);
        }

        // Moving the elements of the readBuffer to the beginning of the
        // buffer (if there are any).

        System.arraycopy(
                this.buffer, this.bufferPointer - this.readBuffer,
                this.buffer, 0, this.readBuffer
        );

        // Adjusting the bufferPointer to match the new buffer

        this.bufferPointer = this.readBuffer;
    }

    @Override
    public void reset() {
        this.readBuffer = this.bufferPointer;
    }

    @Override
    public final void close() throws IOException {
        this.readBuffer = 0;
        this.bufferPointer = 0;
        this.close0();
    }
}
