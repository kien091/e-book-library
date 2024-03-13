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
@Document(collection = "Favorite")
public class Favorite{
    @Id
    private String id;
    private String userId;
    private String bookId;

    public Favorite(String userId, String bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }

}
