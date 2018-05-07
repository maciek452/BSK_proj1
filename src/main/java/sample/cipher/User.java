package sample.cipher;

import lombok.Getter;
import lombok.Setter;

import java.security.interfaces.RSAPublicKey;

@Getter
@Setter
public class User {
  private String email;
  private byte[] encryptedSessionKey;
  private RSAPublicKey publicKey;

  public User(String email) {
    this.email = email;
  }

  public User(String email, byte[] encryptedSessionKey, RSAPublicKey publicKey) {
    this.email = email;
    this.encryptedSessionKey = encryptedSessionKey;
    this.publicKey = publicKey;
  }
}
