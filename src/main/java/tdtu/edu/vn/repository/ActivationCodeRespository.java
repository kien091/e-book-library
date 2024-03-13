package tdtu.edu.vn.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tdtu.edu.vn.model.ActivationCode;

import java.util.Date;

public interface ActivationCodeRespository extends MongoRepository<ActivationCode, String> {
    ActivationCode findByCodeAndStatusAndBookWithDrmIdsContains(String code, ActivationCode.ActivationCodeStatus status, String bookId);

    ActivationCode findByStatusAndBookWithDrmIdsContainsAndEndDateAfter(ActivationCode.ActivationCodeStatus status, String bookId, Date endDate);

    ActivationCode findByOrderId(String orderId);
}