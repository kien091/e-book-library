package tdtu.edu.vn.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdtu.edu.vn.model.Document;
import tdtu.edu.vn.model.Order;
import tdtu.edu.vn.model.User;
import tdtu.edu.vn.service.ebook.OrderService;
import tdtu.edu.vn.service.ebook.UserService;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private OrderService orderService;
    private UserService userService;

    @PostMapping
    @RequestMapping("place-order")
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest orderRequest) {
        User user = userService.findByEmail(orderRequest.getUserEmail());
        Order order = orderService.placeOrder(user, orderRequest.getDocuments(), orderRequest.getValidDays());

        return ResponseEntity.ok(order);
    }

    private static class OrderRequest {
        private String userEmail;
        private List<Document> documents;
        private int validDays;

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public List<Document> getDocuments() {
            return documents;
        }

        public void setDocuments(List<Document> documents) {
            this.documents = documents;
        }

        public int getValidDays() {
            return validDays;
        }

        public void setValidDays(int validDays) {
            this.validDays = validDays;
        }
    }
}