package tdtu.edu.vn.service.ebook;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tdtu.edu.vn.model.Category;
import tdtu.edu.vn.repository.CategoryRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // CRUD
    public Category createCategory(Category category){
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    public Category getCategoryById(String id){
        return categoryRepository.findById(id).orElse(null);
    }

    public Category updateCategory(Category category){
        if(categoryRepository.existsById(category.getId())){
            return categoryRepository.save(category);
        }
        return null;
    }

    public boolean deleteCategory(String id){
        if(categoryRepository.existsById(id)){
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
