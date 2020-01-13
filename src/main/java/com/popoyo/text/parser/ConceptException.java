package com.popoyo.text.parser;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author aalaniz
 */
public class ConceptException extends Exception {

    private static final String AFTER_CONCEPT_IS_REQUIRED = "[afterConcept] es requerido para los siguientes conceptos: %s";
    private static final String UNKNOWN_CONCEPT = "[afterConcept] hace referencia a conceptos que no existen: %s";
    private static final String SELF_REFERENCED_CONCEPT = "[description] y [afterConcept] tienen el mismo valor para los siguientes " +
        "casos: %s";
    private static final String UNKNOWN_CLASS = "La clase [%s] no existe o no se encuentra disponible en el class path";
    private static final String UNSUPPORTED_STRATEGY = "La estrategia de extraccion de texto [%s] no esta soportada";

    public ConceptException(String message) {
        super(message);
    }

    public static ConceptException createAfterConceptIsMissingException(List<String> concepts) {
        String detail = concepts.stream().collect(Collectors.joining(", "));
        return new ConceptException(String.format(AFTER_CONCEPT_IS_REQUIRED, detail));
    }

    public static ConceptException createUnknownConceptException(List<String> concepts) {
        String detail = concepts.stream().collect(Collectors.joining(", "));
        return new ConceptException(String.format(UNKNOWN_CONCEPT, detail));
    }

    public static ConceptException createSelfReferencedConceptException(List<String> concepts) {
        String detail = concepts.stream().collect(Collectors.joining(", "));
        return new ConceptException(String.format(SELF_REFERENCED_CONCEPT, detail));
    }

    public static ConceptException createUnknownClass(String clazz) {
        return new ConceptException(String.format(UNKNOWN_CLASS, clazz));
    }

    public static ConceptException createUnsupportedStrategy(TextExtractorStrategy strategy) {
        return new ConceptException(String.format(UNSUPPORTED_STRATEGY, strategy));
    }
}
