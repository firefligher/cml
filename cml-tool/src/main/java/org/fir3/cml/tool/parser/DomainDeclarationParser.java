package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Domain;
import org.fir3.cml.tool.tokenizer.KeywordToken;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.Sequence;
import org.fir3.cml.tool.util.seq.SequenceMatcher;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Optional;

final class DomainDeclarationParser
        implements EntityParser<DomainDeclaration> {

    private static final SequenceMatcher<Token> UBIQUITOUS_MATCHER =
            new SequenceMatcher<>(new KeywordToken(
                    KeywordToken.Keyword.Ubiquitous
            ));

    private static final SequenceMatcher<Token> DOMAIN_MATCHER =
            new SequenceMatcher<>(new KeywordToken(
                    KeywordToken.Keyword.Domain
            ));

    private static final SequenceMatcher<Token> SEMICOLON_MATCHER =
            new SequenceMatcher<>(new KeywordToken(
                    KeywordToken.Keyword.Semicolon
            ));

    private static final SequenceMatcher<Token> DOT_MATCHER =
            new SequenceMatcher<>(new KeywordToken(KeywordToken.Keyword.Dot));

    @Override
    public Optional<DomainDeclaration> parse(
            Sequence<Token> src,
            ParserController parserCtrl,
            Environment environment
    ) throws IOException {
        try (Sequence.Mark mark = src.mark()) {
            EnumSet<Domain.Flag> flags = EnumSet.noneOf(Domain.Flag.class);

            if (UBIQUITOUS_MATCHER.skip(src)) {
                flags.add(Domain.Flag.Ubiquitous);
            }

            if (!DOMAIN_MATCHER.skip(src)) {
                mark.reset();
                return Optional.empty();
            }

            StringBuilder name = new StringBuilder();
            Optional<DomainSegment> nextSegment;

            do {
                if (name.length() > 0) {
                    name.append('.');
                }

                nextSegment = parserCtrl.parse(
                        src,
                        DomainSegment.class,
                        environment
                );

                if (!nextSegment.isPresent()) {
                    mark.reset();
                    return Optional.empty();
                }

                name.append(nextSegment.get().getSegment());
            } while (DOT_MATCHER.skip(src));

            if (!SEMICOLON_MATCHER.skip(src)) {
                mark.reset();
                return Optional.empty();
            }

            return Optional.of(new DomainDeclaration(name.toString(), flags));
        }
    }
}
