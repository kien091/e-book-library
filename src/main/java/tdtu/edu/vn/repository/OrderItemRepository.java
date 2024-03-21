package tdtu.edu.vn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tdtu.edu.vn.model.OrderItem;

import java.util.List;

@Repository
public interface OrderItemRepository extends MongoRepository<OrderItem, String> { // Modify
    List<OrderItem> findByOrderId(String orderId);

    List<OrderItem> findByBookId(String orderId);

    OrderItem findByBookIdAndOrderId(String bookId, String orderId);

    OrderItem findByOrderIdAndBookId(String orderId, String bookId);
}
