package org.fir3.cml.tool.tokenizer;

/**
 * A token that represents a single keyword of CML.
 */
public final class KeywordToken implements Token {
    /**
     * An enumeration of all supported keywords.
     */
    public enum Keyword {
        Builtin("builtin"),
        Comma(","),
        Domain("domain"),
        Dot("."),
        LeftBrace("{"),
        LeftChevron("<"),
        Model("model"),
        RightBrace("}"),
        RightChevron(">"),
        Semicolon(";"),
        Ubiquitous("ubiquitous");

        private final String charSequence;

        Keyword(String charSequence) {
            this.charSequence = charSequence;
        }

        /**
         * Returns the character sequence that represents this keyword in CML
         * source code.
         *
         * @return  The character sequence that represents this keyword
         */
        public String getCharSequence() {
            return this.charSequence;
        }
    }

    private final Keyword keyword;

    public KeywordToken(Keyword keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the keyword that is represented by this token.
     *
     * @return  The keyword that is represented by this token
     */
    public Keyword getKeyword() {
        return this.keyword;
    }

    @Override
    public int hashCode() {
        return this.keyword.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KeywordToken) {
            return ((KeywordToken) obj).keyword == this.keyword;
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("KeywordToken(keyword=%s)", this.keyword);
    }
}
