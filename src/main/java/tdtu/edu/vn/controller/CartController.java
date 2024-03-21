package tdtu.edu.vn.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdtu.edu.vn.model.*;
import tdtu.edu.vn.service.ebook.*;
import tdtu.edu.vn.util.JwtUtilsHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

            ActivationCode activationCode = acService.findValidCodeWithOrderIdAndBookId(orderService.getAllOrders(), document.getId());
            if(activationCode != null || !document.getDrmEnabled()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Activation code is not valid or document is not DRM enabled");
            }

            // update order
            Order order = orderService.getCartByUser(user);
            if (order == null) {
                order = new Order();
                order.setUserId(user.getId());
                List<String> bookIds = new ArrayList<>();
                bookIds.add(document.getId());
                order.setBookIds(bookIds);
                order.setOrderStatus(Order.OrderStatus.CART);
                order = orderService.createOrder(order);

                // not create ActivationCode in here (when user order it will be created)
            }else {
                List<String> bookIds = order.getBookIds();
                if(!bookIds.contains(document.getId())){
                    bookIds.add(document.getId());
                    order.setBookIds(bookIds);
                    order = orderService.updateOrder(order);
                }
            }

            // update orderItem
            OrderItem orderItem = orderItemService.getOrderItemByOrderIdAndBookId(order.getId(), document.getId());
            if(orderItem == null) {
                orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setBookId(document.getId());
                orderItem.setDate(new Date());
                orderItem.setCombo(false);
                orderItemService.createOrderItem(orderItem);
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Book already exists in cart");
            }

            return ResponseEntity.ok("Add book to cart successfully");
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }

    @PostMapping("/book/remove/{id}")
    public ResponseEntity<String> removeBookToCart(@PathVariable String id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String email = jwtUtilsHelper.getEmailFromToken(token);

            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            Document document = documentService.getDocumentById(id);
            System.out.println("document: "+document);
            if (document == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found");
            }

            Order cart = orderService.getCartByUser(user);
            System.out.println("cart "+cart);
            if (cart == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found");
            }

            OrderItem orderItem = orderItemService.findByOrderIdAndBookId(cart.getId(), id);
            System.out.println("orderItem "+orderItem);
            if (orderItem == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("OrderItem not found for bookId: " + id);
            }

            List<String> bookIds = cart.getBookIds();
            System.out.println("bookIds "+bookIds);
            bookIds.remove(document.getId());
            cart.setBookIds(bookIds);
            if (bookIds.isEmpty()) {
                orderService.deleteOrder(cart);
            } else {
                orderService.updateOrder(cart);
            }
            orderItemService.deleteOrderItem(orderItem.getId());
            System.out.println("book removed from cart successfully");

            return ResponseEntity.ok("Book removed from cart successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }

    @RequestMapping("/order")
    public ResponseEntity<Order> order(@RequestParam int validDays, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String email = jwtUtilsHelper.getEmailFromToken(token);

            User user = userService.findByEmail(email);
            if(user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            Order order = orderService.getCartByUser(user);
            if(order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            order = orderService.order(order, validDays);

            return ResponseEntity.ok(order);
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @RequestMapping("")
    public ResponseEntity<List<OrderItem>> getCart(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String email = jwtUtilsHelper.getEmailFromToken(token);

            User user = userService.findByEmail(email);
            if(user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            Order order = orderService.getCartByUser(user);
            if(order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            List<OrderItem> orderItems = orderItemService.getAllOrderItemByOrderId(order.getId());
            for (OrderItem orderItem : orderItems) {
                Document document = documentService.getDocumentById(orderItem.getBookId());
                if (document != null) {
                    orderItem.setBookId(document.getId());
                    orderItem.setComboId(document.getName());

                }
            }
            return ResponseEntity.ok(orderItems);
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
