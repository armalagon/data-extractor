package com.popoyo.text.parser;

/**
 *
 * @author aalaniz
 */
public final class ConceptOutput {

    private final int page;
    private final int line;
    private final Concept concept;
    private final Object value;

    public ConceptOutput(int page, int line, Concept concept, Object value) {
        this.page = page;
        this.line = line;
        this.concept = concept;
        this.value = value;
    }

    public int getPage() {
        return page;
    }

    public int getLine() {
        return line;
    }

    public Concept getConcept() {
        return concept;
    }

    public Object getValue() {
        return value;
    }

}
