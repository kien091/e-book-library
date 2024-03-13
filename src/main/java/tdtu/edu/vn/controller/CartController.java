package tdtu.edu.vn.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdtu.edu.vn.Payload.ResponseData;
import tdtu.edu.vn.model.*;
import tdtu.edu.vn.service.ebook.*;
import tdtu.edu.vn.util.JwtUtilsHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/cart")
public class CartController { // Modify
    UserService userService;
    DocumentService documentService;
    ActivationCodeService acService;
    OrderService orderService;
    OrderItemService orderItemService;

    JwtUtilsHelper jwtUtilsHelper;

    @PostMapping("/book/{id}")
    public ResponseEntity<String> addBookToCart(@PathVariable String id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String email = jwtUtilsHelper.getEmailFromToken(token);

            User user = userService.findByEmail(email);
            if(user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            Document document = documentService.getDocumentById(id);
            if(document == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found");
            }

            // update order
            Order order = orderService.getCartByUser(user);
            if (order == null) {
                order = new Order();
                order.setUserId(user.getId());
                ArrayList<String> bookIds = new ArrayList<>();
                bookIds.add(document.getId());
                order.setBookIds(bookIds);
                order.setOrderStatus(Order.OrderStatus.CART);
                order = orderService.createOrder(order);

                // not create ActivationCode in here (when user order it will be created)
            }else {
                List<String> bookIds = order.getBookIds();
                bookIds.add(document.getId());
                order = orderService.updateOrder(order);
            }

            // update orderItem
            OrderItem orderItem = orderItemService.getOrderItemByOrderId(order.getId());
            if(orderItem == null) {
                orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setBookId(document.getId());
                orderItem.setQuantity(1);
                orderItem.setDate(order.getOrderDate());
                orderItem.setCombo(false);
                orderItemService.createOrderItem(orderItem);
            }else {
                orderItem.setQuantity(orderItem.getQuantity() + 1);
                orderItemService.updateOrderItem(orderItem);
            }

            return ResponseEntity.ok("Add book to cart successfully");
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }

    // add method to order all books in cart (it will be created activation code for this)
    // add method to remove book from cart

    private String generateActivationCode() {
        Random random = new Random();
        int length = 16;

        // with 10 digits and 26 characters (use ascii code to convert to character)
        return random.ints(length, 0, 36)
                .mapToObj(i -> i < 10 ? String.valueOf(i) : String.valueOf((char) (i + 55)))
                .collect(Collectors.joining());
    }
}
