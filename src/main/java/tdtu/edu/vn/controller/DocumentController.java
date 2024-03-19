package tdtu.edu.vn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdtu.edu.vn.model.Document;
import tdtu.edu.vn.service.ebook.DocumentService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/home")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @GetMapping()
    public Page<Document> getAllDocuments(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        return documentService.getAllDocuments(PageRequest.of(page, size));
    }

    @GetMapping("/documents-free")
    public Page<Document> getFreeDocuments(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        return documentService.getFreeDocuments(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable String id) {
        Document document = documentService.getDocumentById(id);
        if (document == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(document);
    }

    @GetMapping("/search")
    public Page<Document> searchBooks(@RequestParam String searchTerm,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size){
        return documentService.searchDocuments(searchTerm, PageRequest.of(page, size));
    }

    @GetMapping("/category")
    public Page<Document> getDocumentsByCategoryName(
            @RequestParam String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return documentService.findDocumentsByCategoryName(categoryName, PageRequest.of(page, size));

    }


}
