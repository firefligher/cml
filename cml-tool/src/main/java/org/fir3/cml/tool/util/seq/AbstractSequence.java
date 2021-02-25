package org.fir3.cml.tool.util.seq;

import java.io.IOException;

/**
 * An abstract implementation of the {@link Sequence} interface that provides a
 * generic implementation of the mark-mechanism for derived classes.
 *
 * @param <TElement>    The type of the elements that can be read from this
 *                      sequence.
 */
public abstract class AbstractSequence<TElement>
        implements Sequence<TElement> {
    private int bufferPointer;
    private TElement[] buffer;

    protected AbstractSequence() {
        this.buffer = this.newArray(0);
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
        TElement nextElement = null;

        // If the value of bufferPointer is greater than zero, the next element
        // is taken from the internal buffer. If this element is null, or
        // bufferPointer is equal to zero, the element it is read directly from
        // the sequence.

        if (this.bufferPointer > 0) {
            this.bufferPointer--;
            nextElement = this.buffer[this.bufferPointer];
        }

        if (nextElement == null) {
            nextElement = this.read0();

            // If bufferPointer is less than zero, we buffer the element

            if (this.bufferPointer < 0) {
                this.bufferPointer++;
                this.buffer[-this.bufferPointer] = nextElement;
            }
        }

        return nextElement;
    }

    @Override
    public void mark(int elementCount) {
        if (this.buffer.length < elementCount) {
            this.buffer = this.newArray(elementCount);
        }

        this.bufferPointer = -elementCount;
    }

    @Override
    public void reset() {
        this.bufferPointer = this.buffer.length;
    }

    @Override
    public final void close() throws IOException {
        this.bufferPointer = 0;
        this.close0();
    }

    @SuppressWarnings("unchecked")
    private TElement[] newArray(int size) {
        return (TElement[]) new Object[size];
    }
}
