package tdtu.edu.vn.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tdtu.edu.vn.model.Category;

import java.util.List;

public interface CategoryRepository extends MongoRepository<Category, String>{
    List<Category> findByName(String name);
}
