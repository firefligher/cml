package org.fir3.cml.tool.util.seq;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractSequenceTest {
    private static final class TestSequence<TElement>
            extends AbstractSequence<TElement> {
        private final TElement[] buffer;
        private int bufferPointer;
        private boolean closed;

        @SafeVarargs
        public TestSequence(TElement... elements) {
            this.buffer = Arrays.copyOf(elements, elements.length);
        }

        @Override
        protected TElement read0() {
            if (!this.closed && this.bufferPointer < this.buffer.length) {
                return this.buffer[this.bufferPointer++];
            }

            return null;
        }

        @Override
        protected void close0() {
            this.closed = true;
        }
    }

    private static final Integer[] TEST_SEQUENCE = new Integer[] {
              1,   2,    3,     4, 5, 42, 1337, 1024,
            256, 126, 2048, 65536, 0,  0,   -1,   23
    };

    @Test
    public void testRead() throws IOException {
        Sequence<Integer> seq = new TestSequence<>(
                AbstractSequenceTest.TEST_SEQUENCE);

        // Asserting that all elements are read in the same order as they are
        // present in the TEST_SEQUENCE

        for (int value : AbstractSequenceTest.TEST_SEQUENCE) {
            assertEquals(value, seq.read());
        }

        seq.close();
    }

    @Test
    public void testMarkAndReset() throws IOException {
        Sequence<Integer> seq = new TestSequence<>(
                AbstractSequenceTest.TEST_SEQUENCE);

        // Setting up a mark of five

        seq.mark(5);

        // Reading the first five elements and asserting that they match the
        // ones of the original array

        for (int index = 0; index < 5; index++) {
            assertEquals(
                    AbstractSequenceTest.TEST_SEQUENCE[index],
                    seq.read()
            );
        }

        // Resetting the sequence (which should still be valid) and all
        // elements, including the first five

        seq.reset();

        for (int value : AbstractSequenceTest.TEST_SEQUENCE) {
            assertEquals(value, seq.read());
        }

        seq.close();
    }

    @Test
    public void testMarkAndResetPartially() throws IOException {
        Sequence<Integer> seq = new TestSequence<>(
                AbstractSequenceTest.TEST_SEQUENCE);

        // Setting up a mark of five

        seq.mark(5);

        // Reading only the first three elements

        for (int index = 0; index < 3; index++) {
            assertEquals(
                    AbstractSequenceTest.TEST_SEQUENCE[index],
                    seq.read()
            );
        }

        // Resetting the sequence and reading the total sequence

        seq.reset();

        for (int value : AbstractSequenceTest.TEST_SEQUENCE) {
            assertEquals(value, seq.read());
        }

        seq.close();
    }

    @Test
    public void testMarkAndResetEmpty() throws IOException {
        Sequence<Integer> seq = new TestSequence<>(
                AbstractSequenceTest.TEST_SEQUENCE);

        seq.mark(5);
        seq.reset();

        for (int value : AbstractSequenceTest.TEST_SEQUENCE) {
            assertEquals(value, seq.read());
        }

        seq.close();
    }
}
