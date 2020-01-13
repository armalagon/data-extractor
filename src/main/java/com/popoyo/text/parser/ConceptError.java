package com.popoyo.text.parser;

/**
 *
 * @author aalaniz
 */
public final class ConceptError {

    private final int page;
    private final int line;
    private final Concept concept;
    private final String content;
    private final String error;

    public ConceptError(int page, int line, Concept concept, String content, String error) {
        this.page = page;
        this.line = line;
        this.concept = concept;
        this.content = content;
        this.error = error;
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

    public String getContent() {
        return content;
    }

    public String getError() {
        return error;
    }
}
