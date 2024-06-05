package com.group17.comic.plugins.exporter.concretes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DocxConverterTest {
    @Mock
    private DocxExporter docxConverter;
    @Test
    void shouldBeEqualToDOCX() {
        Assertions.assertEquals(docxConverter.getPluginName(), "DOCX");
    }

    @Test
    void testBlobType() {

    }

    @Test
    void getConvertedFile() {

    }
}