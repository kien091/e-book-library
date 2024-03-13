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
@Document(collection = "ActivationCode")
public class ActivationCode {
    @Id
    private String id;
    private String orderId;
    private String code;
    private List<String> bookWithDrmIds;
    private Date startDate;
    private Date endDate;
    private ActivationCodeStatus status;

    public enum ActivationCodeStatus {
        UNUSED,
        USED,
        EXPIRED
    }
}