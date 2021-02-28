package org.fir3.cml.api.util;

import java.util.Objects;

/**
 * A pair is a 2-tuple.
 *
 * @param <TFirst>  The type of the first component.
 * @param <TSecond> The type of the second component.
 */
public final class Pair<TFirst, TSecond> {
    private final TFirst firstComponent;
    private final TSecond secondComponent;

    public Pair(TFirst firstComponent, TSecond secondComponent) {
        this.firstComponent = firstComponent;
        this.secondComponent = secondComponent;
    }

    public TFirst getFirstComponent() {
        return this.firstComponent;
    }

    public TSecond getSecondComponent() {
        return this.secondComponent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            Pair<?, ?> pair = (Pair<?, ?>) obj;

            return Objects.equals(this.firstComponent, pair.firstComponent) &&
                    Objects.equals(this.secondComponent, pair.secondComponent);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.firstComponent.hashCode() ^
                this.secondComponent.hashCode();
    }
}
