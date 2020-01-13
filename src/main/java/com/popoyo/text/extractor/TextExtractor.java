package com.popoyo.text.extractor;

import com.popoyo.text.converter.ConversionConfiguration;
import com.popoyo.text.converter.ConversionException;
import com.popoyo.text.converter.StringInputConverter;

/**
 *
 * @author aalaniz
 */
public interface TextExtractor<T> {

    String rawValue(String content);

    default T value(String content, Class<T> expectedType, ConversionConfiguration configuration) throws ConversionException {
        return StringInputConverter.INSTANCE.convert(rawValue(content), expectedType, configuration);
    }

}
