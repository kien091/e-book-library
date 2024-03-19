package tdtu.edu.vn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user")
public class User implements Serializable {
    @Id
    private String id;
    private String username;
    private String position; //chức vụ công ty
    private String email;
    private String phone;
    private String password;
    private String confirmPassword;
    private String fullname;
    private String address;
    private String sex;
    private String birthday;
    private Date createday;
    private String subscribe; //mô tả bản thân
    private byte[] avatar;
    private String role;
    private boolean isLocked = false;





    public User(String username, String email, String password, String confirmPassword){
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

}


