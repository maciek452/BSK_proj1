package sample.cipher;

import lombok.Getter;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

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
