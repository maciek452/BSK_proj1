package sample.cipher;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class SHAKeyEncoder {

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

  public static RSAPrivateKey decode(byte[] input, String password) {

    initCipher(Cipher.DECRYPT_MODE, password);
    try {
      byte[] decryptedPrivateKey = cipher.doFinal(input);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decryptedPrivateKey);
      return (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      return null;
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (InvalidKeySpecException e) {
      e.printStackTrace();
    }
    return null;
  }
}
