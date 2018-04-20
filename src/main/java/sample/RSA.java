package sample;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;
import java.security.*;


public class RSA {

    private static final Logger log = Logger.getLogger(RSA.class.getName());

    private static File privateKeyFile, publicKeyFile;
    private static Key privateKey, publicKey;

    public static int createRsaForUser(String userName, String password) {
        if (createFiles(userName) == -1 ||
                generateKeys() == -1)
            return -1;
        try (Writer privateKeyFileWriter = new FileWriter(privateKeyFile)) {
            privateKeyFileWriter.append(privateKey.toString());
        } catch (IOException e) {
        }
        try (Writer publicKeyFileWriter = new FileWriter(publicKeyFile)) {
            publicKeyFileWriter.append(publicKey.toString());
        } catch (IOException e) {
        }

        return 0;
    }

    private static int createFiles(String userName) {
        privateKeyFile = new File("privateKeys" + File.separator + userName);
        publicKeyFile = new File("publicKeys" + File.separator + userName);
        try {
            privateKeyFile.createNewFile();
            publicKeyFile.createNewFile();
        } catch (IOException e) {
            log.info("User with this name already exists. " + e.getMessage());
            return -1;
        }
        return 0;
    }

    private static int generateKeys() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.genKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            log.info(e.getMessage());
            return -1;
        }
        return 0;
    }
}
