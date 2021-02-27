package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Domain;
import org.fir3.cml.api.model.Model;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.Sequence;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

final class DomainParser implements EntityParser<Domain> {
    @Override
    public Optional<Domain> parse(
            Sequence<Token> src,
            ParserController parserCtrl,
            Environment environment
    ) throws IOException {
        // Expecting a domain declaration at the beginning of each sequence

        Optional<DomainDeclaration> nullableDeclaration = parserCtrl.parse(
                src,
                DomainDeclaration.class,
                environment
        );

        if (!nullableDeclaration.isPresent()) {
            return Optional.empty();
        }

        DomainDeclaration declaration = nullableDeclaration.get();

        // Reading models until there are no models left

        Set<Model> models = new HashSet<>();
        Optional<Model> nextModel;

        while ((nextModel = parserCtrl.parse(
                src,
                Model.class,
                environment
        )).isPresent()) {
            models.add(nextModel.get());
        }

        return Optional.of(new Domain(
                declaration.getName(),
                declaration.getFlags(),
                models
        ));
    }
}
