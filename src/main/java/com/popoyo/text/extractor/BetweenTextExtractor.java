package com.popoyo.text.extractor;

import java.util.Objects;

/**
 *
 * @author aalaniz
 */
public class BetweenTextExtractor<T> implements TextExtractor<T> {

    private final String leadingText;
    private final String trailingText;
    private final boolean trim;

    public BetweenTextExtractor(String leadingText, String trailingText, boolean trim) throws NullPointerException {
        this.leadingText = Objects.requireNonNull(leadingText);
        this.trailingText = Objects.requireNonNull(trailingText);
        this.trim = trim;
    }

    public BetweenTextExtractor(String leadingText, String trailingText) {
        this(leadingText, trailingText, true);
    }

    @Override
    public String rawValue(String content) {
        int fromIndex = content.indexOf(leadingText);
        if (fromIndex != -1) {
            fromIndex += leadingText.length();
            int toIndex = content.indexOf(trailingText, fromIndex);

            String rawValue = toIndex == -1 ? content.substring(fromIndex) : content.substring(fromIndex, toIndex);
            return trim ? rawValue.trim() : rawValue;
        } else {
            return "";
        }
    }

}
