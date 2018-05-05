package sample;

import sample.Constants.Modes;
import sample.cipher.BlowfishEncryption;
import sample.cipher.FileHeader;
import sample.cipher.RSAKeyEncoder;
import sample.cipher.User;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;

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

      byte[] encoded = RSAKeyEncoder.encode(inputInBytes, password);
      // RSA.writeKeyToFile("Testing"+ File.separator + "encoded.txt", encoded);
      System.out.println(new String(encoded));

      byte[] decoded = RSAKeyEncoder.decode(encoded, password);
      // RSA.writeKeyToFile("Testing"+ File.separator + "decoded.txt", decoded);
      System.out.println(new String(decoded));
    }

    if (true) {
      String password = "dupa";
      ArrayList<User> users = new ArrayList<>();
      KeyPairGenerator key_gen = KeyPairGenerator.getInstance("RSA");
      key_gen.initialize(2048);
      KeyPair keyPair = key_gen.generateKeyPair();
      User user = new User("maciek.dupa", "assassa".getBytes(), (RSAPublicKey) keyPair.getPublic());

      users.add(user);
      FileHeader fileHeader =
          new FileHeader("Blowfish", 80, 128, Modes.CFB, "dupa".getBytes(), users);

      BlowfishEncryption.encrypt(fileHeader, Paths.get("myfile.txt"), Paths.get("encryptedFile.txt"));

      byte[] fileHeaderBytes =
          BlowfishEncryption.decrypt(user, password, "encryptedFile.txt", "decryptedFile.txt");
      FileHeader readFileHeader = new FileHeader(fileHeaderBytes);
      System.out.println(readFileHeader.getBlockSize());


    }
  }
}
