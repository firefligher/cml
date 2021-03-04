package org.fir3.cml.api;

import org.fir3.cml.api.model.Type;
import org.fir3.cml.api.util.TypeHelper;

/**
 * A collection of all built-ins of CML.
 */
public final class Builtin {
    /**
     * A datatype that has exactly two different instances.
     */
    public static final Type TYPE_BIT = TypeHelper.fromString(
            "M:org.fir3.cml.__builtin__.Bit"
    );

    /**
     * A simple collection of elements.
     */
    public static final Type TYPE_SEQUENCE = TypeHelper.fromString(
            "org.fir3.cml.__builtin__.Sequence<P:P1>"
    );

    private Builtin() {
        throw new UnsupportedOperationException(
                "Do not instantiate this class"
        );
    }
}
