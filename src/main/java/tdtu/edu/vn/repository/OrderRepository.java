package tdtu.edu.vn.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tdtu.edu.vn.model.Order;

public interface OrderRepository extends MongoRepository<Order, String> {
    Order findByUserId(String userId);
    Order findByActivationCodeId(String activationCodeIds);
    Order findByUserIdAndBookIdsContains(String userId, String bookId);

    // Modify
    @Query(value = "{'userId': ?0}", fields = "{'orderStatus': 'CART'}")
    Order findCartByUserId(String userId);
}
