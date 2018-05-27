package sample.cipher;

import static sample.Constants.Constants.MAIN_ALGORITHM;
import static sample.Constants.Constants.SESSION_KEY_SIZE;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SessionKeyGenerator {

  private static KeyGenerator generator;

  public static SecretKey generateSessionKey() {

    if (generator == null) {
      try {
        generator = KeyGenerator.getInstance(MAIN_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        Point point = MouseInfo.getPointerInfo().getLocation();
        secureRandom.setSeed(System.nanoTime() * point.x * point.y);
        generator.init(SESSION_KEY_SIZE, secureRandom);
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
    }
    return generator.generateKey();
  }
}
