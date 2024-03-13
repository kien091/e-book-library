package tdtu.edu.vn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Discount")
public class Discount {
    @Id
    private String id;
    private String name;
    private String discount;
    private String status;

    public Discount(String name, String discount, String status) {
        this.name = name;
        this.discount = discount;
        this.status = status;
    }

}
