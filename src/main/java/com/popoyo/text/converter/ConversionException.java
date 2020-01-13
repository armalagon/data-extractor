package com.popoyo.text.converter;

/**
 *
 * @author aalaniz
 */
public class ConversionException extends Exception {

    private static final String CONVERSION_ERROR = "El valor [%s] no se puede convertir a %s";
    private static final String CONVERSION_DETAIL_ERROR = CONVERSION_ERROR + ": %s";

    public ConversionException(String message) {
        super(message);
    }

    ConversionException(String value, Class<?> expectedType) {
        super(String.format(CONVERSION_ERROR, value, expectedType.getSimpleName()));
    }

    ConversionException(String value, Class<?> expectedType, String detail) {
        super(String.format(CONVERSION_ERROR, value, expectedType.getSimpleName(), detail));
    }

}
