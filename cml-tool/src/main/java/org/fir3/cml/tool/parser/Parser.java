package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Domain;
import org.fir3.cml.api.model.Model;
import org.fir3.cml.api.model.TypeParameter;
import org.fir3.cml.tool.exception.ParserException;
import org.fir3.cml.tool.impl.DomainImpl;
import org.fir3.cml.tool.impl.ModelImpl;
import org.fir3.cml.tool.impl.TypeParameterImpl;
import org.fir3.cml.tool.tokenizer.IdentifierToken;
import org.fir3.cml.tool.tokenizer.KeywordToken;
import org.fir3.cml.tool.tokenizer.Token;

import java.util.*;
import java.util.regex.Pattern;

/**
 * The parser converts a sequence of {@link Token} instances into a
 * {@link Domain}.
 */
public final class Parser {
    private enum IdentifierType {
        Domain("^[0-9A-Za-z]+(\\.[0-9A-Za-z]+)*$"),
        AttributeOrModel("^([A-Za-z]|_)([0-9A-Za-z]|_)*$");

        private final Pattern pattern;

        IdentifierType(String regexPattern) {
            this.pattern = Pattern.compile(regexPattern);
        }

        public Pattern getPattern() {
            return this.pattern;
        }
    }

    private static final class IdentifierOrKeyword {
        private final KeywordToken.Keyword keyword;
        private final String identifier;

        public IdentifierOrKeyword(KeywordToken.Keyword keyword) {
            Objects.requireNonNull(keyword);

            this.keyword = keyword;
            this.identifier = null;
        }

        public IdentifierOrKeyword(String identifier) {
            Objects.requireNonNull(identifier);

            this.keyword = null;
            this.identifier = identifier;
        }

        public boolean isKeyword() {
            return this.keyword != null;
        }

        public boolean isIdentifier() {
            return this.identifier != null;
        }

        public KeywordToken.Keyword getKeyword() {
            return keyword;
        }

        public String getIdentifier() {
            return identifier;
        }
    }

    private final Iterator<Token> tokens;

    public Parser(Token[] tokens) {
        this(Arrays.asList(tokens));
    }

    public Parser(Iterable<Token> tokens) {
        this.tokens = tokens.iterator();
    }

    public Optional<Domain> parse() throws ParserException {
        if (!this.tokens.hasNext()) {
            return Optional.empty();
        }

        EnumSet<Domain.Flag> domainFlags = EnumSet.noneOf(Domain.Flag.class);
        String domainIdentifier;

        // Parse domain declaration

        KeywordToken.Keyword keyword = this.expectKeywordOf(
                KeywordToken.Keyword.Ubiquitous,
                KeywordToken.Keyword.Domain
        );

        if (keyword == KeywordToken.Keyword.Ubiquitous) {
            domainFlags.add(Domain.Flag.Ubiquitous);
            this.expectKeywordOf(KeywordToken.Keyword.Domain);
        }

        domainIdentifier = this.expectIdentifier(IdentifierType.Domain);
        this.expectKeywordOf(KeywordToken.Keyword.Semicolon);

        // Parse as many model declarations as possible

        Set<Model> models = new HashSet<>();
        Optional<Model> nextModel;

        while ((nextModel = this.parseModel()).isPresent()) {
            models.add(nextModel.get());
        }

        return Optional.of(new DomainImpl(
                domainIdentifier,
                domainFlags,
                models
        ));
    }

