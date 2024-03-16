package tdtu.edu.vn.service.ebook;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tdtu.edu.vn.model.Document;
import tdtu.edu.vn.model.Order;
import tdtu.edu.vn.model.OrderItem;
import tdtu.edu.vn.model.User;
import tdtu.edu.vn.repository.OrderItemRepository;
import tdtu.edu.vn.repository.OrderRepository;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private DocumentService documentService;
    private OrderRepository orderRepository;
    private ActivationCodeService activationCodeService;
    private OrderItemRepository orderItemRepository;

    public Order order(Order cart, int validDays) {
        cart.setOrderDate(new Date());
        cart.setOrderStatus(Order.OrderStatus.ORDERED);
        Order savedOrder = orderRepository.save(cart);

        List<Document> allDocuments = documentService.getAllDocuments()
                .stream()
                .filter(document -> cart.getBookIds().contains(document.getId()))
                .toList();

        List<String> drm_documents = allDocuments
                .stream()
                .filter(Document::getDrmEnabled)
                .map(Document::getId)
                .toList();

        activationCodeService.createActivationCode(savedOrder, drm_documents, validDays);

        return savedOrder;
    }

    // Modify
    public Order getCartByUser(User user) {
        if (orderRepository.findCartByUserId(user.getId()) != null)
            return orderRepository.findCartByUserId(user.getId());
        return null;
    }

    // Modify
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    // Modify
    public Order updateOrder(Order order) {
        if (orderRepository.existsById(order.getId()))
            return orderRepository.save(order);
        return null;
    }

    public boolean deleteOrder(Order order) {
        try {
            orderRepository.delete(order);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Modify (change logic after)
    public Order findByUserIdAndBookId(String userId, String bookId) {
        List<OrderItem> orderItems = orderItemRepository.findByBookId(bookId);
        return orderRepository.findAll()
                .stream()
                .filter(order -> order.getUserId().equals(userId))
                .filter(order -> orderItems.stream().anyMatch(orderItem -> orderItem.getOrderId().equals(order.getId())))
                .findFirst()
                .orElse(null);
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}