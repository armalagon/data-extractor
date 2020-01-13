package com.popoyo.text.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author aalaniz
 */
public abstract class ConceptReader {

    private static final String RESOURCE_NOT_FOUND = "El recurso [%s] no esta disponible en el classpath";
    private static final String DESERIALIZATION_ERROR = "Ha ocurrido un error durante la deserializacion del recurso [%s]";

    private static class ListOfConcept extends TypeReference<List<Concept>> {
    };

    public static List<Concept> readFromClasspath(String resource) throws IOException, ConceptException {
        try (InputStream is = ConceptReader.class.getResourceAsStream(resource)) {
            if (is == null) {
                throw new IOException(String.format(RESOURCE_NOT_FOUND, resource));
            }
            ObjectMapper mapper = new ObjectMapper();
            List<Concept> concepts = mapper.readValue(is, new ListOfConcept());
            validateDependantConcepts(concepts);
            List<Concept> sortedConcepts = sortConceptsBasedOnDependencies(concepts);
            for (Concept concept : sortedConcepts) {
                concept.textExtractor();
            }
            return sortedConcepts;
        } catch (IOException ioe) {
            throw new IOException(String.format(DESERIALIZATION_ERROR, resource), ioe);
        } catch (IllegalArgumentException | NullPointerException exc) {
            throw new ConceptException(exc.getMessage());
        }
    }

    private static List<Concept> sortConceptsBasedOnDependencies(List<Concept> concepts) throws ConceptException {
        Map<String, Concept> cache = concepts.stream()
                .collect(Collectors.toMap(Concept::getDescription, Function.identity()));

        List<Concept> sortedConcepts = new ArrayList<>(concepts.size());
        for (Concept concept : concepts) {
            if (!sortedConcepts.contains(concept)) {
                if (concept.getStrategy() == TextExtractorStrategy.BETWEEN_CONCEPT_AND_PATTERN) {
                    sortedConcepts.addAll(findDependentConcepts(cache, sortedConcepts, concept));
                } else {
                    sortedConcepts.add(concept);
                }
            }
        }
        return sortedConcepts;
    }

    private static void validateDependantConcepts(List<Concept> concepts) throws ConceptException {
        List<String> conceptsWithMissingConfiguration = concepts.stream()
                .filter(concept -> concept.getStrategy() == TextExtractorStrategy.BETWEEN_CONCEPT_AND_PATTERN)
                .filter(concept -> concept.getAfterConceptKey() == null)
                .map(Concept::getDescription)
                .collect(Collectors.toList());
        if (!conceptsWithMissingConfiguration.isEmpty()) {
            throw ConceptException.createAfterConceptIsMissingException(conceptsWithMissingConfiguration);
        }

        List<String> conceptDescriptions = concepts.stream()
                .map(Concept::getDescription)
                .collect(Collectors.toList());

        Set<String> referencedConcepts = concepts.stream()
                .filter(concept -> concept.getAfterConceptKey() != null)
                .map(Concept::getAfterConceptKey)
                .collect(Collectors.toSet());
        List<String> missingConcepts = referencedConcepts.stream()
                .filter(description -> !conceptDescriptions.contains(description))
                .collect(Collectors.toList());
        if (!missingConcepts.isEmpty()) {
            throw ConceptException.createUnknownConceptException(missingConcepts);
        }

        List<String> selfReferencedConcepts = concepts.stream()
                .filter(concept -> concept.getDescription().equals(concept.getAfterConceptKey()))
                .map(Concept::getDescription)
                .collect(Collectors.toList());
        if (!selfReferencedConcepts.isEmpty()) {
            throw ConceptException.createSelfReferencedConceptException(selfReferencedConcepts);
        }

        // TODO Validar dependencia circular
    }

    private static List<Concept> findDependentConcepts(Map<String, Concept> cache, List<Concept> sortedConcepts, Concept concept) {
        List<Concept> family = new ArrayList<>();
        Concept next = new Concept(concept, cache);

        family.add(next);
        while (next.getAfterConceptKey() != null) {
            next = cache.get(next.getAfterConceptKey());
            addIfNoPresent(cache, sortedConcepts, next, family);
        }
        addIfNoPresent(cache, sortedConcepts, next, family);
        Collections.reverse(family);
        return family;
    }

    private static void addIfNoPresent(Map<String, Concept> cache, List<Concept> sortedConcepts, Concept concept, List<Concept> family) {
        if (!sortedConcepts.contains(concept)) {
            if (concept.getStrategy() == TextExtractorStrategy.BETWEEN_CONCEPT_AND_PATTERN) {
                family.add(new Concept(concept, cache));
            } else {
                family.add(concept);
            }
        }
    }
}
