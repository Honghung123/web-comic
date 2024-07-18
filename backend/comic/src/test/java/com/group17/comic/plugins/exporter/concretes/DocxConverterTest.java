package com.group17.comic.plugins.exporter.concretes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
class DocxConverterTest {
    @SpyBean
    private DocxExporter docxConverter;

    @Test
    void shouldBeEqualToDOCX() {
        Assertions.assertEquals("DOCX", docxConverter.getPluginName());
    }
}
