package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Attribute;
import org.fir3.cml.api.model.Type;
import org.fir3.cml.tool.tokenizer.IdentifierToken;
import org.fir3.cml.tool.tokenizer.KeywordToken;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.IteratorSequence;
import org.fir3.cml.tool.util.seq.Sequence;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AttributeParserImpl {
    @Test
    public void testParse() throws IOException {
        ParserController ctrl = new ParserController();
        ctrl.register(Type.class, new TypeParser());
        ctrl.register(DomainSegment.class, new DomainSegmentParser());
        ctrl.register(Identifier.class, new IdentifierParser());

        try (Sequence<Token> src = new IteratorSequence<>(
                new IdentifierToken("SimpleType"),
                new IdentifierToken("_Value"),
                new KeywordToken(KeywordToken.Keyword.Semicolon)
        )) {
            EntityParser<Attribute> parser = new AttributeParser();
            Optional<Attribute> nullableAttribute = parser.parse(
                    src,
                    ctrl,
                    Environment.EMPTY_ENVIRONMENT
            );

            assertTrue(nullableAttribute.isPresent());

            Attribute attribute = nullableAttribute.get();
            assertEquals("_Value", attribute.getName());

            Type type = attribute.getType();
            assertSame(Type.Category.Model, type.getCategory());

            Type.ModelType modelType = (Type.ModelType) type;
            assertEquals("SimpleType", modelType.getModelName());
            assertTrue(modelType.getTypeParameters().isEmpty());
        }
    }
}
