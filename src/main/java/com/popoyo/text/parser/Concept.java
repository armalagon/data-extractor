package com.popoyo.text.parser;

import com.popoyo.text.converter.ConversionConfiguration;
import com.popoyo.text.converter.ConversionException;
import com.popoyo.text.extractor.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author aalaniz
 */
@JsonDeserialize(builder = Concept.ConceptBuilder.class)
public final class Concept {

    private final String description;
    private final String javaType;
    private final TextExtractorStrategy strategy;
    private final int page;
    private final int line;
    private final String leadingText;
    private final String trailingText;
    private final int index;
    private final int length;
    private final String stopAtKeyword;
    private final boolean detail;
    private final String regex;
    private final String[] cleanup;
    private final String afterConceptKey;
    private final String beforeRegex;
    private final Map<String, Concept> cache;
    private TextExtractor textExtractor;

    private Concept(ConceptBuilder builder) throws ConceptException {
        this.description = builder.description;
        this.javaType = builder.javaType;
        this.strategy = builder.strategy;
        this.page = builder.page == null ? -1 : builder.page;
        this.line = builder.line == null ? -1 : builder.line;
        this.leadingText = builder.leadingText;
        this.trailingText = builder.trailingText;
        this.index = builder.index == null ? -1 : builder.index;
        this.length = builder.length == null ? -1 : builder.length;
        this.stopAtKeyword = builder.stopAtKeyword;
        this.detail = builder.detail == null ? false : builder.detail;
        this.regex = builder.regex;
        this.cleanup = builder.cleanup;
        this.afterConceptKey = builder.afterConcept;
        this.beforeRegex = builder.beforeRegex;
        this.cache = null;
    }

    Concept(Concept original, Map<String, Concept> cache) {
        this.description = original.description;
        this.javaType = original.javaType;
        this.strategy = original.strategy;
        this.page = original.page;
        this.line = original.line;
        this.leadingText = original.leadingText;
        this.trailingText = original.trailingText;
        this.index = original.index;
        this.length = original.length;
        this.stopAtKeyword = original.stopAtKeyword;
        this.detail = original.detail;
        this.regex = original.regex;
        this.cleanup = original.cleanup;
        this.afterConceptKey = original.afterConceptKey;
        this.beforeRegex = original.beforeRegex;
        this.cache = cache;
    }

    public String getDescription() {
        return description;
    }

    public String getJavaType() {
        return javaType;
    }

    public TextExtractorStrategy getStrategy() {
        return strategy;
    }

    public int getPage() {
        return page;
    }

    public int getLine() {
        return line;
    }

    public String getLeadingText() {
        return leadingText;
    }

    public String getTrailingText() {
        return trailingText;
    }

    public int getIndex() {
        return index;
    }

    public int getLength() {
        return length;
    }

    public String getStopAtKeyword() {
        return stopAtKeyword;
    }

    public boolean isDetail() {
        return detail;
    }

    public String getRegex() {
        return regex;
    }

    public String[] getCleanup() {
        return cleanup;
    }

    public String getAfterConceptKey() {
        return afterConceptKey;
    }

    public String getBeforeRegex() {
        return beforeRegex;
    }

    void textExtractor() throws ConceptException {
        if (strategy == TextExtractorStrategy.SELF) {
            textExtractor = new SelfTextExtractor();
        } else if (strategy == TextExtractorStrategy.OFFSET) {
            textExtractor = new OffsetTextExtractor(leadingText);
        } else if (strategy == TextExtractorStrategy.FIXED) {
            textExtractor = new FixedTextExtractor(index, length);
        } else if (strategy == TextExtractorStrategy.BETWEEN) {
            textExtractor = new BetweenTextExtractor(leadingText, trailingText);
        } else if (strategy == TextExtractorStrategy.PATTERN) {
            textExtractor = new PatternTextExtractor(regex, cleanup);
        } else if (strategy == TextExtractorStrategy.BETWEEN_CONCEPT_AND_PATTERN) {
            textExtractor = new ConceptAndPatternTextExtractor(cache.get(afterConceptKey), beforeRegex, true);
        } else {
            throw ConceptException.createUnsupportedStrategy(strategy);
        }
    }

