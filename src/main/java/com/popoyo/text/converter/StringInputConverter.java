package com.popoyo.text.converter;

import com.popoyo.text.converter.ConversionConfigurationBuilder.DateConversionRule;
import com.popoyo.text.converter.ConversionConfigurationBuilder.EmptyConversionRule;
import com.popoyo.text.converter.ConversionConfigurationBuilder.NumberConversionRule;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Date;

/**
 *
 * @author aalaniz
 */
public final class StringInputConverter {

    public static final StringInputConverter INSTANCE = new StringInputConverter();

    private StringInputConverter() {
    }

    public <T> T convert(String value, Class<T> expectedType) throws ConversionException {
        return convert(value, expectedType, ConversionConfiguration.DEFAULT_CONFIGURATION);
    }

    public <T> T convert(String value, Class<T> expectedType, ConversionConfiguration configuration) throws ConversionException {
        if (configuration == null) {
            throw new ConversionException("Parametro [configuration] es requerido");
        }
        if (value == null || value.isEmpty()) {
            return handleEmptyValue(value, expectedType, configuration);
        } else if (expectedType == String.class) {
            return (T) value;
        } else if (expectedType == StringBuilder.class) {
            return (T) new StringBuilder(value);
        } else if (expectedType == Character.class) {
            return (T) convertToCharacter(value);
        } else if (expectedType == Boolean.class) {
            return (T) convertToBoolean(value);
        } else if (Number.class.isAssignableFrom(expectedType)) {
            return handleNumericValue(value, expectedType, configuration);
        } else if (Temporal.class.isAssignableFrom(expectedType) || Date.class.isAssignableFrom(expectedType)) {
            return handleDateValue(value, expectedType, configuration);
        } else {
            return createUsingConstructor(value, expectedType);
        }
    }

    private <T> T handleEmptyValue(String value, Class<T> expectedType, ConversionConfiguration configuration) throws ConversionException {
        EmptyConversionRule emptyConversionRule = configuration.getEmptyRule(expectedType);
        if (emptyConversionRule != null) {
            if (emptyConversionRule.returnNull) {
                return (T) null;
            } else if (emptyConversionRule.throwException) {
                throw new ConversionException("El valor no puede ser null");
            } else {
                return (T) emptyConversionRule.returnDefaultValue;
            }
        } else {
            return (T) null;
        }
    }

    private <T> T handleNumericValue(String value, Class<T> expectedType, ConversionConfiguration configuration)
            throws ConversionException {
        String cleanValue = removeMillisSeparator(value, configuration);
        cleanValue = handleMinusSignAtEnd(cleanValue, configuration);
        if (expectedType == Byte.class) {
            return (T) Byte.valueOf(cleanValue);
        } else if (expectedType == Short.class) {
            return (T) Short.valueOf(cleanValue);
        } else if (expectedType == Integer.class) {
            return (T) Integer.valueOf(cleanValue);
        } else if (expectedType == Long.class) {
            return (T) Long.valueOf(cleanValue);
        } else if (expectedType == Float.class) {
            return (T) Float.valueOf(cleanValue);
        } else if (expectedType == Double.class) {
            return (T) Double.valueOf(cleanValue);
        } else if (expectedType == BigInteger.class) {
            return (T) new BigInteger(cleanValue);
        } else if (expectedType == BigDecimal.class) {
            return (T) new BigDecimal(cleanValue);
        } else {
            return createUsingConstructor(cleanValue, expectedType);
        }
    }

    private <T> T handleDateValue(String value, Class<T> expectedType, ConversionConfiguration configuration) throws ConversionException {
        // TODO Considerar locale para el formateo de las fechas
        // TODO Incluir manejo de Time
        DateConversionRule dateConversionRule = configuration.getDateConversionRule();
        String dateFormat = dateConversionRule.dateFormat;
        String dateTimeFormat = dateConversionRule.dateTimeFormat;
        if (expectedType == LocalDate.class) {
            if (dateFormat != null) {
                return (T) LocalDate.parse(value, DateTimeFormatter.ofPattern(dateFormat));
            } else {
                return (T) LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
            }
        } else if (expectedType == LocalDateTime.class) {
            if (dateTimeFormat != null) {
                return (T) LocalDateTime.parse(value, DateTimeFormatter.ofPattern(dateTimeFormat));
            } else {
                return (T) LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
            }
        } else if (Date.class.isAssignableFrom(expectedType)) {
            LocalDateTime localDateTime = handleDateValue(value, LocalDateTime.class, configuration);
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            if (expectedType == Date.class) {
                return (T) date;
            } else if (expectedType == java.sql.Date.class) {
                return (T) new java.sql.Date(date.getTime());
            } else if (expectedType == java.sql.Time.class) {
                return (T) new java.sql.Time(date.getTime());
            } else if (expectedType == java.sql.Timestamp.class) {
                return (T) new java.sql.Timestamp(date.getTime());
            } else {
                return createUsingConstructor(value, expectedType);
            }
        } else {
            return createUsingConstructor(value, expectedType);
        }
    }

    public static <T> T createUsingConstructor(String value, Class<T> expectedType) throws ConversionException {
        try {
            Constructor<T> constructor = expectedType.getConstructor(String.class);
            return constructor.newInstance(value);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            String detail = "La creacion del objecto a traves de un constructor de tipo String fallo";
            throw new ConversionException(value, expectedType, detail);
        }
    }

    private static String removeMillisSeparator(String value, ConversionConfiguration configuration) {
        String cleanValue;
        char millisSeparator;
        if (configuration.getNumberConversionRule() != null) {
            NumberConversionRule numberConversionRule = configuration.getNumberConversionRule();
            millisSeparator = numberConversionRule.millisSeparator;
        } else {
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
            millisSeparator = symbols.getGroupingSeparator();
        }
        if (value.indexOf(millisSeparator) != -1) {
            cleanValue = removeAll(value, millisSeparator);
        } else {
            cleanValue = value;
        }
        return cleanValue;
    }

    private static String handleMinusSignAtEnd(String value, ConversionConfiguration configuration) {
        String cleanValue;
        NumberConversionRule numberConversionRule = configuration.getNumberConversionRule();
        if (numberConversionRule != null && numberConversionRule.minusSignAtEnd && value.endsWith("-")) {
            char[] origin = value.toCharArray();
            char[] dest = new char[origin.length];
            dest[0] = '-';
            System.arraycopy(origin, 0, dest, 1, origin.length - 1);
            cleanValue = new String(dest);
        } else {
            cleanValue = value;
        }
        return cleanValue;
    }

    private static String removeAll(String value, char old) {
        char[] chars = value.toCharArray();
        StringBuilder newValue = new StringBuilder(chars.length);
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != old) {
                newValue.append(chars[i]);
            }
        }
        return newValue.toString();
    }

    private static Character convertToCharacter(String value) throws ConversionException {
        if (value.length() > 1) {
            throw new ConversionException(value, Character.class);
        }
        return Character.valueOf(value.charAt(0));
    }

    private static Boolean convertToBoolean(String value) throws ConversionException {
        if ("t".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value) ||
                "v".equalsIgnoreCase(value) || "verdadero".equalsIgnoreCase(value) ||
                "y".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) ||
                "s".equalsIgnoreCase(value) || "si".equalsIgnoreCase(value) ||
                "1".equals(value)) {
            return Boolean.TRUE;
        } else if ("f".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value) ||
                "falso".equalsIgnoreCase(value) ||
                "n".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value) ||
                "0".equals(value)) {
            return Boolean.FALSE;
        } else {
            throw new ConversionException(value, Boolean.class);
        }
    }

}
