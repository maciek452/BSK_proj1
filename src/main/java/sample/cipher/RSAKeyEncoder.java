package sample.cipher;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RSAKeyEncoder {

  private static Cipher cipher;

  private static void initCipher(int mode, String password) {
    try {
      MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
      byte[] hash = sha256.digest(password.getBytes());
      SecretKey key = new SecretKeySpec(hash, "AES");
      cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(mode, key);
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }

  public static byte[] encode(byte[] input, String password) {
    initCipher(Cipher.ENCRYPT_MODE, password);
    try {
      return cipher.doFinal(input);
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static byte[] decode(byte[] input, String password) {
    initCipher(Cipher.DECRYPT_MODE, password);
    try {
      return cipher.doFinal(input);
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
