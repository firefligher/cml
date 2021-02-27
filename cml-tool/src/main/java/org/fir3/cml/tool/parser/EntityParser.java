package org.fir3.cml.tool.parser;

import org.fir3.cml.tool.tokenizer.Token;
import org.fir3.cml.tool.util.seq.Sequence;

import java.io.IOException;
import java.util.Optional;

/**
 * The interface of all parsers that parse one particular type of entity.
 *
 * @param <TEntity>         The type of the entity that this parser
 *                          implementation is able to parse from a sequence of
 *                          tokens.
 */
interface EntityParser<TEntity> {
    /**
     * Parses one instance of <code>TEntity</code> from the specified
     * <code>src</code> sequence.
     *
     * If the implementation fails to parse an instance of <code>TEntity</code>
     * from <code>src</code>, the sequence will be reset to the initial state
     * that it was passed by the caller.
     *
     * @param src           The source sequence of tokens, where the instance
     *                      of <code>TEntity</code> will be parsed from.
     *
     * @param parserCtrl    A controller that may be used for parsing other
     *                      sub-entities that are part of the resulting entity.
     *
     * @param environment   The current environment information.
     *
     * @return  An {@link Optional} container that either contains the parsed
     *          instance of <code>TEntity</code> or <code>null</code>, if no
     *          such entity can be directly parsed from <code>src</code>.
     *
     * @throws IOException  If an exception occurs while reading from
     *                      <code>src</code>.
     */
    Optional<TEntity> parse(
            Sequence<Token> src,
            ParserController parserCtrl,
            Environment environment
    ) throws IOException;
}
