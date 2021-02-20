package org.fir3.cml.tool.tokenizer;

import org.fir3.cml.tool.exception.TokenizerException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The tokenizer reads {@link Token}s from a byte source.
 */
public final class Tokenizer implements Closeable {
    private static final MultiSequenceMatcher WHITESPACE_MATCHER =
            new MultiSequenceMatcher(
                    new SequenceMatcher((byte) 0x09),   // Tab
                    new SequenceMatcher((byte) 0x0A),   // Line feed
                    new SequenceMatcher(
                            (byte) 0x0D,                // Carriage Return
                            (byte) 0x0A                 // Line Feed
                    ),
                    new SequenceMatcher((byte) 0x20)    // Space
            );

    private static final SequenceMatcher COMMENT_START_MATCHER =
            new SequenceMatcher(
                    (byte) 0x2F,    // Slash
                    (byte) 0x2A     // Asterisk
            );

    private static final SequenceMatcher COMMENT_END_MATCHER =
            new SequenceMatcher(
                    (byte) 0x2A,    // Asterisk
                    (byte) 0x2F     // Slash
            );

    private static final Map<
            SequenceMatcher,
            KeywordToken.Keyword
            > KEYWORD_MATCHERS;

    private static final MultiSequenceMatcher KEYWORD_MATCHER;

    static {
        KEYWORD_MATCHERS = Arrays.stream(KeywordToken.Keyword.values())
                .map(k -> new AbstractMap.SimpleEntry<>(
                        new SequenceMatcher(
                                k.getCharSequence()
                                        .getBytes(StandardCharsets.US_ASCII)
                        ),
                        k
                ))
                .collect(Collectors.toMap(
                        AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue
                ));

        KEYWORD_MATCHER = new MultiSequenceMatcher(
                Tokenizer.KEYWORD_MATCHERS.keySet()
                        .toArray(new SequenceMatcher[0])
        );
    }

    private static boolean isIdentifierCharacter(int c) {
        return (c > 0x2F && c < 0x3A)       // Numbers
                || (c > 0x40 && c < 0x5B)   // Uppercase letters
                || (c > 0x60 && c < 0x7B)   // Lowercase letters
                || (c == 0x5F);             // Underscore
    }

    private final InputStream source;

    public Tokenizer(InputStream src) {
        // Since the tokenizer may need to rewind the source stream, we ensure
        // that the mark-feature will be supported by the internally stored
        // stream instance.

        if (src.markSupported()) {
            this.source = src;
        } else {
            // According to the Java documentation, the BufferedInputStream
            // always supports the mark-feature.

            this.source = new BufferedInputStream(src);
        }
    }

    @Override
    public void close() throws IOException {
        this.source.close();
    }

    /**
     * Read the next token from the byte source that this instance was
     * initialized with.
     *
     * @return  An {@link Optional} container that either contains a valid
     *          {@link Token} instance, or <code>null</code>, if the end of the
     *          underlying stream has been reached already.
     *
     * @throws TokenizerException   If the next bytes of the underlying stream
     *                              do not form a valid token.
     */
    public Optional<Token> nextToken() throws TokenizerException {
        boolean skipped;

        do {
            try {
                skipped = this.skipWhitespace() || this.skipComment();
            } catch (EOFException ignored) {
                return Optional.empty();
            } catch (IOException ex) {
                throw new TokenizerException(ex);
            }
        } while (skipped);

        Optional<SequenceMatcher> keywordMatcher;

        try {
            keywordMatcher = Tokenizer.KEYWORD_MATCHER.skip(this.source);
        } catch (IOException ex) {
            throw new TokenizerException(ex);
        }

        if (keywordMatcher.isPresent()) {
            return keywordMatcher.map(m -> new KeywordToken(
                    Tokenizer.KEYWORD_MATCHERS.get(m)
            ));
        }

        try {
            return Optional.of(new IdentifierToken(
                    this.parseIdentifier().orElseThrow(
                            () -> new TokenizerException("Unknown token")
                    )
            ));
        } catch (EOFException ignored) {
            return Optional.empty();
        } catch (IOException ex) {
            throw new TokenizerException(ex);
        }
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
            this.source.mark(1);
            int character = this.source.read();

            if (character == -1) {
                throw new EOFException();
            }

            if (!Tokenizer.isIdentifierCharacter(character)) {
                this.source.reset();
                break;
            }

            builder.append((char) character);
        }

        if (builder.length() == 0) {
            return Optional.empty();
        }

        return Optional.of(builder.toString());
    }
}
