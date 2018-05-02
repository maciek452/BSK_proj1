package sample.cipher;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RSA {
    private static Base64.Encoder encoder = Base64.getEncoder();

    public static int createRsaForUser(String userName, String password) {
        GenerateKeys gk;
        gk = new GenerateKeys(2048);
        gk.createKeys();
        byte[] encodedPublicKey = gk.getPublicKey().getEncoded();
        writeKeyToFile("KeyPair" + File.separator + "publicKey" + File.separator + userName, encodedPublicKey);
        byte[] encodedPrivateKey = gk.getPrivateKey().getEncoded();
        writeKeyToFile("KeyPair" + File.separator + "notEncPrivateKey" + File.separator + userName, encodedPrivateKey);
        byte[] encryptedPrivateKey = encryptPrivateKeyWithRSA(encodedPrivateKey, password);
        writeKeyToFile("KeyPair" + File.separator + "privateKey" + File.separator + userName, encryptedPrivateKey);
        byte[] loadedKey = loadPrivateKey("KeyPair" + File.separator + "privateKey" + File.separator + userName, password);

        System.out.println(encodedPrivateKey);
        System.out.println(loadedKey);
        System.out.println("takie same?: " + (encodedPrivateKey == loadedKey));

        return 0;
    }

    private static byte[] loadPrivateKey(String path, String password) {
        try {
            byte[] encryptedPrivateKey = Files.readAllBytes(Paths.get(path));
            Cipher cipher = createCipher(Cipher.DECRYPT_MODE, password);
            return cipher.doFinal(encryptedPrivateKey);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Cipher createCipher(int mode, String password) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(password.getBytes());
            SecretKey key = new SecretKeySpec(hash, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(mode, key);
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] encryptPrivateKeyWithRSA(byte[] encodedPrivateKey, String password) {
        try {
            Cipher cipher = createCipher(Cipher.ENCRYPT_MODE, password);
            return cipher.doFinal(encodedPrivateKey);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void writeKeyToFile(String path, byte[] key) {
        File f = new File(path);
        f.getParentFile().mkdirs();
        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(encoder.encodeToString(key).getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
