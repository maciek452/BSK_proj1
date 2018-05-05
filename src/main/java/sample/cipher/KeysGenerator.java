package sample.cipher;

import lombok.Getter;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeysGenerator {

  private KeyPairGenerator keyGen;
  @Getter private KeyPair pair;

  public KeysGenerator(int keyLength) {
    try {
      this.keyGen = KeyPairGenerator.getInstance("RSA");
      this.keyGen.initialize(keyLength);
      this.pair = this.keyGen.generateKeyPair();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }
}
