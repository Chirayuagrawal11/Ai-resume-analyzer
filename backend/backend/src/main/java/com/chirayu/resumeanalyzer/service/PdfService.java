package com.chirayu.resumeanalyzer.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfService {
    public String extractText(MultipartFile file) {
        if(file.isEmpty() || !file.getOriginalFilename().toLowerCase().endsWith(".pdf"))
            throw new IllegalArgumentException("Only non-empty PDF files are supported");
        try(var document=Loader.loadPDF(file.getBytes())) {
            return new PDFTextStripper().getText(document);
        } catch(Exception e) {
            throw new IllegalArgumentException("Could not read PDF: "+e.getMessage());
        }
    }
}
