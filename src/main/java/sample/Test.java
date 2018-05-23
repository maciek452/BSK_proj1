package sample;

import sample.Constants.Modes;
import sample.cipher.BlowfishEncryption;
import sample.cipher.FileHeader;
import sample.cipher.SHAKeyEncoder;
import sample.cipher.User;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;

public class Test {

  public static void main(String[] args) throws Exception {

    if (false) {
      String input = "mama !@#$%^&*()";
      String password = "dupa";

      byte[] inputInBytes = input.getBytes();
      // RSA.writeKeyToFile("Testing"+ File.separator + "input.txt", inputInBytes);
      System.out.println(new String(inputInBytes));
      byte[] readInputInBytes =
          Files.readAllBytes(Paths.get("Testing" + File.separator + "input.txt"));
      System.out.println(new String(readInputInBytes));

      byte[] encoded = SHAKeyEncoder.encode(inputInBytes, password);
      // RSA.writeKeyToFile("Testing"+ File.separator + "encoded.txt", encoded);
      System.out.println(new String(encoded));

      byte[] decoded = SHAKeyEncoder.decode(encoded, password);
      // RSA.writeKeyToFile("Testing"+ File.separator + "decoded.txt", decoded);
      System.out.println(new String(decoded));
    }

    if (true) {
      String password = "maciek";
      ArrayList<User> users = new ArrayList<>();
      byte[] publicKeyBytes =
          Files.readAllBytes(
              Paths.get("KeyPair" + File.separator + "publicKey" + File.separator + "maciek"));
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
      RSAPublicKey encodedPublicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);

      User user = new User("maciek", "maciek".getBytes(), encodedPublicKey);

      users.add(user);
      FileHeader fileHeader =
          new FileHeader("Blowfish", 80, 8, Modes.ECB, "dupa".getBytes(), users);

      BlowfishEncryption.encrypt(
          fileHeader, Paths.get("myfile.txt"), Paths.get("encryptedFile.txt"));


      byte[] encodedPrivateKeyBytes =
          BlowfishEncryption.decrypt(user, password, "encryptedFile.txt", "decryptedFile.txt");
      System.out.println(new String(encodedPrivateKeyBytes));
      byte[] readPrivateKey =
          Files.readAllBytes(
              Paths.get(
                  "KeyPair"
                      + File.separator
                      + "notEncPrivateKey"
                      + File.separator
                      + user.getEmail()));

      System.out.println(Arrays.equals(encodedPrivateKeyBytes, readPrivateKey));
    }
  }
}
