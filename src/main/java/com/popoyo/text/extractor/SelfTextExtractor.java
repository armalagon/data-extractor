package com.popoyo.text.extractor;

/**
 *
 * @author aalaniz
 */
public class SelfTextExtractor<T> implements TextExtractor<T> {

    @Override
    public String rawValue(String content) {
        return content;
    }

}
