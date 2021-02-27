package org.fir3.cml.tool.util.seq;

import java.io.IOException;
import java.util.*;

/**
 * An abstract implementation of the {@link Sequence} interface that provides a
 * generic implementation of the mark-mechanism for derived classes.
 *
 * @param <TElement>    The type of the elements that can be read from this
 *                      sequence.
 */
public abstract class AbstractSequence<TElement>
        implements Sequence<TElement> {

    private class MarkImpl implements Mark {
        private final List<TElement> buffer;
        private final int uniqueDataOffset;

        public MarkImpl(List<TElement> buffer) {
            this.buffer = buffer;
            this.uniqueDataOffset = buffer.size();
        }

        @Override
        public void reset() {
            AbstractSequence.this.reset(this);
        }

        @Override
        public void close() {
            AbstractSequence.this.remove(this);
        }

        public void add(TElement element) {
            this.buffer.add(element);
        }

        public void addAll(Collection<TElement> elements) {
            this.buffer.addAll(elements);
        }

        public List<TElement> getBuffer() {
            return this.buffer;
        }

        public List<TElement> getUniqueBuffer() {
            if (this.uniqueDataOffset >= this.buffer.size()) {
                return Collections.emptyList();
            }

            return this.buffer.subList(
                    this.uniqueDataOffset,
                    this.buffer.size()
            );
        }
    }

    private final Stack<MarkImpl> marks;
    private final Queue<TElement> buffer;

    protected AbstractSequence() {
        this.marks = new Stack<>();
        this.buffer = new LinkedList<>();
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
    public TElement read() throws IOException {
        if (!this.buffer.isEmpty()) {
            return this.buffer.poll();
        }

        TElement nextElement = this.read0();

        if (!this.marks.empty()) {
            this.marks.peek().add(nextElement);
        }

        return nextElement;
    }

    @Override
    public Mark mark() {
        return this.marks.push(new MarkImpl(new ArrayList<>(this.buffer)));
    }

    @Override
    public void close() throws IOException {
        this.buffer.clear();
        this.marks.clear();

        this.close0();
    }

    @SuppressWarnings("unchecked")
    private void reset(Mark mark) {
        this.removeMarksAbove(mark);

        // Setting the buffer to the buffer of the mark

        MarkImpl typedMark = (MarkImpl) mark;

        this.buffer.clear();
        this.buffer.addAll(typedMark.getBuffer());
    }

    @SuppressWarnings("unchecked")
    private void remove(Mark mark) {
        this.removeMarksAbove(mark);

        // Removing the mark from the marks stack and, if there is a mark
        // bellow, copying the buffer of the original mark to its parent.

        this.marks.pop();

        if (this.marks.empty()) {
            return;
        }

        this.marks.peek().addAll(((MarkImpl) mark).getUniqueBuffer());
    }

    private void removeMarksAbove(Mark mark) {
        while (!this.marks.empty()) {
            Mark topmostMark = this.marks.peek();

            if (topmostMark == mark) {
                break;
            }

            this.remove(topmostMark);
        }

        if (this.marks.empty()) {
            throw new IllegalArgumentException("Invalid mark");
        }
    }
}
