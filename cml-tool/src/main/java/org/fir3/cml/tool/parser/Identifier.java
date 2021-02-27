package org.fir3.cml.tool.parser;

/**
 * This class represents the identifier of an attribute or a model.
 */
final class Identifier {
    private final String identifier;

    /**
     * Initializes a new instance of <code>Identifier</code>, which represents
     * the identifier whose name is passed as <code>identifier</code>
     * parameter.
     *
     * @param identifier    The name of the identifier that the new
     *                      <code>Identifier</code> instance represents.
     */
    public Identifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the name of the identifier that is represented by this instance.
     *
     * @return  The name of the identifier that this instance represents
     */
    public String getIdentifier() {
        return this.identifier;
    }
}
