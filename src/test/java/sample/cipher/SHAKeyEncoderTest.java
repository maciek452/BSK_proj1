package sample.cipher;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static org.junit.Assert.*;

public class SHAKeyEncoderTest {

    @Test
    public void keyEncryptionTest() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String passsword = "password";
        Path path = Paths.get("testFile.txt");

        KeysGenerator keysGenerator;
        keysGenerator = new KeysGenerator(2048);
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keysGenerator.getPair().getPrivate();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keysGenerator.getPair().getPublic();

        byte[] privateKeyBytes = keysGenerator.getPair().getPrivate().getEncoded();
        byte[] publicKeyBytes = keysGenerator.getPair().getPublic().getEncoded();

        byte[] encryptedPrivateKey = SHAKeyEncoder.encode(privateKeyBytes, passsword);
        Files.write(path, encryptedPrivateKey);
        byte[] readPrivateKey = Files.readAllBytes(path);
        Files.delete(path);
        byte[] decryptedPrivateKey = SHAKeyEncoder.decode(readPrivateKey, passsword);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decryptedPrivateKey);
        RSAPrivateKey encodedPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);

        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        RSAPublicKey encodedPublicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);

        assertEquals(encodedPrivateKey, rsaPrivateKey);
        assertEquals(encodedPublicKey, rsaPublicKey);
    }

}