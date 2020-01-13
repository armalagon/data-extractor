package com.popoyo.text.converter;

import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author aalaniz
 */
public class ConversionConfigurationBuilder {

    Map<Class<?>, EmptyConversionRuleBuilder<?>> emptyBuilderByType = new HashMap<>();
    NumberConversionRuleBuilder numberBuilder;
    DateConversionRuleBuilder dateBuilder;

    public ConversionConfigurationBuilder() {
        numberBuilder = new NumberConversionRuleBuilder(this);
        dateBuilder = new DateConversionRuleBuilder(this);
    }

    public <T> EmptyConversionRuleBuilder<T> whenIsEmpty(Class<T> type) {
        EmptyConversionRuleBuilder<T> empty = new EmptyConversionRuleBuilder<>(type, this);
        emptyBuilderByType.put(type, empty);
        return empty;
    }

    public NumberConversionRuleBuilder whenIsNumber() {
        return numberBuilder;
    }

    public DateConversionRuleBuilder whenIsDate() {
        return dateBuilder;
    }

    public ConversionConfiguration build() {
        return new ConversionConfiguration(this);
    }

    public static class EmptyConversionRule<T> {
        final Class<T> type;
        final boolean returnNull;
        final boolean throwException;
        final T returnDefaultValue;

        EmptyConversionRule(EmptyConversionRuleBuilder<T> builder) {
            this.type = builder.type;
            this.returnNull = builder.returnNull;
            this.throwException = builder.throwException;
            this.returnDefaultValue = builder.returnDefaultValue;
        }

        public Class<T> getType() {
            return type;
        }

        public boolean isReturnNull() {
            return returnNull;
        }

        public boolean isThrowException() {
            return throwException;
        }

        public T getReturnDefaultValue() {
            return returnDefaultValue;
        }
    }

    public static class EmptyConversionRuleBuilder<T> {
        ConversionConfigurationBuilder parentBuilder;
        Class<T> type;
        boolean returnNull;
        boolean throwException;
        T returnDefaultValue;

        EmptyConversionRuleBuilder(Class<T> type, ConversionConfigurationBuilder parentBuilder) {
            this.type = type;
            this.returnNull = true;
            this.parentBuilder = parentBuilder;
        }

        public EmptyConversionRuleBuilder returnNull() {
            returnNull = true;
            throwException = false;
            returnDefaultValue = null;
            return this;
        }

        public EmptyConversionRuleBuilder throwException() {
            throwException = true;
            returnNull = false;
            returnDefaultValue = null;
            return this;
        }

        public EmptyConversionRuleBuilder returnDefaultValue(T returnDefaultValue) {
            this.returnDefaultValue = returnDefaultValue;
            returnNull = false;
            throwException = false;
            return this;
        }

        public ConversionConfigurationBuilder and() {
            return parentBuilder;
        }
    }

    public static class NumberConversionRule {
        final char millisSeparator;
        final boolean minusSignAtEnd;
        final int round;

        NumberConversionRule(NumberConversionRuleBuilder builder) {
            this.millisSeparator = builder.millisSeparator;
            this.minusSignAtEnd = builder.minusSignAtEnd;
            this.round = builder.round;
        }

        public char getMillisSeparator() {
            return millisSeparator;
        }

        public boolean isMinusSignAtEnd() {
            return minusSignAtEnd;
        }

        public int getRound() {
            return round;
        }
    }

    public static class NumberConversionRuleBuilder {
        ConversionConfigurationBuilder parentBuilder;
        char millisSeparator;
        boolean minusSignAtEnd;
        int round;

        NumberConversionRuleBuilder(ConversionConfigurationBuilder parentBuilder) {
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
            millisSeparator = symbols.getGroupingSeparator();
            round = -1;
            this.parentBuilder = parentBuilder;
        }

        public NumberConversionRuleBuilder locale(Locale locale) {
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            millisSeparator = symbols.getGroupingSeparator();
            return this;
        }

        public NumberConversionRuleBuilder locale() {
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
            millisSeparator = symbols.getGroupingSeparator();
            return this;
        }

        public NumberConversionRuleBuilder millisSeparator(char millisSeparator) {
            this.millisSeparator = millisSeparator;
            return this;
        }

        public NumberConversionRuleBuilder minusSignAtEnd() {
            return minusSignAtEnd(true);
        }

        public NumberConversionRuleBuilder minusSignAtEnd(boolean minusSignAtEnd) {
            this.minusSignAtEnd = minusSignAtEnd;
            return this;
        }

        public NumberConversionRuleBuilder round(int round) {
            if (round < 0) {
                throw new IllegalArgumentException("[round] debe ser mayor o igual a cero");
            }
            this.round = round;
            return this;
        }

        public NumberConversionRuleBuilder roundTo2() {
            return round(2);
        }

        public NumberConversionRuleBuilder roundTo4() {
            return round(4);
        }

        public ConversionConfigurationBuilder and() {
            return parentBuilder;
        }
    }

    public static class DateConversionRule {
        final String dateFormat;
        final String dateTimeFormat;

        DateConversionRule(DateConversionRuleBuilder builder) {
            this.dateFormat = builder.dateFormat;
            this.dateTimeFormat = builder.dateTimeFormat;
        }

        public String getDateFormat() {
            return dateFormat;
        }

        public String getDateTimeFormat() {
            return dateTimeFormat;
        }
    }

    public static class DateConversionRuleBuilder {
        ConversionConfigurationBuilder parentBuilder;
        String dateFormat;
        String dateTimeFormat;

        DateConversionRuleBuilder(ConversionConfigurationBuilder parentBuilder) {
            this.parentBuilder = parentBuilder;
        }

        public DateConversionRuleBuilder dateFormat(String dateFormat) {
            if (dateFormat == null || dateFormat.isEmpty()) {
                throw new IllegalArgumentException("Parametro [dateFormat] es requerido");
            }
            this.dateFormat = dateFormat;
            return this;
        }

        public DateConversionRuleBuilder isoDateFormat() {
            dateFormat = "uuuu-MM-dd";
            return this;
        }

        public DateConversionRuleBuilder dateTimeFormat(String dateTimeFormat) {
            if (dateTimeFormat == null || dateTimeFormat.isEmpty()) {
                throw new IllegalArgumentException("Parametro [dateTimeFormat] es requerido");
            }
            this.dateTimeFormat = dateTimeFormat;
            return this;
        }

        public DateConversionRuleBuilder isoDateTimeFormat() {
            dateTimeFormat = "uuuu-MM-ddThh:mm:ss";
            return this;
        }

        public ConversionConfigurationBuilder and() {
            return parentBuilder;
        }
    }

}
