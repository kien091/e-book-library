package tdtu.edu.vn.service.ebook;

import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tdtu.edu.vn.model.Category;
import tdtu.edu.vn.model.Document;
import tdtu.edu.vn.repository.CategoryRepository;
import tdtu.edu.vn.repository.DocumentRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class DocumentService {
    private DocumentRepository documentRepository;
    private CategoryRepository categoryRepository;

    // CRUD
    public Document createDocument(Document document) {
        return documentRepository.save(document);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
    public Page<Document> getAllDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable);
    }
    public Page<Document> getFreeDocuments(Pageable pageable) {
        return documentRepository.findByDrmEnabled(false, pageable);
    }

    public Document getDocumentById(String id) {
        return documentRepository.findById(id).orElse(null);
    }

    public Document updateDocument(Document document) {
        if (documentRepository.existsById(document.getId())) {
            return documentRepository.save(document);
        }
        return null;
    }

    public boolean deleteDocument(String id) {
        if (documentRepository.existsById(id)) {
            documentRepository.deleteById(id);
            return true;
        }
        return false;
    }



    // Other methods
    public Page<Document> searchDocuments(String searchTerm, Pageable pageable) {
        if ( searchTerm == null || searchTerm.isEmpty() || searchTerm.equals("all") ){
            return documentRepository.findAll(pageable);
        } else if (searchTerm.toLowerCase().contains("miễn phí")|| searchTerm.toLowerCase().contains("free")){
            return documentRepository.findByDrmEnabled(false, pageable);
        } else if (searchTerm.toLowerCase().contains("trả phí") || searchTerm.toLowerCase().contains("bản quyền")){
            return documentRepository.findByDrmEnabled(true, pageable);
        } else{
            try{
                int year = Integer.parseInt(searchTerm);
                return documentRepository.findByYear(year, pageable);
            } catch (NumberFormatException e){
                return documentRepository.searchByName(searchTerm, pageable);
            }
        }
    }


    public Page<Document> findDocumentsByCategoryName(String categoryName, Pageable pageable) {
        if ("all".equals(categoryName) || categoryName == null || categoryName.isEmpty()) {
            return documentRepository.findAll(pageable);
        } else {
            List<Category> categories = categoryRepository.findByName(categoryName);
            if (!categories.isEmpty()) {
                Category category = categories.get(0);
                return documentRepository.findByCategoryId(category.getId(), pageable);
            } else {
                // Trả về một Page rỗng nếu không tìm thấy danh mục
                return Page.empty(pageable);
            }
        }
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