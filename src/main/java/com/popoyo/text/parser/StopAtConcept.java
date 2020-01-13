package com.popoyo.text.parser;

/**
 *
 * @author aalaniz
 */
public final class StopAtConcept {

    private final String stopAtKeyword;
    private final int page;
    private final int line;

    public StopAtConcept(String stopAtKeyword, int page, int line) {
        this.stopAtKeyword = stopAtKeyword;
        this.page = page;
        this.line = line;
    }

    public String getStopAtKeyword() {
        return stopAtKeyword;
    }

    public int getPage() {
        return page;
    }

    public int getLine() {
        return line;
    }

    public boolean hasBeenReached(int currentPage, int currentLine) {
        return (currentPage == page && currentLine >= line) || currentPage > page;
    }
}
