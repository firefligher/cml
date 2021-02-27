package org.fir3.cml.tool.parser;

/**
 * This class represents a segment of domain identifier.
 */
final class DomainSegment {
    private final String segment;

    /**
     * Initializes a new instance of <code>DomainSegment</code> that represents
     * the passed <code>segment</code>.
     *
     * @param segment   The name of the segment that the new instance will
     *                  represent.
     */
    public DomainSegment(String segment) {
        this.segment = segment;
    }

    /**
     * Returns the name of the segment that this instance represents.
     *
     * @return  The name of the segment that this instance represents
     */
    public String getSegment() {
        return segment;
    }
}
