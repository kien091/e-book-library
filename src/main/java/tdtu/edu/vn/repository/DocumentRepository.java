package tdtu.edu.vn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tdtu.edu.vn.model.Document;

public interface DocumentRepository extends MongoRepository<Document, String> {
    Document findByName(String name);

    @Query(value = "{ \"$text\": { \"$search\":  ?0 } }", sort = "{ \"score\": { \"$meta\": \"textScore\" }, \"_id\": -1 }")
    Page<Document> searchByName(String keyword, Pageable pageable);

    Page<Document> findByCategoryId(String categoryId, Pageable pageable);

    Page<Document> findByDrmEnabled(boolean drmEnabled, Pageable pageable);

    Page<Document> findByYear(int year, Pageable pageable);
}


