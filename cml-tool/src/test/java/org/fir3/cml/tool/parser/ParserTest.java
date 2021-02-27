package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Domain;
import org.fir3.cml.api.model.Model;
import org.fir3.cml.api.model.TypeParameter;
import org.fir3.cml.tool.tokenizer.IdentifierToken;
import org.fir3.cml.tool.tokenizer.KeywordToken;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.IteratorSequence;
import org.fir3.cml.tool.util.seq.Sequence;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {
    @Test
    public void testParseDomain() throws IOException {
        try (Sequence<Token> src = new IteratorSequence<>(
                new KeywordToken(KeywordToken.Keyword.Domain),
                new IdentifierToken("Test42Domain1337"),
                new KeywordToken(KeywordToken.Keyword.Semicolon)
        )) {
            Parser parser = new Parser(src);

            Optional<Domain> nullableDomain = parser.parse();
            assertTrue(nullableDomain.isPresent());

            Domain domain = nullableDomain.get();
            assertEquals("Test42Domain1337", domain.getName());
            assertEquals(EnumSet.noneOf(Domain.Flag.class), domain.getFlags());
            assertTrue(domain.getModels().isEmpty());
        }
    }

    @Test
    public void testParseDomainWithGenericBuiltinModel()
            throws IOException {
        try (Sequence<Token> src = new IteratorSequence<>(
                new KeywordToken(KeywordToken.Keyword.Domain),
                new IdentifierToken("7TestDOMAIN"),
                new KeywordToken(KeywordToken.Keyword.Dot),
                new IdentifierToken("43"),
                new KeywordToken(KeywordToken.Keyword.Semicolon),
                new KeywordToken(KeywordToken.Keyword.Builtin),
                new KeywordToken(KeywordToken.Keyword.Model),
                new IdentifierToken("generic_test_model"),
                new KeywordToken(KeywordToken.Keyword.LeftChevron),
                new IdentifierToken("p1"),
                new KeywordToken(KeywordToken.Keyword.Comma),
                new IdentifierToken("PARAM2"),
                new KeywordToken(KeywordToken.Keyword.Comma),
                new IdentifierToken("_3"),
                new KeywordToken(KeywordToken.Keyword.RightChevron),
                new KeywordToken(KeywordToken.Keyword.Semicolon)
        )) {
            Parser parser = new Parser(src);

            Optional<Domain> nullableDomain = parser.parse();
            assertTrue(nullableDomain.isPresent());

            // Assert domain metadata

            Domain domain = nullableDomain.get();
            assertEquals("7TestDOMAIN.43", domain.getName());
            assertTrue(domain.getFlags().isEmpty());

            // Assert model collection

            Set<Model> models = domain.getModels();
            assertEquals(1, models.size());

            // Assert generic builtin-in model

            Model model = models.stream().findFirst().orElse(null);
            assertNotNull(model);
            assertEquals("generic_test_model", model.getName());
            assertEquals(EnumSet.of(Model.Flag.Builtin), model.getFlags());
            assertTrue(model.getAttributes().isEmpty());
            assertEquals(
                    new HashSet<>(Arrays.asList("p1", "PARAM2", "_3")),
                    model.getTypeParameters()
                            .stream()
                            .map(TypeParameter::getName)
                            .collect(Collectors.toSet())
            );
        }
    }

    @Test
    public void testParseUbiquitousDomainWithBuiltinModel()
            throws IOException {
        try (Sequence<Token> src = new IteratorSequence<>(
                new KeywordToken(KeywordToken.Keyword.Ubiquitous),
                new KeywordToken(KeywordToken.Keyword.Domain),
                new IdentifierToken("org"),
                new KeywordToken(KeywordToken.Keyword.Dot),
                new IdentifierToken("example"),
                new KeywordToken(KeywordToken.Keyword.Dot),
                new IdentifierToken("test"),
                new KeywordToken(KeywordToken.Keyword.Semicolon),
                new KeywordToken(KeywordToken.Keyword.Builtin),
                new KeywordToken(KeywordToken.Keyword.Model),
                new IdentifierToken("__TestModel__"),
                new KeywordToken(KeywordToken.Keyword.Semicolon)
        )) {
            Parser parser = new Parser(src);

            Optional<Domain> nullableDomain = parser.parse();
            assertTrue(nullableDomain.isPresent());

            // Validate the domain metadata

            Domain domain = nullableDomain.get();
            assertEquals("org.example.test", domain.getName());
            assertEquals(EnumSet.of(Domain.Flag.Ubiquitous), domain.getFlags());

            // Validate the models metadata

            Set<Model> models = domain.getModels();
            assertEquals(1, models.size());

            // Validate the only model

            Model testModel = models.stream().findFirst().orElse(null);
            assertNotNull(testModel);
            assertEquals("__TestModel__", testModel.getName());
            assertEquals(EnumSet.of(Model.Flag.Builtin), testModel.getFlags());
            assertTrue(testModel.getAttributes().isEmpty());
            assertTrue(testModel.getTypeParameters().isEmpty());
        }
    }
}
