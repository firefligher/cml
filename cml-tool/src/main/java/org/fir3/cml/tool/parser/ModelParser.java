package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Attribute;
import org.fir3.cml.api.model.Model;
import org.fir3.cml.tool.impl.ModelImpl;
import org.fir3.cml.tool.tokenizer.KeywordToken;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.Sequence;
import org.fir3.cml.tool.util.seq.SequenceMatcher;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

final class ModelParser implements EntityParser<Model> {
    private static final SequenceMatcher<Token> SEMICOLON_MATCHER =
            new SequenceMatcher<>(new KeywordToken(
                    KeywordToken.Keyword.Semicolon
            ));

    private static final SequenceMatcher<Token> LEFT_BRACE_MATCHER =
            new SequenceMatcher<>(new KeywordToken(
                    KeywordToken.Keyword.LeftBrace
            ));

    private static final SequenceMatcher<Token> RIGHT_BRACE_MATCHER =
            new SequenceMatcher<>(new KeywordToken(
                    KeywordToken.Keyword.RightBrace
            ));

    @Override
    public Optional<Model> parse(
            Sequence<Token> src,
            ParserController parserCtrl,
            Environment environment
    ) throws IOException {
        try (Sequence.Mark mark = src.mark()) {
            Optional<ModelDeclaration> nullableDeclaration = parserCtrl.parse(
                    src,
                    ModelDeclaration.class,
                    environment
            );

            if (!nullableDeclaration.isPresent()) {
                mark.reset();
                return Optional.empty();
            }

            ModelDeclaration declaration = nullableDeclaration.get();

            // If the declaration is built-in, we expect one remaining
            // semicolon and are done. Otherwise, we need to read attributes.

            Set<Attribute> attributes = new HashSet<>();

            if (declaration.getFlags().contains(Model.Flag.Builtin)) {
                if (!SEMICOLON_MATCHER.skip(src)) {
                    mark.reset();
                    return Optional.empty();
                }

                return Optional.of(new ModelImpl(
                        declaration.getName(),
                        declaration.getFlags(),
                        declaration.getTypeParameters(),
                        attributes
                ));
            }

            // Expecting an attribute listing that is encapsulated by braces

            if (!LEFT_BRACE_MATCHER.skip(src)) {
                mark.reset();
                return Optional.empty();
            }

            Environment attributeEnv = environment.extend(
                    declaration.getTypeParameters()
            );

            Optional<Attribute> nextAttribute;

            while ((nextAttribute = parserCtrl.parse(
                    src,
                    Attribute.class,
                    attributeEnv
            )).isPresent()) {
                attributes.add(nextAttribute.get());
            }

            if (!RIGHT_BRACE_MATCHER.skip(src)) {
                mark.reset();
                return Optional.empty();
            }

            return Optional.of(new ModelImpl(
                    declaration.getName(),
                    declaration.getFlags(),
                    declaration.getTypeParameters(),
                    attributes
            ));
        }
    }
}
