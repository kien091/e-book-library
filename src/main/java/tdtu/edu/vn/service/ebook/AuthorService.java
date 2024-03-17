package tdtu.edu.vn.service.ebook;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tdtu.edu.vn.model.Author;
import tdtu.edu.vn.repository.AuthorRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;

    // update CRUD
    public Author createAuthor(Author author){
        return authorRepository.save(author);
    }

    public List<Author> getAllAuthors(){
        return authorRepository.findAll();
    }

    public Author getAuthorById(String id){
        return authorRepository.findById(id).orElse(null);
    }

    public Author updateAuthor(Author author){
        if(authorRepository.existsById(author.getId()))
            return authorRepository.save(author);
        return null;
    }

    public boolean deleteAuthor(String id){
        if(authorRepository.existsById(id)){
            authorRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
