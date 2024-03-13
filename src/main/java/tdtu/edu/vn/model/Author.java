package tdtu.edu.vn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Author")
public class Author {

    @Id
    private String id;
    private String name;
    private String description;
    private String thumbnail;
    private String status;

    public Author(String name, String description, String thumbnail, String status) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.status = status;
    }
}
