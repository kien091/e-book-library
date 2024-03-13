package tdtu.edu.vn.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tdtu.edu.vn.model.EncodeDocument;

public interface EncodeDocumentRepository extends MongoRepository<EncodeDocument, String> {
    EncodeDocument findByDocumentId(String documentId);
}
