package sample.cipher;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;

public class SessionKeyEncryptor {

  public static byte[] encrypt(SecretKey sessionKey, User user) {
    try {
      Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
      rsa.init(Cipher.ENCRYPT_MODE, user.getPublicKey());
      return rsa.doFinal(sessionKey.getEncoded());
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static SecretKey decrypt(byte[] encryptedSessionKey, RSAPrivateKey privateKey){
    try {
      Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
      rsa.init(Cipher.DECRYPT_MODE, privateKey);
      byte[] decryptedKeyBytes = rsa.doFinal(encryptedSessionKey);
      return new SecretKeySpec(decryptedKeyBytes, "Blowfish");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }
    return null;
  }
}
