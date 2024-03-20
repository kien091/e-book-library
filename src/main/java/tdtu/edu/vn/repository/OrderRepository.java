package tdtu.edu.vn.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tdtu.edu.vn.model.Order;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);
    Order findByActivationCodeId(String activationCodeIds);
    Order findByUserIdAndBookIdsContains(String userId, String bookId);

    // Modify
    @Query(value = "{'userId': ?0, 'orderStatus': 'CART'}")
    Order findCartByUserId(String userId);
}
