package sample.cipher;

import static sample.Constants.Constants.MAIN_ALGORITHM;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class SessionKeyEncryptor {

    private static final String CIPHER_SPI = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";

  public static byte[] encrypt(SecretKey sessionKey, RSAPublicKey rsaPublicKey) {
    try {
      Cipher rsa = Cipher.getInstance(CIPHER_SPI);
      rsa.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
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
      Cipher rsa = Cipher.getInstance(CIPHER_SPI);
      rsa.init(Cipher.DECRYPT_MODE, privateKey);
      byte[] decryptedKeyBytes = rsa.doFinal(encryptedSessionKey);
      return new SecretKeySpec(decryptedKeyBytes, MAIN_ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      return SessionKeyGenerator.generateSessionKey();
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }
    return null;
  }
}
