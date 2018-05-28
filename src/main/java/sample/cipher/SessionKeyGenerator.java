package sample.cipher;

import static sample.Constants.Constants.MAIN_ALGORITHM;
import static sample.Constants.Constants.SESSION_KEY_SIZE;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SessionKeyGenerator {

  private static KeyGenerator generator;

  public static SecretKey generateSessionKey() {

    if (generator == null) {
      try {
        generator = KeyGenerator.getInstance(MAIN_ALGORITHM);
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        generator.init(SESSION_KEY_SIZE, secureRandom);
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
    }
    return generator.generateKey();
  }
}
