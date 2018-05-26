package sample.cipher;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.interfaces.RSAPrivateKey;

public class UsersManager {
  private static String PUBLIC_PATH = "KeyPair" + File.separator + "publicKey" + File.separator;
  private static String PRIVATE_PATH = "KeyPair" + File.separator + "privateKey" + File.separator;

  public static int createRsaForUser(String userName, String password) {
    KeysGenerator keysGenerator;
    keysGenerator = new KeysGenerator(2048);
    byte[] encodedPublicKey = keysGenerator.getPair().getPublic().getEncoded();
    writeKeyToFile(PUBLIC_PATH + userName, encodedPublicKey);
    byte[] encodedPrivateKey = keysGenerator.getPair().getPrivate().getEncoded();
    byte[] encryptedPrivateKey = SHAKeyEncoder.encode(encodedPrivateKey, password);
    writeKeyToFile(PRIVATE_PATH + userName, encryptedPrivateKey);
    return 0;
  }

  public static RSAPrivateKey loadPrivateKey(String userEmail, String password) {
    try {
      byte[] encryptedPrivateKey = Files.readAllBytes(Paths.get(PRIVATE_PATH + userEmail));
      return SHAKeyEncoder.decode(encryptedPrivateKey, password);
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
}
