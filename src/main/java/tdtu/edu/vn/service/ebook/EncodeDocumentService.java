package tdtu.edu.vn.service.ebook;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tdtu.edu.vn.model.EncodeDocument;
import tdtu.edu.vn.repository.EncodeDocumentRepository;

@Service
@AllArgsConstructor
public class EncodeDocumentService {
    private EncodeDocumentRepository edRepository;

    public EncodeDocument createEncodeDocument(EncodeDocument encodeDocument) {
        return edRepository.save(encodeDocument);
    }

    public EncodeDocument findByDocumentId(String documentId) {
        return edRepository.findByDocumentId(documentId);
    }

    public void deleteEncodeDocument(String id) {
        edRepository.deleteById(id);
    }
}
