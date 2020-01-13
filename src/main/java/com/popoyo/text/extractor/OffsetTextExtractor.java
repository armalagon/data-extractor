package com.popoyo.text.extractor;

import java.util.Objects;

/**
 *
 * @author aalaniz
 */
public class OffsetTextExtractor<T> implements TextExtractor<T> {

    private final String offset;
    private final boolean trim;

    public OffsetTextExtractor(String offset, boolean trim) throws NullPointerException {
        this.offset = Objects.requireNonNull(offset, "[offset] es requerido");
        this.trim = trim;
    }

    public OffsetTextExtractor(String offset) {
        this(offset, true);
    }

    @Override
    public String rawValue(String content) {
        int index = content.indexOf(offset);
        if (index != -1) {
            String rawValue = content.substring(index + offset.length());
            return trim ? rawValue.trim() : rawValue;
        } else {
            return "";
        }
    }

}
