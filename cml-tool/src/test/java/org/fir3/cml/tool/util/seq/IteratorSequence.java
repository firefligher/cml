package org.fir3.cml.tool.util.seq;

import java.util.Arrays;
import java.util.Iterator;

public final class IteratorSequence<TElement>
        extends AbstractSequence<TElement> {
    private final Iterator<TElement> source;

    @SafeVarargs
    public IteratorSequence(TElement... elements) {
        this(Arrays.asList(elements));
    }

    public IteratorSequence(Iterable<TElement> src) {
        this(src.iterator());
    }

    public IteratorSequence(Iterator<TElement> src) {
        this.source = src;
    }

    @Override
    protected TElement read0() {
        if (this.source.hasNext()) {
            return this.source.next();
        }

        return null;
    }

    @Override
    protected void close0() { }
}
