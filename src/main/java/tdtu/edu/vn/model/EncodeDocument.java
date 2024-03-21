package tdtu.edu.vn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "EncodeDocument")
public class EncodeDocument {
    @Id
    private String id;
    private String documentId;
    private String password;

    public EncodeDocument(String documentId, String password) {
        this.documentId = documentId;
        this.password = password;
    }
}
