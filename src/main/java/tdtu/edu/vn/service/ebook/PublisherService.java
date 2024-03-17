package tdtu.edu.vn.service.ebook;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tdtu.edu.vn.model.Publisher;
import tdtu.edu.vn.repository.PublisherRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class PublisherService {
    private final PublisherRepository publisherRepository;

    // CRUD
    public Publisher createPublisher(Publisher publisher) {
        return publisherRepository.save(publisher);
    }

    public List<Publisher> getAllPublishers() {
        return publisherRepository.findAll();
    }

    public Publisher getPublisherById(String id) {
        return publisherRepository.findById(id).orElse(null);
    }

    public Publisher updatePublisher(Publisher publisher) {
        if (publisherRepository.existsById(publisher.getId())) {
            return publisherRepository.save(publisher);
        }
        return null;
    }

    public boolean deletePublisher(String id) {
        if (publisherRepository.existsById(id)) {
            publisherRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
