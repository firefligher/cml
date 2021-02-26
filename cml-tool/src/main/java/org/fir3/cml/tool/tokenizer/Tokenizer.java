package org.fir3.cml.tool.tokenizer;

import org.fir3.cml.tool.exception.TokenizerException;
import org.fir3.cml.tool.util.seq.AbstractSequence;
import org.fir3.cml.tool.util.seq.MultiSequenceMatcher;
import org.fir3.cml.tool.util.seq.Sequence;
import org.fir3.cml.tool.util.seq.SequenceMatcher;

import java.io.EOFException;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The tokenizer reads {@link Token}s from a byte source.
 */
public final class Tokenizer extends AbstractSequence<Token> {
    @SuppressWarnings("unchecked")
    private static final SequenceMatcher<Byte>[] EMPTY_SEQUENCE_MATCHER_ARRAY =
            new SequenceMatcher[0];


    private static final MultiSequenceMatcher<Byte> WHITESPACE_MATCHER =
            new MultiSequenceMatcher<>(
                    new SequenceMatcher<>((byte) 0x09), // Tab
                    new SequenceMatcher<>((byte) 0x0A), // Line feed
                    new SequenceMatcher<>(
                            (byte) 0x0D,                // Carriage Return
                            (byte) 0x0A                 // Line Feed
                    ),
                    new SequenceMatcher<>((byte) 0x20)  // Space
            );

    private static final SequenceMatcher<Byte> COMMENT_START_MATCHER =
            new SequenceMatcher<>(
                    (byte) 0x2F,    // Slash
                    (byte) 0x2A     // Asterisk
            );

    private static final SequenceMatcher<Byte> COMMENT_END_MATCHER =
            new SequenceMatcher<>(
                    (byte) 0x2A,    // Asterisk
                    (byte) 0x2F     // Slash
            );

    private static final Map<
            SequenceMatcher<Byte>,
            KeywordToken.Keyword
            > KEYWORD_MATCHERS;

    private static final MultiSequenceMatcher<Byte> KEYWORD_MATCHER;

    static {
        KEYWORD_MATCHERS = Arrays.stream(KeywordToken.Keyword.values())
                .map(k -> new AbstractMap.SimpleEntry<>(
                        new SequenceMatcher<>(
                                k.getCharSequence()
                                        .chars()
                                        .mapToObj(c -> (byte) c)
                                        .toArray(Byte[]::new)
                        ),
                        k
                ))
                .collect(Collectors.toMap(
                        AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue
                ));

        KEYWORD_MATCHER = new MultiSequenceMatcher<>(
                Tokenizer.KEYWORD_MATCHERS.keySet()
                        .toArray(Tokenizer.EMPTY_SEQUENCE_MATCHER_ARRAY)
        );
    }

    private static boolean isIdentifierCharacter(int c) {
        return (c > 0x2F && c < 0x3A)       // Numbers
                || (c > 0x40 && c < 0x5B)   // Uppercase letters
                || (c > 0x60 && c < 0x7B)   // Lowercase letters
                || (c == 0x5F);             // Underscore
    }

    private final Sequence<Byte> source;

    /**
     * Creates a new instance of <code>Tokenizer</code> and initializes it with
     * the passed <code>src</code> sequence.
     *
     * @param src   The byte sequence that this tokenizer operates on
     */
    public Tokenizer(Sequence<Byte> src) {
        this.source = src;
    }

    @Override
    protected Token read0() throws IOException {
        boolean skipped;

        do {
            try {
                skipped = this.skipWhitespace() || this.skipComment();
            } catch (EOFException ignored) {
                return null;
            } catch (IOException ex) {
                throw new TokenizerException(ex);
            }
        } while (skipped);

        Optional<SequenceMatcher<Byte>> keywordMatcher;

        try {
            keywordMatcher = Tokenizer.KEYWORD_MATCHER.skip(this.source);
        } catch (IOException ex) {
            throw new TokenizerException(ex);
        }

        if (keywordMatcher.isPresent()) {
            return new KeywordToken(Tokenizer.KEYWORD_MATCHERS.get(
                    keywordMatcher.get()
            ));
        }

        try {
            Optional<String> identifier = this.parseIdentifier();

            if (!identifier.isPresent()) {
                throw new TokenizerException("Unknown token");
            }

            return new IdentifierToken(identifier.get());
        } catch (EOFException ignored) {
            return null;
        } catch (IOException ex) {
            throw new TokenizerException(ex);
        }
    }

    @Override
    protected void close0() throws IOException {
        this.source.close();
    }

    private boolean skipWhitespace() throws IOException {
        return Tokenizer.WHITESPACE_MATCHER.skip(
                this.source
        ).isPresent();
    }

    private boolean skipComment() throws IOException {
        if (!Tokenizer.COMMENT_START_MATCHER.skip(this.source)) {
            return false;
        }

        while (!Tokenizer.COMMENT_END_MATCHER.skip(this.source)) {
            this.source.read();
        }

        return true;
    }

    private Optional<String> parseIdentifier() throws IOException {
        StringBuilder builder = new StringBuilder();

        while (true) {
            Byte character;

            try (Mark mark = this.source.mark()) {
                character = this.source.read();

                if (character == null) {
                    throw new EOFException();
                }

                if (!Tokenizer.isIdentifierCharacter(character)) {
                    mark.reset();
                    break;
                }
            }

            builder.append((char) (int) character);
        }

        if (builder.length() == 0) {
            return Optional.empty();
        }

        return Optional.of(builder.toString());
    }
}
