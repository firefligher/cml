package org.fir3.cml.tool.parser;

import org.fir3.cml.tool.tokenizer.IdentifierToken;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.IteratorSequence;
import org.fir3.cml.tool.util.seq.Sequence;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DomainSegmentParserTest {
    private static void expectSegment(
            String name,
            Optional<DomainSegment> segment
    ) {
        assertTrue(segment.isPresent());
        assertEquals(name, segment.get().getSegment());
    }

    @Test
    public void testParse() throws IOException {
        ParserController ctrl = new ParserController();

        try (Sequence<Token> src = new IteratorSequence<>(
                new IdentifierToken("textSegment"),
                new IdentifierToken("0123456789"),
                new IdentifierToken("42mixed1Segment1337"),
                new IdentifierToken("ALLCAPS"),
                new IdentifierToken("_mixed_with_underscore_12"),
                new IdentifierToken("k"),
                new IdentifierToken("K"),
                new IdentifierToken("7"),
                new IdentifierToken("_")
        )) {
            EntityParser<DomainSegment> parser = new DomainSegmentParser();

            expectSegment(
                    "textSegment",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );

            expectSegment(
                    "0123456789",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );

            expectSegment(
                    "42mixed1Segment1337",
                    parser.parse(src, ctrl, null)
            );

            expectSegment(
                    "ALLCAPS",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );

            expectSegment(
                    "_mixed_with_underscore_12",
                    parser.parse(src, ctrl, null)
            );

            expectSegment(
                    "k",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );

            expectSegment(
                    "K",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );

            expectSegment(
                    "7",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );

            expectSegment(
                    "_",
                    parser.parse(src, ctrl, Environment.EMPTY_ENVIRONMENT)
            );
        }
    }

    @Test
    public void testRollbackOnFailure() throws IOException {
        ParserController ctrl = new ParserController();

        try (Sequence<Token> src = new IteratorSequence<>(
                new IdentifierToken("-")
        )) {
            EntityParser<DomainSegment> parser = new DomainSegmentParser();

            assertFalse(parser.parse(
                    src,
                    ctrl,
                    Environment.EMPTY_ENVIRONMENT
            ).isPresent());

            assertEquals(new IdentifierToken("-"), src.read());
        }
    }
}
