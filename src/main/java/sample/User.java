package sample;
import java.security.interfaces.RSAPublicKey;

public class User
{
    String name;
    byte[] encryptedKey;
    RSAPublicKey pubkey;

    public User(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return this.name;
    }

    public User()
    {
        name="NAME";
        encryptedKey=null;
        pubkey=null;
    }
}
