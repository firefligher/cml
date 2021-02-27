package org.fir3.cml.tool.parser;

import org.fir3.cml.tool.tokenizer.IdentifierToken;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.Sequence;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

final class IdentifierParser implements EntityParser<Identifier> {
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile(
            "^(_|[A-Za-z])\\w*$"
    );

    @Override
    public Optional<Identifier> parse(
            Sequence<Token> src,
            ParserController parserCtrl,
            Environment environment
    ) throws IOException {
        try (Sequence.Mark mark = src.mark()) {
            Token token = src.read();

            if (!(token instanceof IdentifierToken)) {
                mark.reset();
                return Optional.empty();
            }

            String identifier = ((IdentifierToken) token).getIdentifier();

            if (!IdentifierParser.IDENTIFIER_PATTERN.matcher(
                    identifier
            ).matches()) {
                mark.reset();
                return Optional.empty();
            }

            return Optional.of(new Identifier(identifier));
        }
    }
}
