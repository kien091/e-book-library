package tdtu.edu.vn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "OrderItem")
public class OrderItem {
    @Id
    private String id;
    private String orderId;
    private String bookId;
    private String comboId;

    private int quantity;
    private Date date;
    private boolean isCombo;

    public OrderItem(String orderId, String bookId, String comboId, int quantity, Date date, boolean isCombo) {
        this.orderId = orderId;
        this.bookId = bookId;
        this.comboId = comboId;
        this.quantity = quantity;
        this.date = date;
        this.isCombo = isCombo;
    }
}
