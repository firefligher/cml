package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Attribute;
import org.fir3.cml.api.model.Type;
import org.fir3.cml.tool.impl.AttributeImpl;
import org.fir3.cml.tool.tokenizer.KeywordToken;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.Sequence;
import org.fir3.cml.tool.util.seq.SequenceMatcher;

import java.io.IOException;
import java.util.Optional;

final class AttributeParser implements EntityParser<Attribute> {
    private static final SequenceMatcher<Token> SEMICOLON_MATCHER =
            new SequenceMatcher<>(new KeywordToken(
                    KeywordToken.Keyword.Semicolon
            ));

    @Override
    public Optional<Attribute> parse(
            Sequence<Token> src,
            ParserController parserCtrl,
            Environment environment
    ) throws IOException {
        try (Sequence.Mark mark = src.mark()) {
            Optional<Type> optionalType = parserCtrl.parse(
                    src,
                    Type.class,
                    environment
            );

            if (!optionalType.isPresent()) {
                mark.reset();
                return Optional.empty();
            }

            Optional<Identifier> optionalIdentifier = parserCtrl.parse(
                    src,
                    Identifier.class,
                    environment
            );

            if (!optionalIdentifier.isPresent()) {
                mark.reset();
                return Optional.empty();
            }

            if (!SEMICOLON_MATCHER.matches(src)) {
                mark.reset();
                return Optional.empty();
            }

            return Optional.of(new AttributeImpl(
                    optionalIdentifier.get().getIdentifier(),
                    optionalType.get()
            ));
        }
    }
}
