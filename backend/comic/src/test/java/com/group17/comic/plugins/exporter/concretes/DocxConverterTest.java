package com.group17.comic.plugins.exporter.concretes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class DocxConverterTest {
    @Mock
    private DocxConverter docxConverter;
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