package sample.cipher;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static sample.Constants.Constants.PRIVATE_PATH;
import static sample.Constants.Constants.PUBLIC_PATH;

public class UsersManager {

  private static KeyFactory keyFactory;

  public static int createRsaForUser(String userName, String password) {
    KeysGenerator keysGenerator;
    keysGenerator = new KeysGenerator(2048);
    byte[] encodedPublicKey = keysGenerator.getPair().getPublic().getEncoded();
    writeKeyToFile(PUBLIC_PATH + userName, encodedPublicKey);
    byte[] encodedPrivateKey = keysGenerator.getPair().getPrivate().getEncoded();
    byte[] encryptedPrivateKey = RSAKeyEncoder.encode(encodedPrivateKey, password);
    writeKeyToFile(PRIVATE_PATH + userName, encryptedPrivateKey);
    return 0;
  }

  public static RSAPrivateKey loadPrivateKey(String userEmail, String password) {
    try {
      byte[] encryptedPrivateKey = Files.readAllBytes(Paths.get(PRIVATE_PATH + userEmail));
      return RSAKeyEncoder.decode(encryptedPrivateKey, password);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static RSAPublicKey loadPublicKey(String userEmail) {
    try {
      byte[] encryptedPrivateKey = Files.readAllBytes(Paths.get(PUBLIC_PATH + userEmail));
      return convertBytesToPrivateKey(encryptedPrivateKey);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static void writeKeyToFile(String path, byte[] key) {
    try {
      FileUtils.writeByteArrayToFile(new File(path), key);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static RSAPublicKey convertBytesToPrivateKey(byte[] publicKeyBytes) {
    if (keyFactory == null) {
      initKeyFactory();
    }
    try {
      return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
    } catch (InvalidKeySpecException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static void initKeyFactory() {
    try {
      keyFactory = KeyFactory.getInstance("RSA");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }
}