    public StopAtConcept createStopAtConcept(int currentPage, int currentLine, String content) {
        if (stopAtKeyword != null && content.contains(stopAtKeyword)) {
            return new StopAtConcept(content, currentPage, currentLine);
        } else {
            return null;
        }
    }

    public boolean isProcessable(int currentPage, int currentLine, StopAtConcept stopAtConcept) {
        if (stopAtConcept != null) {
            return !stopAtConcept.hasBeenReached(currentPage, currentLine);
        } else if (detail && page != -1 && line != -1 && ((page == currentPage && currentLine >= line) || currentPage > page)) {
            return true;
        } else if (page != -1 && page == currentPage && line != -1 && line == currentLine) {
            return true;
        } else {
            return false;
        }
    }

    public Object value(String content, ConversionConfiguration configuration) throws ConceptException, ConversionException {
        return textExtractor.value(content, javaTypeClass(), configuration);
    }

    public String rawValue(String content) throws ConceptException {
        return textExtractor.rawValue(content);
    }

    private Class javaTypeClass() throws ConceptException {
        try {
            Class clazz = Class.forName(javaType);
            return clazz;
        } catch (ClassNotFoundException e) {
            throw ConceptException.createUnknownClass(javaType);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Concept)) {
            return false;
        }
        Concept other = (Concept) o;
        return Objects.equals(description, other.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description);
    }

    @Override
    public String toString() {
        return "Concept{" +
                "description='" + description + '\'' +
                ", javaType='" + javaType + '\'' +
                ", strategy=" + strategy +
                ", page=" + page +
                ", line=" + line +
                ", leadingText='" + leadingText + '\'' +
                ", trailingText='" + trailingText + '\'' +
                ", index=" + index +
                ", length=" + length +
                ", stopAtKeyword='" + stopAtKeyword + '\'' +
                ", expense=" + detail +
                ", regex='" + regex + '\'' +
                ", cleanup=" + Arrays.toString(cleanup) +
                ", afterConcept='" + afterConceptKey + '\'' +
                ", beforeRegex='" + beforeRegex + '\'' +
                '}';
    }

    @JsonPOJOBuilder
    public static class ConceptBuilder {
        String description;
        String javaType;
        TextExtractorStrategy strategy;
        Integer page;
        Integer line;
        String leadingText;
        String trailingText;
        Integer index;
        Integer length;
        String stopAtKeyword;
        Boolean detail;
        String regex;
        String[] cleanup;
        String afterConcept;
        String beforeRegex;

        public ConceptBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ConceptBuilder withJavaType(String javaType) {
            this.javaType = javaType;
            return this;
        }

        public ConceptBuilder withStrategy(TextExtractorStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public ConceptBuilder withPage(Integer page) {
            this.page = page;
            return this;
        }

        public ConceptBuilder withLine(Integer line) {
            this.line = line;
            return this;
        }

        public ConceptBuilder withLeadingText(String leadingText) {
            this.leadingText = leadingText;
            return this;
        }

        public ConceptBuilder withTrailingText(String trailingText) {
            this.trailingText = trailingText;
            return this;
        }

        public ConceptBuilder withIndex(Integer index) {
            this.index = index;
            return this;
        }

        public ConceptBuilder withLength(Integer length) {
            this.length = length;
            return this;
        }

        public ConceptBuilder withStopAtKeyword(String stopAtKeyword) {
            this.stopAtKeyword = stopAtKeyword;
            return this;
        }

        public ConceptBuilder withDetail(Boolean detail) {
            this.detail = detail;
            return this;
        }

        public ConceptBuilder withRegex(String regex) {
            this.regex = regex;
            return this;
        }

        public ConceptBuilder withCleanup(String[] cleanup) {
            this.cleanup = cleanup;
            return this;
        }

        public ConceptBuilder withAfterConcept(String afterConcept) {
            this.afterConcept = afterConcept;
            return this;
        }

        public ConceptBuilder withBeforeRegex(String beforeRegex) {
            this.beforeRegex = beforeRegex;
            return this;
        }

        public Concept build() throws ConceptException {
            return new Concept(this);
        }
    }
}
