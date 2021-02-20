package org.fir3.cml.tool.tokenizer;

import java.util.Objects;

/**
 * A token that represents an identifier.
 *
 * <p>
 *     Note that an identifier may be both, a domain identifier or an
 *     attribute-/model-identifier, although these two differ on the set of
 *     characters that they are allowed to consist of.
 * </p>
 */
public final class IdentifierToken implements Token {
    private final String identifier;

    public IdentifierToken(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the identifier that this token represents.
     *
     * @return  The identifier that this token represents
     */
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public int hashCode() {
        return this.identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IdentifierToken) {
            return Objects.equals(
                    ((IdentifierToken) obj).identifier,
                    this.identifier
            );
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format(
                "IdentifierToken(identifier=%s)",
                this.identifier
        );
    }
}
