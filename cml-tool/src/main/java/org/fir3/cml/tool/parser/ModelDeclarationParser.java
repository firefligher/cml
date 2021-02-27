package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Model;
import org.fir3.cml.api.model.TypeParameter;
import org.fir3.cml.tool.tokenizer.KeywordToken;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.Sequence;
import org.fir3.cml.tool.util.seq.SequenceMatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

final class ModelDeclarationParser implements EntityParser<ModelDeclaration> {
    private static final SequenceMatcher<Token> BUILTIN_MATCHER =
            new SequenceMatcher<>(new KeywordToken(
                    KeywordToken.Keyword.Builtin
            ));

    private static final SequenceMatcher<Token> MODEL_MATCHER =
            new SequenceMatcher<>(new KeywordToken(
                    KeywordToken.Keyword.Model
            ));

    private static final SequenceMatcher<Token> LEFT_CHEVRON_MATCHER =
            new SequenceMatcher<>(new KeywordToken(
                    KeywordToken.Keyword.LeftChevron
            ));

    private static final SequenceMatcher<Token> RIGHT_CHEVRON_MATCHER =
            new SequenceMatcher<>(new KeywordToken(
                    KeywordToken.Keyword.RightChevron
            ));

    private static final SequenceMatcher<Token> COMMA_MATCHER =
            new SequenceMatcher<>(new KeywordToken(
                    KeywordToken.Keyword.Comma
            ));

    @Override
    public Optional<ModelDeclaration> parse(
            Sequence<Token> src,
            ParserController parserCtrl,
            Environment environment
    ) throws IOException {
        try (Sequence.Mark mark = src.mark()) {
            EnumSet<Model.Flag> flags = EnumSet.noneOf(Model.Flag.class);

            if (BUILTIN_MATCHER.skip(src)) {
                flags.add(Model.Flag.Builtin);
            }

            if (!MODEL_MATCHER.skip(src)) {
                mark.reset();
                return Optional.empty();
            }

            Optional<Identifier> nullableModelName = parserCtrl.parse(
                    src,
                    Identifier.class,
                    environment
            );

            if (!nullableModelName.isPresent()) {
                mark.reset();
                return Optional.empty();
            }

            List<TypeParameter> typeParameters = new ArrayList<>();

            if (LEFT_CHEVRON_MATCHER.skip(src)) {
                do {
                    Optional<Identifier> nullableTypeParamName =
                            parserCtrl.parse(
                                    src,
                                    Identifier.class,
                                    environment
                            );

                    if (!nullableTypeParamName.isPresent()) {
                        mark.reset();
                        return Optional.empty();
                    }

                    typeParameters.add(new TypeParameter(
                            nullableTypeParamName.get().getIdentifier()
                    ));
                } while (COMMA_MATCHER.skip(src));

                if (!RIGHT_CHEVRON_MATCHER.skip(src)) {
                    mark.reset();
                    return Optional.empty();
                }
            }

            return Optional.of(new ModelDeclaration(
                    nullableModelName.get().getIdentifier(),
                    flags,
                    typeParameters
            ));
        }
    }
}
