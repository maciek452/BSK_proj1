package sample.cipher;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UsersRegistrar {

  public static int createRsaForUser(String userName, String password) {
    KeysGenerator keysGenerator;
    keysGenerator = new KeysGenerator(2048);
    byte[] encodedPublicKey = keysGenerator.getPair().getPublic().getEncoded();
    writeKeyToFile(
        "KeyPair" + File.separator + "publicKey" + File.separator + userName, encodedPublicKey);
    byte[] encodedPrivateKey = keysGenerator.getPair().getPrivate().getEncoded();
    writeKeyToFile(
        "KeyPair" + File.separator + "notEncPrivateKey" + File.separator + userName,
        encodedPrivateKey);
    byte[] encryptedPrivateKey = SHAKeyEncoder.encode(encodedPrivateKey, password);
    writeKeyToFile(
        "KeyPair" + File.separator + "privateKey" + File.separator + userName, encryptedPrivateKey);
    byte[] loadedKey =
        loadPrivateKey(
            "KeyPair" + File.separator + "privateKey" + File.separator + userName, password);

    System.out.println(new String(encodedPrivateKey));
    System.out.println(new String(loadedKey));
    System.out.println(
        "takie same?: " + (new String(encodedPrivateKey).equals(new String(loadedKey))));

    return 0;
  }

  public static byte[] loadPrivateKey(String path, String password) {
    try {
      byte[] encryptedPrivateKey = Files.readAllBytes(Paths.get(path));
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
