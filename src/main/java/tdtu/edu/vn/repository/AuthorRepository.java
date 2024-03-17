package tdtu.edu.vn.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tdtu.edu.vn.model.Author;

public interface AuthorRepository extends MongoRepository<Author, String> {
}
