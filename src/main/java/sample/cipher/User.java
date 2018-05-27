package sample.cipher;

import lombok.Getter;
import lombok.Setter;

import java.security.interfaces.RSAPublicKey;

@Getter
@Setter
public class User {
  private String email;
  private RSAPublicKey publicKey;
  private byte[] encryptedSessionKey;

  public User(String email) {
    this.email = email;
  }

  public User(String email, RSAPublicKey publicKey) {
    this.email = email;
    this.publicKey = publicKey;
  }

  @Override
  public String toString(){
    return email;
  }
}
