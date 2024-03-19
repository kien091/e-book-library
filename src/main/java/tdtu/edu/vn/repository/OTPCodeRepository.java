package tdtu.edu.vn.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tdtu.edu.vn.model.OTPCode;

import java.util.Date;
import java.util.List;

public interface OTPCodeRepository extends MongoRepository<OTPCode, String> {
    OTPCode findByUserId(String userId);
    void deleteByUserId(String userId);
    List<OTPCode> findByCreatedAtBefore(Date expirationTime);
}