package org.fir3.cml.tool.parser;

import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.Sequence;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A controller that can be used to communicate with and between
 * implementations of the {@link EntityParser} interface.
 */
final class ParserController {
    private final Map<Class<?>, EntityParser<?>> parsers;

    public ParserController() {
        this.parsers = new HashMap<>();
    }

    /**
     * Registers the <code>parserImpl</code> for the <code>entityClass</code>.
     *
     * Afterwards, the {@link #parse(Sequence, Class, Environment)} method of
     * this instance may be used for parsing instances of
     * <code>entityClass</code>.
     *
     * @param entityClass   The class whose instance can be parsed using the
     *                      provided <code>parserImpl</code>.
     *
     * @param parserImpl    The instance of the parser implementation that will
     *                      be used for parsing instances of
     *                      <code>TEntity</code>.
     *
     * @param <TEntity>     The entity whose parsing-strategy is registered
     *                      with the current method-call.
     *
     * @throws NullPointerException     If <code>null</code> is passed as value
     *                                  of any parameter.
     *
     * @throws IllegalArgumentException If a parser implementation for
     *                                  <code>entityClass</code> has been
     *                                  registered already.
     */
    public <TEntity> void register(
            Class<TEntity> entityClass,
            EntityParser<TEntity> parserImpl
    ) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(parserImpl, "parserImpl is null");

        if (this.parsers.containsKey(entityClass)) {
            throw new IllegalArgumentException(
                    "entityClass is already registered"
            );
        }

        this.parsers.put(entityClass, parserImpl);
    }

    /**
     * Parses an instance of <code>TEntity</code> from the passed
     * <code>src</code> sequence.
     *
     * If the method fails to parse an instance of <code>TEntity</code> from
     * the next tokens of <code>src</code>, <code>src</code> will be reset to
     * the initial state, in which it was passed to this method by the caller.
     *
     * @param src           The source sequence that the requested instance of
     *                      <code>TEntity</code> will be parsed from.
     *
     * @param entityClass   The class of <code>TEntity</code>.
     * @param environment   The current environment.
     *
     * @param <TEntity>     The entity that will be parsed from the
     *                      <code>src</code> sequence.
     *
     * @return  An {@link Optional} container that either contains the parsed
     *          instance of <code>TEntity</code>, or <code>null</code>, if no
     *          instance could be parsed.
     *
     * @throws IOException  If an exception occurs while reading from
     *                      <code>src</code>.
     */
    @SuppressWarnings("unchecked")
    public <TEntity> Optional<TEntity> parse(
            Sequence<Token> src,
            Class<TEntity> entityClass,
            Environment environment
    ) throws IOException {
        Objects.requireNonNull(src, "src is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(environment, "environment is null");

        EntityParser<TEntity> parser =
                (EntityParser<TEntity>) this.parsers.get(entityClass);

        if (parser == null) {
            throw new IllegalStateException(String.format(
                    "No parser for '%s'",
                    entityClass.getName()
            ));
        }

        return parser.parse(src, this, environment);
    }
}
