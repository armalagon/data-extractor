package com.popoyo.text.extractor;

import com.popoyo.text.parser.Concept;
import com.popoyo.text.parser.ConceptException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aalaniz
 */
public class ConceptAndPatternTextExtractor<T> implements TextExtractor<T> {

    private final Concept afterConcept;
    private final Pattern beforePattern;
    private final boolean trim;

    public ConceptAndPatternTextExtractor(Concept afterConcept, String beforeRegex, boolean trim) {
        this.afterConcept = afterConcept;
        this.beforePattern = Pattern.compile(beforeRegex);
        this.trim = trim;
    }

    @Override
    public String rawValue(String content) {
        try {
            String left = afterConcept.rawValue(content);
            Matcher matcher = beforePattern.matcher(content);

            if (!left.isEmpty() && matcher.find()) {
                int start = content.indexOf(left) + left.length();
                int end = matcher.start();
                String value = content.substring(start, end);
                return trim ? value.trim() : value;
            } else {
                return "";
            }
        } catch(ConceptException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }
}
