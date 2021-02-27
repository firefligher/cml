package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Model;
import org.fir3.cml.tool.tokenizer.IdentifierToken;
import org.fir3.cml.tool.tokenizer.KeywordToken;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.IteratorSequence;
import org.fir3.cml.tool.util.seq.Sequence;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ModelDeclarationParserTest {
    @Test
    public void testParse() throws IOException {
        ParserController ctrl = new ParserController();
        ctrl.register(DomainSegment.class, new DomainSegmentParser());
        ctrl.register(Identifier.class, new IdentifierParser());

        try (Sequence<Token> src = new IteratorSequence<>(
                new KeywordToken(KeywordToken.Keyword.Builtin),
                new KeywordToken(KeywordToken.Keyword.Model),
                new IdentifierToken("TestModel")
        )) {
            EntityParser<ModelDeclaration> parser =
                    new ModelDeclarationParser();

            Optional<ModelDeclaration> nullableDeclaration = parser.parse(
                    src,
                    ctrl,
                    Environment.EMPTY_ENVIRONMENT
            );

            assertTrue(nullableDeclaration.isPresent());

            ModelDeclaration declaration = nullableDeclaration.get();
            assertEquals("TestModel", declaration.getName());

            EnumSet<Model.Flag> flags = declaration.getFlags();
            assertEquals(1, flags.size());
            assertTrue(flags.contains(Model.Flag.Builtin));

            assertTrue(declaration.getTypeParameters().isEmpty());
        }
    }
}
