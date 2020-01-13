package com.popoyo.text.parser;

import com.popoyo.text.converter.ConversionConfiguration;
import com.popoyo.text.converter.ConversionException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author aalaniz
 */
public class TextParser {
    private static final Logger LOGGER = Logger.getLogger(TextParser.class.getName());

    private static final String FILE_NOT_FOUND = "El archivo [%s] no existe";
    private static final String NO_ACCESS_PERMISSION = "No tiene permisos para extraer texto del pdf [%s]";
    private static final String PDF_IS_ENCRYPTED = "El archivo pdf [%s] esta encriptado";

    private final File file;
    private final List<Concept> concepts;
    private final ConversionConfiguration configuration;

    public TextParser(String path, List<Concept> concepts) {
        this(path, concepts, ConversionConfiguration.DEFAULT_CONFIGURATION);
    }

    public TextParser(String path, List<Concept> concepts, ConversionConfiguration configuration) {
        this.file = new File(path);
        this.concepts = concepts;
        this.configuration = configuration;
    }

    public TextParsingResult parsePdf() throws IOException {
        if (!file.exists()) {
            throw new IOException(String.format(FILE_NOT_FOUND, file.getPath()));
        }
        try (PDDocument pdfDocument = PDDocument.load(file)) {
            if (!pdfDocument.getCurrentAccessPermission().canExtractContent()) {
                throw new IOException(String.format(NO_ACCESS_PERMISSION, file.getPath()));
            }
            if (pdfDocument.isEncrypted()) {
                throw new IOException(String.format(PDF_IS_ENCRYPTED, file.getPath()));
            }

            PDFTextStripper stripper = new PDFTextStripper();
            TextParsingResult result = new TextParsingResult();
            StopAtConcept stopAtConcept = null;

            for (int page = 1; page < pdfDocument.getNumberOfPages(); page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);

                String currentPageContent = stripper.getText(pdfDocument);
                String[] lines = currentPageContent.split("\\n");

                for (int j = 0; j < lines.length; j++) {
                    int line = j + 1;
                    String content = lines[j];

                    for (Concept concept : concepts) {
                        try {
                            if (stopAtConcept == null) {
                                stopAtConcept = concept.createStopAtConcept(page, line, content);
                            }
                            if (concept.isProcessable(page, line, stopAtConcept)) {
                                Object value = concept.value(content, configuration);
                                ConceptOutput conceptOutput = new ConceptOutput(page, line, concept, value);
                                result.addConceptOutput(conceptOutput);
                            }
                        } catch (ConceptException | ConversionException | IllegalArgumentException | NullPointerException exc) {
                            ConceptError conceptError = new ConceptError(page, line, concept, content, exc.getMessage());
                            result.addConceptError(conceptError);
                        }
                    }
                }
            }

            return result;
        }
    }
}
