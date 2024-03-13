package tdtu.edu.vn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Comment")
public class Comment {
    @Id
    private String id;
    private Document documentId;
    private User userId;
    private String content;
    private String date;
    private String status;//0: chưa duyệt, 1: đã duyệt
    public Comment(Document documentId, User userId, String content, String date, String status) {
        this.documentId = documentId;
        this.userId = userId;
        this.content = content;
        this.date = date;
        this.status = status;
    }

}
