package tdtu.edu.vn.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.core.io.ByteArrayResource;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class PDFSecurity {
    public static void encryptPDF(String source, String destination, String password) {
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

    public static ByteArrayResource openEncryptedPdf(String source, String decryptedPassword) {
        try {
            PDDocument pdDocument = PDDocument.load(new File(source), decryptedPassword);

            int keyLength = 128;
            AccessPermission ap = new AccessPermission();
            ap.setCanPrint(false);

            StandardProtectionPolicy spp = new StandardProtectionPolicy(decryptedPassword, decryptedPassword, ap);
            spp.setEncryptionKeyLength(keyLength);
            spp.setPermissions(ap);

            pdDocument.setAllSecurityToBeRemoved(true);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            pdDocument.save(byteArrayOutputStream);

            pdDocument.protect(spp);

            pdDocument.close();

            return new ByteArrayResource(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
