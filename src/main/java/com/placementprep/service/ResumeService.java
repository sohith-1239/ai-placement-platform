package com.placementprep.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class ResumeService {
    @Autowired
    private GeminiService geminiService;

    public String extractTextFromPDF(MultipartFile file) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();

        try (PDDocument document = PDDocument.load(convFile)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            return text;
        } finally {
            convFile.delete();
        }
    }

    public String analyzeResume(String resumeText) {
        String prompt = "Analyze the following resume and evaluate it against software engineering roles. " + 
                        "Return a JSON object with 'score' (0-100 integer) and 'feedback' string. " + 
                        "Resume: " + resumeText;
        return geminiService.generateContent(prompt);
    }
}
