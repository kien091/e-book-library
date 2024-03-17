package tdtu.edu.vn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Order")
public class Order {
    @Id
    private String id;
    private String userId;
    private List<String> bookIds;
    private String activationCodeId;
    private Date orderDate;
    private OrderStatus orderStatus;

    public enum OrderStatus {
        CART,
        ORDERED,
        ACCEPTED,
        CANCELLED
    }
}