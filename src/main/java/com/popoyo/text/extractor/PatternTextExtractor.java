package com.popoyo.text.extractor;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aalaniz
 */
public class PatternTextExtractor<T> implements TextExtractor<T> {

    private final Pattern pattern;
    private final String[] cleanup;
    private final boolean trim;

    public PatternTextExtractor(String regex, String[] cleanup, boolean trim) throws NullPointerException {
        Objects.requireNonNull(regex, "[regex] es requerido");
        this.pattern = Pattern.compile(regex);
        this.cleanup = cleanup;
        this.trim = trim;
    }

    public PatternTextExtractor(String regex) {
        this(regex, null, true);
    }

    public PatternTextExtractor(String regex, String[] cleanup) {
        this(regex, cleanup, true);
    }

    @Override
    public String rawValue(String content) {
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            String rawValue = content.substring(matcher.start(), matcher.end());
            rawValue = trim ? rawValue.trim() : rawValue;
            if (cleanup == null || cleanup.length == 0) {
                return rawValue;
            } else {
                return cleanExtractedContent(rawValue);
            }
        } else {
            return "";
        }
    }

    private String cleanExtractedContent(String content) {
        String cleanupContent = content;
        for (String textToRemove : cleanup) {
            cleanupContent = cleanupContent.replaceAll(textToRemove, "");
        }
        return cleanupContent;
    }

}
