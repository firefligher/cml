package org.fir3.cml.tool.parser;

import org.fir3.cml.tool.tokenizer.IdentifierToken;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.IteratorSequence;
import org.fir3.cml.tool.util.seq.Sequence;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class IdentifierParserTest {
    private static void expectIdentifier(
            String expected,
            Optional<Identifier> actual
    ) {
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get().getIdentifier());
    }

    @Test
    public void testParse() throws IOException {
        ParserController ctrl = new ParserController();

        try (Sequence<Token> src = new IteratorSequence<>(
                new IdentifierToken("firstToken"),
                new IdentifierToken("SecondToken"),
                new IdentifierToken("_ThirdToken"),
                new IdentifierToken("token123With456Numbers"),
                new IdentifierToken("_"),
                new IdentifierToken("Q"),
                new IdentifierToken("q"),
                new IdentifierToken("_5"),
                new IdentifierToken("z1"),
                new IdentifierToken("__")
        )) {
            EntityParser<Identifier> parser = new IdentifierParser();

            expectIdentifier(
                    "firstToken",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );

            expectIdentifier(
                    "SecondToken",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );

            expectIdentifier(
                    "_ThirdToken",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );

            expectIdentifier(
                    "token123With456Numbers",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );

            expectIdentifier(
                    "_",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );

            expectIdentifier(
                    "Q",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );

            expectIdentifier(
                    "q",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );

            expectIdentifier(
                    "_5",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );

            expectIdentifier(
                    "z1",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );

            expectIdentifier(
                    "__",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );
        }
    }

    @Test
    public void testRollbackOnFailure() throws IOException {
        ParserController ctrl = new ParserController();

        try (Sequence<Token> src = new IteratorSequence<>(
                new IdentifierToken("5")
        )) {
            EntityParser<Identifier> parser = new IdentifierParser();

            assertFalse(parser.parse(
                    src,
                    ctrl,
                    Environment.EMPTY_ENVIRONMENT
            ).isPresent());

            assertEquals(new IdentifierToken("5"), src.read());
        }
    }
}
