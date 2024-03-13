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

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable String id)
    {
        Document book = documentService.getDocumentById(id);
        if(book == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    @GetMapping("/search")
    public Page<Document> searchBooks(@RequestParam String searchTerm,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size){
        return documentService.searchDocuments(searchTerm, PageRequest.of(page, size));
    }

    @GetMapping("category/{categoryId}")
    public Page<Document> getDocumentsByCategory(@PathVariable String categoryId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size){
        return documentService.findCategory(categoryId, PageRequest.of(page, size));

    }


}
