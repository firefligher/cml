package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Type;
import org.fir3.cml.tool.impl.TypeParameterImpl;
import org.fir3.cml.tool.tokenizer.IdentifierToken;
import org.fir3.cml.tool.tokenizer.KeywordToken;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.IteratorSequence;
import org.fir3.cml.tool.util.seq.Sequence;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TypeParserTest {
    @Test
    public void testParseSimpleType() throws IOException {
        ParserController ctrl = new ParserController();
        ctrl.register(DomainSegment.class, new DomainSegmentParser());
        ctrl.register(Identifier.class, new IdentifierParser());

        try (Sequence<Token> src = new IteratorSequence<>(
                new IdentifierToken("SimpleType")
        )) {
            TypeParser parser = new TypeParser();

            Optional<Type> nullableType = parser.parse(
                    src,
                    ctrl,
                    Environment.EMPTY_ENVIRONMENT
            );

            assertTrue(nullableType.isPresent());

            Type type = nullableType.get();
            assertSame(Type.Category.Model, type.getCategory());

            Type.ModelType modelType = (Type.ModelType) type;
            assertEquals("SimpleType", modelType.getModelName());
            assertTrue(modelType.getTypeParameters().isEmpty());
        }
    }

    @Test
    public void testParseParameterType() throws IOException {
        ParserController ctrl = new ParserController();
        ctrl.register(DomainSegment.class, new DomainSegmentParser());
        ctrl.register(Identifier.class, new IdentifierParser());

        try (Sequence<Token> src = new IteratorSequence<>(
                new IdentifierToken("Param1")
        )) {
            TypeParser parser = new TypeParser();

            Optional<Type> nullableType = parser.parse(
                    src,
                    ctrl,
                    new Environment(Collections.singleton(
                            new TypeParameterImpl("Param1")
                    ))
            );

            assertTrue(nullableType.isPresent());

            Type type = nullableType.get();
            assertSame(Type.Category.Parameter, type.getCategory());

            Type.ParameterType parameterType = (Type.ParameterType) type;
            assertEquals("Param1", parameterType.getTypeParameterName());
        }
    }

    @Test
    public void testParseComplexType() throws IOException {
        TypeParser parser = new TypeParser();

        ParserController ctrl = new ParserController();
        ctrl.register(DomainSegment.class, new DomainSegmentParser());
        ctrl.register(Identifier.class, new IdentifierParser());
        ctrl.register(Type.class, parser);

        try (Sequence<Token> src = new IteratorSequence<>(
                new IdentifierToken("ComplexType"),
                new KeywordToken(KeywordToken.Keyword.LeftChevron),
                new IdentifierToken("Param1"),
                new KeywordToken(KeywordToken.Keyword.Comma),
                new IdentifierToken("ComplexType"),
                new KeywordToken(KeywordToken.Keyword.LeftChevron),
                new IdentifierToken("Param2"),
                new KeywordToken(KeywordToken.Keyword.Comma),
                new IdentifierToken("Param3"),
                new KeywordToken(KeywordToken.Keyword.RightChevron),
                new KeywordToken(KeywordToken.Keyword.RightChevron)
        )) {
            Optional<Type> nullableType = parser.parse(
                    src,
                    ctrl,
                    new Environment(new HashSet<>(Arrays.asList(
                            new TypeParameterImpl("Param1"),
                            new TypeParameterImpl("Param2"),
                            new TypeParameterImpl("Param3")
                    )))
            );

            assertTrue(nullableType.isPresent());

            Type type = nullableType.get();
            assertSame(Type.Category.Model, type.getCategory());

            // Validate outer model

            Type.ModelType modelType = (Type.ModelType) type;
            assertEquals("ComplexType", modelType.getModelName());

            List<Type> typeParameters = modelType.getTypeParameters();
            assertEquals(2, typeParameters.size());

            // Validate first type parameter of the outer model

            Type outerFirstType = typeParameters.get(0);
            assertSame(Type.Category.Parameter, outerFirstType.getCategory());
            assertEquals(
                    "Param1",
                    ((Type.ParameterType) outerFirstType)
                            .getTypeParameterName()
            );

            // Validate second type parameter of the outer model

            Type outerSecondType = typeParameters.get(1);
            assertSame(Type.Category.Model, outerSecondType.getCategory());

            Type.ModelType outerSecondModelType =
                    (Type.ModelType) outerSecondType;

            assertEquals("ComplexType", outerSecondModelType.getModelName());

            List<Type> innerTypeParameters =
                    outerSecondModelType.getTypeParameters();

            assertEquals(2, innerTypeParameters.size());

            // Validate inner parameters

            Type firstInnerType = innerTypeParameters.get(0);
            assertSame(Type.Category.Parameter, firstInnerType.getCategory());
            assertEquals(
                    "Param2",
                    ((Type.ParameterType) firstInnerType)
                            .getTypeParameterName()
            );


            Type secondInnerType = innerTypeParameters.get(1);
            assertSame(Type.Category.Parameter, secondInnerType.getCategory());
            assertEquals(
                    "Param3",
                    ((Type.ParameterType) secondInnerType)
                            .getTypeParameterName()
            );
        }
    }
}
