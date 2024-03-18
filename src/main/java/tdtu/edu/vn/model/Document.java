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
@org.springframework.data.mongodb.core.mapping.Document(collection = "Book")
public class Document implements Serializable {
    @Id
    private String id;
    @TextIndexed
    private String name;
    private String price;
    private int quantity;
    private String authorId;
    private String publisherId;
    private String categoryId;
    private String thumbnail;
    private String pdfUrl;
    private String publicationDate;
    private String description;
    private String discountId;
    private Boolean drmEnabled;
    private String status;
    private int year = 2002;

    public Document(String name, String price, int quantity, String authorId, String publisherId, String categoryId, String thumbnail, String pdfUrl, String publicationDate, String description, String discountId, Boolean drmEnabled, String status, int year) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.authorId = authorId;
        this.publisherId = publisherId;
        this.categoryId = categoryId;
        this.thumbnail = thumbnail;
        this.pdfUrl = pdfUrl;
        this.publicationDate = publicationDate;
        this.description = description;
        this.discountId = discountId;
        this.drmEnabled = drmEnabled;
        this.status = status;
        this.year = year;
    }




}
