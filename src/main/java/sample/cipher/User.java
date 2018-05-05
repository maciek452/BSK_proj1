package sample.cipher;

import lombok.Getter;
import lombok.Setter;

import java.security.interfaces.RSAPublicKey;

@Getter
@Setter
public class User {
  private String email;
  private byte[] encryptedKey;
  private RSAPublicKey publicKey;

  public User(String email) {
    this.email = email;
  }

  public User(String email, byte[] encryptedKey, RSAPublicKey publicKey) {
    this.email = email;
    this.encryptedKey = encryptedKey;
    this.publicKey = publicKey;
  }
}
