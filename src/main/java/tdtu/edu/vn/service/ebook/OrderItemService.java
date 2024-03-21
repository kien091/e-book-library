    package tdtu.edu.vn.service.ebook;

    import lombok.AllArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.stereotype.Service;
    import tdtu.edu.vn.model.OrderItem;
    import tdtu.edu.vn.repository.OrderItemRepository;

    import java.util.List;

    @Service
    @AllArgsConstructor
    public class OrderItemService { // Modify
        private OrderItemRepository orderItemRepository;


        // CRUD
        public OrderItem createOrderItem(OrderItem orderItem) {
            return orderItemRepository.save(orderItem);
        }

        public Page<OrderItem> getAllOrderItems(Pageable pageable) {
            return orderItemRepository.findAll(pageable);
        }

        public List<OrderItem> getAllOrderItems() {
            return orderItemRepository.findAll();
        }

        public OrderItem getOrderItemById(String id) {
            return orderItemRepository.findById(id).orElse(null);
        }

        public OrderItem updateOrderItem(OrderItem orderItem) {
            if (orderItemRepository.existsById(orderItem.getId())) {
                return orderItemRepository.save(orderItem);
            }
            return null;
        }

        public boolean deleteOrderItem(String id) {
            if (orderItemRepository.existsById(id)) {
                orderItemRepository.deleteById(id);
                return true;
            }
            return false;
        }



        // Other methods
        public OrderItem getOrderItemByOrderIdAndBookId(String orderId, String bookId) {
            return orderItemRepository.findByBookIdAndOrderId(bookId, orderId);
        }

        public List<OrderItem> getAllOrderItemByOrderId(String orderId) {
            return orderItemRepository.findByOrderId(orderId);
        }

        public OrderItem findByBookIdAndCartId(String bookId, String cartId) {
            return orderItemRepository.findByBookIdAndOrderId(bookId, cartId);
        }
        public OrderItem findByOrderIdAndBookId(String orderId, String bookId) {
            return orderItemRepository.findByOrderIdAndBookId(orderId, bookId);
        }

    }
