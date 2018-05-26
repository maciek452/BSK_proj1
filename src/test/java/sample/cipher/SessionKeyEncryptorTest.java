package sample.cipher;

import org.junit.Test;

import static org.junit.Assert.*;

import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class SessionKeyEncryptorTest {

    @Test
    public void SessionKeyEncryptionTest()throws Exception{
        Path path = Paths.get("mojZaszyfrowanyKlucz.txt");

        SecretKey key = SessionKeyGenerator.generateSessionKey();
        KeyPairGenerator key_gen = KeyPairGenerator.getInstance("RSA");
        key_gen.initialize(2048);
        KeyPair keyPair = key_gen.generateKeyPair();
        User user = new User("maciek", "maciek".getBytes(), (RSAPublicKey) keyPair.getPublic());
        byte[] encryptedKey =  SessionKeyEncryptor.encrypt(key, user.getPublicKey());
        Files.write(path, encryptedKey);
        byte[] readBytes = Files.readAllBytes(path);
        Files.delete(path);
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        SecretKey decryptedKey = SessionKeyEncryptor.decrypt(readBytes, privateKey);

        assertEquals(key, decryptedKey);
    }
}