package sample.cipher;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SessionKeyGenerator {

  private static final int KEY_SIZE = 128;
  private static KeyGenerator generator;

  public static SecretKey generateSessionKey() {

    if (generator == null) {
      try {
        generator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom();
        Point point = MouseInfo.getPointerInfo().getLocation();
        secureRandom.setSeed(System.nanoTime() * point.x * point.y);
        generator.init(KEY_SIZE, secureRandom);
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
    }
    return generator.generateKey();
  }
}
