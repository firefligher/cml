package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Domain;
import org.fir3.cml.tool.tokenizer.IdentifierToken;
import org.fir3.cml.tool.tokenizer.KeywordToken;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.IteratorSequence;
import org.fir3.cml.tool.util.seq.Sequence;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DomainDeclarationParserTest {
    @Test
    public void testParse() throws IOException {
        ParserController ctrl = new ParserController();
        ctrl.register(DomainSegment.class, new DomainSegmentParser());

        try (Sequence<Token> src = new IteratorSequence<>(
                // First domain

                new KeywordToken(KeywordToken.Keyword.Ubiquitous),
                new KeywordToken(KeywordToken.Keyword.Domain),
                new IdentifierToken("test"),
                new KeywordToken(KeywordToken.Keyword.Dot),
                new IdentifierToken("domain"),
                new KeywordToken(KeywordToken.Keyword.Semicolon),

                // Second domain

                new KeywordToken(KeywordToken.Keyword.Domain),
                new IdentifierToken("2"),
                new KeywordToken(KeywordToken.Keyword.Dot),
                new IdentifierToken("test"),
                new KeywordToken(KeywordToken.Keyword.Dot),
                new IdentifierToken("domain"),
                new KeywordToken(KeywordToken.Keyword.Semicolon)
        )) {
            EntityParser<DomainDeclaration> parser =
                    new DomainDeclarationParser();

            // First declaration

            Optional<DomainDeclaration> nullableDeclaration = parser.parse(
                    src,
                    ctrl,
                    Environment.EMPTY_ENVIRONMENT
            );

            assertTrue(nullableDeclaration.isPresent());

            DomainDeclaration declaration = nullableDeclaration.get();
            assertEquals("test.domain", declaration.getName());
            assertTrue(declaration.getFlags().contains(
                    Domain.Flag.Ubiquitous
            ));

            // Second declaration

            nullableDeclaration = parser.parse(
                    src,
                    ctrl,
                    Environment.EMPTY_ENVIRONMENT
            );

            assertTrue(nullableDeclaration.isPresent());

            declaration = nullableDeclaration.get();
            assertEquals("2.test.domain", declaration.getName());
            assertTrue(declaration.getFlags().isEmpty());
        }
    }

    @Test
    public void testRollbackOnMissingDomainKeyword() throws IOException {
        ParserController ctrl = new ParserController();
        ctrl.register(DomainSegment.class, new DomainSegmentParser());

        try (Sequence<Token> src = new IteratorSequence<>(
                // First domain

                new IdentifierToken("first_domain"),
                new KeywordToken(KeywordToken.Keyword.Semicolon),

                // Second domain

                new KeywordToken(KeywordToken.Keyword.Ubiquitous),
                new IdentifierToken("second_domain"),
                new KeywordToken(KeywordToken.Keyword.Semicolon)
        )) {
            EntityParser<DomainDeclaration> parser =
                    new DomainDeclarationParser();

            // First invalid domain

            Optional<DomainDeclaration> nullableDeclaration = parser.parse(
                    src,
                    ctrl,
                    Environment.EMPTY_ENVIRONMENT
            );

            assertFalse(nullableDeclaration.isPresent());
            assertEquals(new IdentifierToken("first_domain"), src.read());
            assertEquals(
                    new KeywordToken(KeywordToken.Keyword.Semicolon),
                    src.read()
            );

            // Second invalid domain

            nullableDeclaration = parser.parse(
                    src,
                    ctrl,
                    Environment.EMPTY_ENVIRONMENT
            );

            assertFalse(nullableDeclaration.isPresent());
            assertEquals(
                    new KeywordToken(KeywordToken.Keyword.Ubiquitous),
                    src.read()
            );

            assertEquals(new IdentifierToken("second_domain"), src.read());
            assertEquals(
                    new KeywordToken(KeywordToken.Keyword.Semicolon),
                    src.read()
            );
        }
    }

    @Test
    public void testRollbackOnMissingIdentifier() throws IOException {
        ParserController ctrl = new ParserController();
        ctrl.register(DomainSegment.class, new DomainSegmentParser());

        try (Sequence<Token> src = new IteratorSequence<>(
                new KeywordToken(KeywordToken.Keyword.Domain),
                new KeywordToken(KeywordToken.Keyword.Semicolon)
        )) {
            EntityParser<DomainDeclaration> parser =
                    new DomainDeclarationParser();

            Optional<DomainDeclaration> nullableDeclaration = parser.parse(
                    src,
                    ctrl,
                    Environment.EMPTY_ENVIRONMENT
            );

            assertFalse(nullableDeclaration.isPresent());
            assertEquals(
                    new KeywordToken(KeywordToken.Keyword.Domain),
                    src.read()
            );

            assertEquals(
                    new KeywordToken(KeywordToken.Keyword.Semicolon),
                    src.read()
            );
        }
    }

    @Test
    public void testRollbackOnMissingSemicolon() throws IOException {
        ParserController ctrl = new ParserController();
        ctrl.register(DomainSegment.class, new DomainSegmentParser());

        try (Sequence<Token> src = new IteratorSequence<>(
                new KeywordToken(KeywordToken.Keyword.Domain),
                new IdentifierToken("test_domain")
        )) {
            EntityParser<DomainDeclaration> parser =
                    new DomainDeclarationParser();

            Optional<DomainDeclaration> nullableDeclaration = parser.parse(
                    src,
                    ctrl,
                    Environment.EMPTY_ENVIRONMENT
            );

            assertFalse(nullableDeclaration.isPresent());
            assertEquals(
                    new KeywordToken(KeywordToken.Keyword.Domain),
                    src.read()
            );

            assertEquals(
                    new IdentifierToken("test_domain"),
                    src.read()
            );
        }
    }
}
