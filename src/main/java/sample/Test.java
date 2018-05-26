package sample;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import static sample.Constants.Constants.*;
import sample.cipher.BlowfishEncryption;
import sample.cipher.FileHeader;
import sample.cipher.User;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

public class Test {

  public static void main(String[] args) throws Exception {
      Security.addProvider(new BouncyCastleProvider());
    if (true) {
      String password = "maciek";
      ArrayList<User> users = new ArrayList<>();
      byte[] publicKeyBytes =
          Files.readAllBytes(
              Paths.get("KeyPair" + File.separator + "publicKey" + File.separator + "maciek"));
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
      RSAPublicKey encodedPublicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);

      User user = new User("maciek", encodedPublicKey);

      users.add(user);
      FileHeader fileHeader =
          new FileHeader("Blowfish", 80, 32, ECB, "dupadupa".getBytes(), users);

      BlowfishEncryption.encrypt(
          fileHeader, Paths.get("myFile.txt"), Paths.get("encryptedFile.txt"));

      BlowfishEncryption.decrypt(user, password, "encryptedFile.txt", "decryptedFile.txt");
    }
  }
}
