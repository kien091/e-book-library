package tdtu.edu.vn.service.ebook;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tdtu.edu.vn.model.ActivationCode;
import tdtu.edu.vn.model.Order;
import tdtu.edu.vn.repository.ActivationCodeRespository;
import tdtu.edu.vn.repository.OrderRepository;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ActivationCodeService {
    private ActivationCodeRespository activationCodeRepository;
    private OrderRepository orderRepository;

    public ActivationCode findValidActivationCode(String code, String bookId) {
        return activationCodeRepository.findByCodeAndStatusAndBookWithDrmIdsContains(code, ActivationCode.ActivationCodeStatus.USED, bookId);
    }

    public ActivationCode createActivationCode(Order order, List<String> drm_documents, int validDays) {
        ActivationCode activationCode =
                new ActivationCode(null,
                        order.getId(),
                        generateUniqueCode(),
                        drm_documents, new Date(),
                        new Date(System.currentTimeMillis() + validDays  * 60 * 1000L), // set valid minutes (change after)
                        ActivationCode.ActivationCodeStatus.UNUSED);

        activationCodeRepository.save(activationCode);

        order.setActivationCodeId(activationCode.getId());
        orderRepository.save(order);

        return activationCode;
    }

    public String generateUniqueCode() {
        Random random = new Random();
        int length = 16;

        // with 10 digits and 26 characters (use ascii code to convert to character)
        return System.currentTimeMillis() + random.ints(length, 0, 36)
                .mapToObj(i -> i < 10 ? String.valueOf(i) : String.valueOf((char) (i + 55)))
                .collect(Collectors.joining());
    }

    public ActivationCode findValidActivationCodeForDocument(String bookId) {
        return activationCodeRepository.findByStatusAndBookWithDrmIdsContainsAndEndDateAfter(ActivationCode.ActivationCodeStatus.USED, bookId, new Date());
    }


    public ActivationCode findActivationCodeIdByOrderId(String orderId) {
        return activationCodeRepository.findByOrderId(orderId);
    }

    public ActivationCode getActivationCodeById(String id) {
        return activationCodeRepository.findById(id).orElse(null);
    }
}