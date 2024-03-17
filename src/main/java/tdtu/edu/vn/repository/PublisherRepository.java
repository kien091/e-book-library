package tdtu.edu.vn.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tdtu.edu.vn.model.Publisher;

public interface PublisherRepository extends MongoRepository<Publisher, String> {
}
