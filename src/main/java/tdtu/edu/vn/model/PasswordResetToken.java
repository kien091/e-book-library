package tdtu.edu.vn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "password_reset_token")
public class PasswordResetToken {
    @Id
    private String id;
    private String userId;
    private String token;
    private Date expiryDate;
    private int status; // 1: created, 2: verified, 3: used
}