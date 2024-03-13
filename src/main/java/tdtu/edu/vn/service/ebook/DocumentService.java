package tdtu.edu.vn.service.ebook;

import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tdtu.edu.vn.model.Document;
import tdtu.edu.vn.repository.DocumentRepository;

import java.io.File;
import java.io.IOException;

@Service
@AllArgsConstructor
public class DocumentService {
    private DocumentRepository documentRepository;

    public Page<Document> getAllDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable);
    }

    public Document getDocumentById(String id) {
        return documentRepository.findById(id).get();
    }

    public Page<Document> searchDocuments(String searchTerm, Pageable pageable) {
        return documentRepository.searchByName(searchTerm, pageable);
    }

    public Page<Document> findCategory(String categoryId, Pageable pageable) {
        return documentRepository.findByCategoryId(categoryId, pageable);
    }

    public Document createDocument(Document document) {
        return documentRepository.save(document);
    }

    public Document updateDocument(Document document) {
        if (!documentRepository.existsById(document.getId())) {
            return null;
        }

        return documentRepository.save(document);
    }

    public boolean deleteDocument(String id) {
        try {
            documentRepository.deleteById(id);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public Document encryptDocument(Document document) throws IOException {
        File sourceFile = new File(document.getPdfUrl());
        File encryptedFile = encryptPDF(sourceFile.getAbsolutePath());
        String encryptedPdfUrl = encryptedFile.getAbsolutePath();
        document.setPdfUrl(encryptedPdfUrl);
        document.setDrmEnabled(true);

        return document;
    }

    private File encryptPDF(String sourceFile) throws IOException {
        PDDocument doc = PDDocument.load(new File(sourceFile));
        AccessPermission ap = new AccessPermission();
        StandardProtectionPolicy spp = new StandardProtectionPolicy("owner", "user", ap);
        spp.setEncryptionKeyLength(128);
        spp.setPermissions(ap);
        doc.protect(spp);

        File encryptedFile = File.createTempFile("encrypted", ".pdf");
        doc.save(encryptedFile);
        doc.close();

        return encryptedFile;
    }
}