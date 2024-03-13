package tdtu.edu.vn.service.ebook;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tdtu.edu.vn.model.ActivationCode;
import tdtu.edu.vn.model.Document;
import tdtu.edu.vn.model.Order;
import tdtu.edu.vn.repository.ActivationCodeRespository;
import tdtu.edu.vn.repository.OrderRepository;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class ActivationCodeService {
    private ActivationCodeRespository activationCodeRepository;
    private OrderRepository orderRepository;

    public ActivationCode findValidActivationCode(String code, String bookId) {
        return activationCodeRepository.findByCodeAndStatusAndBookWithDrmIdsContains(code, ActivationCode.ActivationCodeStatus.UNUSED, bookId);
    }

    public ActivationCode createActivationCode(Order order, List<String> drm_documents, int validDays) {
        ActivationCode activationCode =
                new ActivationCode(null,
                        order.getId(),
                        generateUniqueCode(),
                        drm_documents, new Date(),
                        new Date(System.currentTimeMillis() + validDays * 24 * 60 * 60 * 1000L),
                        ActivationCode.ActivationCodeStatus.UNUSED);

        activationCodeRepository.save(activationCode);

        order.setActivationCodeId(activationCode.getId());
        orderRepository.save(order);

        return activationCode;
    }

    private String generateUniqueCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public ActivationCode findValidActivationCodeForDocument(String bookId) {
        return activationCodeRepository.findByStatusAndBookWithDrmIdsContainsAndEndDateAfter(ActivationCode.ActivationCodeStatus.USED, bookId, new Date());
    }


    public ActivationCode findActivationCodeIdByOrderId(String orderId) {
        return activationCodeRepository.findByOrderId(orderId);
    }
}