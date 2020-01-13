package com.popoyo.text.converter;

import com.popoyo.text.converter.ConversionConfigurationBuilder.DateConversionRule;
import com.popoyo.text.converter.ConversionConfigurationBuilder.EmptyConversionRule;
import com.popoyo.text.converter.ConversionConfigurationBuilder.EmptyConversionRuleBuilder;
import com.popoyo.text.converter.ConversionConfigurationBuilder.NumberConversionRule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aalaniz
 */
public class ConversionConfiguration {

    public static final ConversionConfiguration DEFAULT_CONFIGURATION = new ConversionConfigurationBuilder()
            .whenIsNumber()
                .locale()
                .roundTo2()
            .and()
            .whenIsDate()
                .isoDateFormat()
                .isoDateTimeFormat()
            .and()
            .build();

    private final Map<Class<?>, EmptyConversionRule<?>> emptyRules;
    private final NumberConversionRule numberConversionRule;
    private final DateConversionRule dateConversionRule;

    ConversionConfiguration(ConversionConfigurationBuilder builder) {
        this.numberConversionRule = new NumberConversionRule(builder.numberBuilder);
        this.dateConversionRule = new DateConversionRule(builder.dateBuilder);
        this.emptyRules = new HashMap<>(builder.emptyBuilderByType.size());
        for (Map.Entry<Class<?>, EmptyConversionRuleBuilder<?>> entry : builder.emptyBuilderByType.entrySet()) {
            emptyRules.put(entry.getKey(), new EmptyConversionRule<>(entry.getValue()));
        }
    }

    public Map<Class<?>, EmptyConversionRule<?>> getEmptyRules() {
        return Collections.unmodifiableMap(emptyRules);
    }

    public EmptyConversionRule<?> getEmptyRule(Class<?> type) {
        return emptyRules.get(type);
    }

    public NumberConversionRule getNumberConversionRule() {
        return numberConversionRule;
    }

    public DateConversionRule getDateConversionRule() {
        return dateConversionRule;
    }

}
