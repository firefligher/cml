package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Type;
import org.fir3.cml.api.model.ModelType;
import org.fir3.cml.api.model.ParameterType;
import org.fir3.cml.api.model.TypeParameter;
import org.fir3.cml.tool.tokenizer.KeywordToken;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.Sequence;
import org.fir3.cml.tool.util.seq.SequenceMatcher;

import java.io.IOException;
import java.util.*;

public final class TypeParser implements EntityParser<Type> {
    private static final SequenceMatcher<Token> DOT_MATCHER =
            new SequenceMatcher<>(new KeywordToken(KeywordToken.Keyword.Dot));

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
    public Optional<Type> parse(
            Sequence<Token> src,
            ParserController parserCtrl,
            Environment environment
    ) throws IOException {
        Set<TypeParameter> typeParameters = environment.getTypeParameters();

        // The name of a type consists of zero to a finite number of
        // DomainSegments and a terminating Identifier

        try (Sequence.Mark outerMark = src.mark()) {
            // Note that a domain segment may also parsed as identifier and
            // vice-versa.

            StringBuilder nameBuilder = new StringBuilder();

            while (true) {
                try (Sequence.Mark innerMark = src.mark()) {
                    Optional<DomainSegment> segment = parserCtrl.parse(
                            src,
                            DomainSegment.class,
                            environment
                    );

                    if (!segment.isPresent() || !DOT_MATCHER.skip(src)) {
                        innerMark.reset();
                        break;
                    }

                    nameBuilder.append(segment.get().getSegment()).append('.');
                }
            }

            Optional<Identifier> modelIdentifier = parserCtrl.parse(
                    src,
                    Identifier.class,
                    environment
            );

            if (!modelIdentifier.isPresent()) {
                outerMark.reset();
                return Optional.empty();
            }

            String name = nameBuilder.append(
                    modelIdentifier.get().getIdentifier()
            ).toString();

            // If name is the name of a type parameter, we are done.

            if (typeParameters.stream().anyMatch(
                    p -> Objects.equals(name, p.getName())
            )) {
                return Optional.of(new ParameterType(name));
            }

            // If there is a left chevron, this is a generic type.

            List<Type> parameters = new ArrayList<>();

            if (LEFT_CHEVRON_MATCHER.skip(src)) {
                // Parsing types until we reach the right chevron

                do {
                    Optional<Type> parameter = parserCtrl.parse(
                            src,
                            Type.class,
                            environment
                    );

                    if (!parameter.isPresent()) {
                        outerMark.reset();
                        return Optional.empty();
                    }

                    parameters.add(parameter.get());
                } while (COMMA_MATCHER.skip(src));

                if (!RIGHT_CHEVRON_MATCHER.skip(src)) {
                    outerMark.reset();
                    return Optional.empty();
                }
            }

            return Optional.of(new ModelType(name, parameters));
        }
    }
}
