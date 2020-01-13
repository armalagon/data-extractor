package com.popoyo.text.extractor;

public class FixedTextExtractor<T> implements TextExtractor<T> {
    private static final String LENGTH_OUT_OF_BOUNDS = "[%d] es mayor que la cantidad de caracteres disponibles [%d]";

    private final int index;
    private final int length;

    public FixedTextExtractor(int index, int length) throws IllegalArgumentException {
        if (index < 0) {
            throw new IllegalArgumentException("[index] debe ser mayor o igual a 0");
        }
        this.index = index;
        this.length = length;
    }

    @Override
    public String rawValue(String content) throws IllegalArgumentException {
        int remaining = content.length() - index + 1;
        if (length != -1 && length > remaining) {
            throw new IllegalArgumentException(String.format(LENGTH_OUT_OF_BOUNDS, length, remaining));
        }
        return length == -1 ? content.substring(index) : content.substring(index, index + length);
    }

}
