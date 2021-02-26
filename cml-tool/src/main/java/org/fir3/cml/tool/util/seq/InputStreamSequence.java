package org.fir3.cml.tool.util.seq;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * A {@link Sequence} implementation that simply wraps an {@link InputStream}
 * instance.
 */
public final class InputStreamSequence extends AbstractSequence<Integer> {
    private final InputStream source;

    /**
     * Initializes a new instance of <code>InputStreamSequence</code>, which
     * wraps the specified <code>src</code>.
     *
     * @param src   The instance of {@link InputStream} that will be wrapped by
     *              the new <code>InputStreamSequence</code> instance.
     *
     * @throws NullPointerException If <code>src</code> is <code>null</code>
     */
    public InputStreamSequence(InputStream src) {
        Objects.requireNonNull(src, "src is not allowed to be null");

        this.source = src;
    }

    @Override
    protected Integer read0() throws IOException {
        int nextByte = this.source.read();

        if (nextByte < 0) {
            return null;
        }

        return nextByte;
    }

    @Override
    protected void close0() throws IOException {
        this.source.close();
    }
}
