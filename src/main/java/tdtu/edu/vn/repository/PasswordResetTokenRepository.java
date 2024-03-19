package tdtu.edu.vn.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tdtu.edu.vn.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    PasswordResetToken findByToken(String token);
    void deleteByUserId(String userId);
}