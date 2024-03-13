package tdtu.edu.vn.model;

import jdk.jfr.DataAmount;
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
@Document(collection = "Publisher")
public class Publisher{
    @Id
    private String id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String description;

    public Publisher(String name, String address, String phone, String email, String description) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.description = description;
    }


}
