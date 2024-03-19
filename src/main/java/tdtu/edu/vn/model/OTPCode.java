package tdtu.edu.vn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "otp_code")
public class OTPCode {
    @Id
    private String id;
    private String userId;
    private String code;
    @Indexed(expireAfterSeconds = 60) // Thời gian hết hạn là 60 giây (1 phút)
    private Date createdAt;
}