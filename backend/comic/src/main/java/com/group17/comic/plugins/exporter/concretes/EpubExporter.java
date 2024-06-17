package com.group17.comic.plugins.exporter.concretes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

import com.group17.comic.dtos.request.ChapterRequest;
import com.group17.comic.dtos.response.ChapterFile;
import com.group17.comic.plugins.exporter.IFileExporter;
import com.group17.comic.utils.FileUtility;
import com.group17.comic.utils.StringUtility;

import lombok.SneakyThrows;

public class EpubExporter implements IFileExporter {
    private static final String UPLOAD_DIR = "backend/comic/src/main/java/com/group17/comic/plugins/exporter/uploads/";
    private static final UUID PLUGIN_ID = UUID.randomUUID();

    @Override
    public UUID getId() {
        return PLUGIN_ID;
    }

    @Override
    public String getPluginName() {
        return "EPUB";
    }

    @Override
    public String getBlobType() {
        return "application/epub+zip";
    }

    @SneakyThrows
    @Override
    public ChapterFile getConvertedFile(ChapterRequest chapterDto) {
        String formatTitle = StringUtility.removeDiacriticalMarks(chapterDto.title());
        formatTitle = formatTitle.replaceAll("[^a-zA-Z0-9\\s]", "-").trim();
        String fileName = formatTitle + ".epub";
        // Wirte content to epub file and save it to folder
        String uploadFolderAbsolutePath = Paths.get(UPLOAD_DIR).toAbsolutePath().toString();
        File uploadFolderFile = new File(uploadFolderAbsolutePath);
        FileUtility.deleteDirectory(uploadFolderFile);
        FileUtility.createDirectory(uploadFolderFile);
        this.createEpubFile(chapterDto.content(), fileName);
        // Get epub file from folder to return to client
        InputStream epubStream = new FileInputStream(UPLOAD_DIR + fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.setContentLength(Files.size(Paths.get(UPLOAD_DIR + fileName)));
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
        InputStreamResource resource = new InputStreamResource(epubStream);
        return new ChapterFile(headers, resource);
    }

    @SneakyThrows
    public void createEpubFile(String htmlContent, String epubFileName) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(UPLOAD_DIR + epubFileName))) {
            // Tạo và ghi nội dung vào file HTML
            ZipEntry htmlEntry = new ZipEntry("index.html");
            zos.putNextEntry(htmlEntry);
            zos.write(htmlContent.getBytes(StandardCharsets.UTF_8));
            // Tạo và ghi vào file 'container.xml'
            String containerXml =
                    """
					<?xml version="1.0" encoding="UTF-8"?>
					<container version="1.0" xmlns="urn:oasis:names:tc:opendocument:xmlns:container">
						<rootfiles>
							<rootfile full-path="content.opf" media-type="application/oebps-package+xml"/>
						</rootfiles>
					</container>""";
            ZipEntry containerEntry = new ZipEntry("META-INF/container.xml");
            zos.putNextEntry(containerEntry);
            zos.write(containerXml.getBytes());
            // Tạo và ghi vào file 'content.opf'
            String contentOpf =
                    """
					<?xml version="1.0" encoding="UTF-8"?>
					<package version="3.0" xmlns="http://www.idpf.org/2007/opf">
						<metadata/>
						<manifest>
							<item id="index" href="index.html" media-type="application/xhtml+xml"/>
						</manifest>
						<spine>
							<itemref idref="index"/>
						</spine>
					</package>""";
            ZipEntry opfEntry = new ZipEntry("content.opf");
            zos.putNextEntry(opfEntry);
            zos.write(contentOpf.getBytes());
        } catch (IOException e) {
            throw new IOException("Error to create Epub file", e);
        }
    }
}
