package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Attribute;
import org.fir3.cml.api.model.Domain;
import org.fir3.cml.api.model.Model;
import org.fir3.cml.api.model.Type;
import org.fir3.cml.tool.exception.ParserException;
import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.Sequence;

import java.io.IOException;
import java.util.Optional;

/**
 * The parser converts a {@link Sequence} of {@link Token} instances into a
 * {@link Domain}.
 */
public final class Parser {
    private static final ParserController PARSER_CONTROLLER;

    static {
        PARSER_CONTROLLER = new ParserController();
        PARSER_CONTROLLER.register(Attribute.class, new AttributeParser());
        PARSER_CONTROLLER.register(
                DomainDeclaration.class,
                new DomainDeclarationParser()
        );

        PARSER_CONTROLLER.register(Domain.class, new DomainParser());
        PARSER_CONTROLLER.register(
                DomainSegment.class,
                new DomainSegmentParser()
        );

        PARSER_CONTROLLER.register(Identifier.class, new IdentifierParser());
        PARSER_CONTROLLER.register(
                ModelDeclaration.class,
                new ModelDeclarationParser()
        );

        PARSER_CONTROLLER.register(Model.class, new ModelParser());
        PARSER_CONTROLLER.register(Type.class, new TypeParser());
    }

    private final Sequence<Token> source;

    /**
     * Initializes a new instance of <code>Parser</code> that may be used for
     * parsing the content of the passed <code>src</code> sequence.
     *
     * @param src   The sequence of tokens that this parser will parse
     */
    public Parser(Sequence<Token> src) {
        this.source = src;
    }

    /**
     * Parses the next {@link Domain} instance from the sequence of tokens that
     * this instance was initialized with.
     *
     * @return  An {@link Optional} container that either contains the parsed
     *          {@link Domain} instance or <code>null</code>, if there are no
     *          tokens left in the underlying sequence.
     *
     * @throws ParserException  If the underlying sequence contains a
     *                          token-subsequence that cannot be parsed.
     */
    public Optional<Domain> parse() throws IOException {
        Optional<Domain> nullableDomain = PARSER_CONTROLLER.parse(
                this.source,
                Domain.class,
                Environment.EMPTY_ENVIRONMENT
        );

        if (this.source.read() != null) {
            throw new ParserException("Unable to parse (full) source");
        }

        return nullableDomain;
    }
}
