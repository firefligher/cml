package org.fir3.cml.tool.tokenizer;

import org.fir3.cml.tool.exception.TokenizerException;
import org.fir3.cml.tool.util.seq.InputStreamSequence;
import org.fir3.cml.tool.util.seq.Sequence;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TokenizerTest {
    private static final Token[] SAMPLE_1_TOKENS = {
            // Line 1
            new KeywordToken(KeywordToken.Keyword.Ubiquitous),
            new KeywordToken(KeywordToken.Keyword.Domain),
            new IdentifierToken("org.fir3.cml.builtin"),
            new KeywordToken(KeywordToken.Keyword.Semicolon),

            // Line 3
            new KeywordToken(KeywordToken.Keyword.Builtin),
            new KeywordToken(KeywordToken.Keyword.Model),
            new IdentifierToken("Bit"),
            new KeywordToken(KeywordToken.Keyword.Semicolon),

            // Line 4
            new KeywordToken(KeywordToken.Keyword.Builtin),
            new KeywordToken(KeywordToken.Keyword.Model),
            new IdentifierToken("Sequence"),
            new KeywordToken(KeywordToken.Keyword.LeftChevron),
            new IdentifierToken("ElementType"),
            new KeywordToken(KeywordToken.Keyword.RightChevron),
            new KeywordToken(KeywordToken.Keyword.Semicolon),

            // Line 6
            new KeywordToken(KeywordToken.Keyword.Model),
            new IdentifierToken("Test"),

            // Line 7
            new KeywordToken(KeywordToken.Keyword.LeftBrace),

            // Line 8
            new IdentifierToken("Bit"),
            new IdentifierToken("_P1Val"),
            new KeywordToken(KeywordToken.Keyword.Semicolon),

            // Line 9
            new IdentifierToken("Sequence"),
            new KeywordToken(KeywordToken.Keyword.LeftChevron),
            new IdentifierToken("Bit"),
            new KeywordToken(KeywordToken.Keyword.RightChevron),
            new IdentifierToken("_P2Val"),
            new KeywordToken(KeywordToken.Keyword.Semicolon),

            // Line 11
            new KeywordToken(KeywordToken.Keyword.RightBrace)
    };

    @Test
    public void testTokenizer() throws IOException, TokenizerException {
        Queue<Token> tokens = new LinkedList<>();

        try (Sequence<Byte> src = new InputStreamSequence(
                TokenizerTest.class.getResourceAsStream("/cml/sample1.cml")
        )) {
            Tokenizer tknzr = new Tokenizer(src);
            Token token;

            while ((token = tknzr.nextToken().orElse(null)) != null) {
                tokens.offer(token);
            }
        }

        assertArrayEquals(
                TokenizerTest.SAMPLE_1_TOKENS,
                tokens.toArray(new Token[0])
        );
    }
}
