package tdtu.edu.vn.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

public class PDFSecurity {
    public void encryptPDF(String source, String destination, String password) {
        try {
            PDDocument document = PDDocument.load(new File(source));

            AccessPermission ap = new AccessPermission();
            StandardProtectionPolicy spp = new StandardProtectionPolicy(password, password, ap);
            spp.setEncryptionKeyLength(128);
            spp.setPermissions(ap);
            document.protect(spp);

            document.save(destination);
            document.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void autoEnterPassword(String source, String password) {
        try {
            Robot robot = new Robot();
            Desktop.getDesktop().open(new File(source));

            for (char c : password.toCharArray()) {
                int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
            }

            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
