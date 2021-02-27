package org.fir3.cml.tool.parser;

import org.fir3.cml.tool.tokenizer.IdentifierToken;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.Sequence;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

final class DomainSegmentParser implements EntityParser<DomainSegment> {
    private static final Pattern DOMAIN_SEGMENT_PATTERN = Pattern.compile(
            "^\\w\\w*$"
    );

    @Override
    public Optional<DomainSegment> parse(
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

            if (!DomainSegmentParser.DOMAIN_SEGMENT_PATTERN.matcher(
                    identifier
            ).matches()) {
                mark.reset();
                return Optional.empty();
            }

            return Optional.of(new DomainSegment(identifier));
        }
    }
}
