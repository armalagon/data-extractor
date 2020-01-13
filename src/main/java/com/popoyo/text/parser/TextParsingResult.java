package com.popoyo.text.parser;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author aalaniz
 */
public class TextParsingResult implements Iterable<ConceptOutput> {
    private final List<ConceptOutput> outputs = new LinkedList<>();
    private final List<ConceptError> errors = new LinkedList<>();

    public void addConceptOutput(ConceptOutput output) {
        outputs.add(output);
    }

    public void addConceptError(ConceptError error) {
        errors.add(error);
    }

    public List<ConceptError> conceptErrors() {
        return Collections.unmodifiableList(errors);
    }

    @Override
    public Iterator<ConceptOutput> iterator() {
        return outputs.iterator();
    }

}
