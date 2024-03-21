package tdtu.edu.vn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@org.springframework.data.mongodb.core.mapping.Document(collection = "Document")
public class Document implements Serializable {
    @Id
    private String id;
    @TextIndexed
    private String name;
    private String authorId;
    private String publisherId;
    private String categoryId;
    private String thumbnail;
    private String pdfUrl;
    private String publicationDate;
    private String description;
    private Boolean drmEnabled;
    private String status;
    private int year = 2002;






}
