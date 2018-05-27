package sample.cipher;

import lombok.Getter;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;

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

  public static RSAPrivateKey getRandomPrivateKey(){
    KeysGenerator keysGenerator = new KeysGenerator(2048);
    return (RSAPrivateKey) keysGenerator.getPair().getPrivate();
  }




}
