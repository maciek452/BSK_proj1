package sample;

import sample.Constants.Modes;
import sample.cipher.BlowfishEncrypt;
import sample.cipher.FileHeader;
import sample.cipher.User;

import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;

public class Test {

    public static void main(String[] args) throws Exception {


        ArrayList<User> users = new ArrayList<>();
        KeyPairGenerator key_gen = KeyPairGenerator.getInstance("RSA");
        key_gen.initialize(2048);
        KeyPair keyPair = key_gen.generateKeyPair();

        users.add(new User("maciek.dupa", "assassa".getBytes(), (RSAPublicKey) keyPair.getPublic()));
        FileHeader fileHeader = new FileHeader("Blowfish", 80, 128, Modes.CFB, "dupa",
                users);

        BlowfishEncrypt.encrypt(fileHeader, Paths.get("myfile.txt"), Paths.get("myOututFile"));

    }
}
