package tdtu.edu.vn.service.ebook;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    public void sendEmail(String from, String to, String subject, String body) throws javax.mail.MessagingException {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setFrom(from, "DRM DOCUMENT");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(mimeMessage);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendOTPEmail(String to, String otp) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setFrom("phuongit9902@drm.com");
        helper.setTo(to);
        helper.setSubject("OTP for Login");
        helper.setText("Your OTP for login is: " + otp, true);

        javaMailSender.send(mimeMessage);
    }

    public void sendResetPasswordOTPEmail(String recipientEmail, String otp) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("phuongit9902@drm.com");
        helper.setTo(recipientEmail);
        helper.setSubject("OTP for Password Reset");
        helper.setText("Your OTP for resetting your password is: " + otp, true);

        javaMailSender.send(message);
    }

    @SneakyThrows
    public void sendResetPasswordLinkEmail(String recipientEmail, String resetPasswordLink) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("phuongit9902@drm.com", "DRM DOCUMENT");
        helper.setTo(recipientEmail);
        helper.setSubject("Reset Your Password");

        String emailContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <title>Reset Your Password</title>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background-color: #f2f2f2; padding: 20px; text-align: center; }" +
                "        .content { padding: 20px; }" +
                "        .button { display: inline-block; padding: 10px 20px; background-color: #007bff; color: #fff; text-decoration: none; border-radius: 4px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <h1>Reset Your Password</h1>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <p>Dear User,</p>" +
                "            <p>We received a request to reset your password. To proceed with resetting your password, please click the button below:</p>" +
                "            <p><a href='" + resetPasswordLink + "' class='button'>Reset Password</a></p>" +
                "            <p>If you did not request a password reset, please ignore this email. Your password will remain unchanged.</p>" +
                "            <p>Best regards,<br>Movie Online System</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";

        helper.setText(emailContent, true);

        javaMailSender.send(message);
    }
}
