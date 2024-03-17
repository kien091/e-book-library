package tdtu.edu.vn.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tdtu.edu.vn.model.Category;

public interface CategoryRepository extends MongoRepository<Category, String>{
}