    private Optional<Model> parseModel() throws ParserException {
        if (!this.tokens.hasNext()) {
            return Optional.empty();
        }

        KeywordToken.Keyword keyword = this.expectKeywordOf(
                KeywordToken.Keyword.Builtin,
                KeywordToken.Keyword.Model
        );

        EnumSet<Model.Flag> modelFlags = EnumSet.noneOf(Model.Flag.class);

        if (keyword == KeywordToken.Keyword.Builtin) {
            modelFlags.add(Model.Flag.Builtin);
            this.expectKeywordOf(KeywordToken.Keyword.Model);
        }

        // Parsing the model identifier and its type parameters (if there are
        // any present)

        String modelIdentifier = this.expectIdentifier(
                IdentifierType.AttributeOrModel
        );

        keyword = this.expectKeywordOf(
                KeywordToken.Keyword.LeftChevron,

                modelFlags.contains(Model.Flag.Builtin)
                        ? KeywordToken.Keyword.Semicolon
                        : KeywordToken.Keyword.LeftBrace
        );

        Set<TypeParameter> typeParameters = new HashSet<>();

        if (keyword == KeywordToken.Keyword.LeftChevron) {
            // Parsing as many type parameters as possible, but at least one.

            do {
                String identifier = this.expectIdentifier(
                        IdentifierType.AttributeOrModel
                );

                typeParameters.add(new TypeParameterImpl(identifier));
            } while (
                    this.expectKeywordOf(
                            KeywordToken.Keyword.Comma,
                            KeywordToken.Keyword.RightChevron
                    ) == KeywordToken.Keyword.Comma
            );
        }

        if (modelFlags.contains(Model.Flag.Builtin)) {
            this.expectKeywordOf(KeywordToken.Keyword.Semicolon);
            return Optional.of(new ModelImpl(
                    modelIdentifier,
                    modelFlags,
                    typeParameters,
                    Collections.emptySet()
            ));
        }

        // TODO: Model-definitions require a seekable sequence tokens

        throw new UnsupportedOperationException("Not implemented");
    }

    private IdentifierOrKeyword expectIdentifierOrKeyword(
            IdentifierType type,
            KeywordToken.Keyword... keywords
    ) throws ParserException {
        Token token = this.expectToken();

        if (token instanceof IdentifierToken) {
            String identifier = ((IdentifierToken) token).getIdentifier();

            if (type.getPattern().matcher(identifier).matches()) {
                return new IdentifierOrKeyword(identifier);
            }
        }

        if (token instanceof KeywordToken) {
            KeywordToken.Keyword keyword = ((KeywordToken) token).getKeyword();
            boolean keywordMatches = Arrays.asList(keywords).contains(keyword);

            if (keywordMatches) {
                return new IdentifierOrKeyword(keyword);
            }
        }

        throw new ParserException(String.format(
                "Expected one keyword of '%s' or an identifier of type " +
                        "'%s', got: %s",
                Arrays.toString(keywords),
                type,
                token
        ));
    }

    private KeywordToken.Keyword expectKeywordOf(
            KeywordToken.Keyword... keywords
    ) throws ParserException {
        Token token = this.expectToken();

        if (!(token instanceof KeywordToken)) {
            throw new ParserException("Expected KeywordToken, got: " + token);
        }

        KeywordToken.Keyword actualKeyword =
                ((KeywordToken) token).getKeyword();

        return Arrays.stream(keywords)
                .filter(k -> Objects.equals(k, actualKeyword))
                .findAny()
                .orElseThrow(() -> new ParserException(String.format(
                        "Expected one keyword of '%s', got keyword '%s'",
                        Arrays.toString(keywords),
                        actualKeyword
                )));
    }

    private String expectIdentifier(IdentifierType type)
            throws ParserException {
        Token token = this.expectToken();

        if (!(token instanceof IdentifierToken)) {
            throw new ParserException(
                    "Expected IdentifierToken, got: " + token
            );
        }

        String identifier = ((IdentifierToken) token).getIdentifier();

        if (!type.getPattern().matcher(identifier).matches()) {
            throw new ParserException(String.format(
                    "Invalid identifier, expected type '%s', got '%s'",
                    type,
                    identifier
            ));
        }

        return identifier;
    }

    private Token expectToken() throws ParserException {
        if (!this.tokens.hasNext()) {
            throw new ParserException(
                    "Expected more tokens, but reached the end of the token " +
                            "sequence"
            );
        }

        return this.tokens.next();
    }
}
