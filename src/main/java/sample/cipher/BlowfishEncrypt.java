package sample.cipher;

import sample.Constants.Modes;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class BlowfishEncrypt {

    public static void encrypt(FileHeader fileHeader, final Path inputPath, final Path outputPath) {
        SecretKey sessionKey = SessionKeyGenerator.generateSessionKey();
        Cipher cipher = createCipher(fileHeader.getCipherMode(), fileHeader.getBlockSize(), sessionKey);
        byte[] fileHeaderXML = fileHeader.getAsByteArrayXML(sessionKey);
        try {
            InputStream inputStream = Files.newInputStream(inputPath);
            OutputStream outputStream = Files.newOutputStream(outputPath);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            byteBuffer.putInt(fileHeaderXML.length);
            outputStream.write(byteBuffer.array());
            outputStream.write(fileHeaderXML);
            File inputFile = inputPath.toFile();
            long outputFileSize = inputFile.length();
            byte[] buffer = new byte[1000];
            long numBytesProcessed = 0L;
            int numBytesRead;
            while ((numBytesRead = inputStream.read(buffer)) >= 0) {
                cipherOutputStream.write(buffer, 0, numBytesRead);
                numBytesProcessed += numBytesRead;
                System.out.println("Wykonano "+ numBytesProcessed*100.0/outputFileSize +"%");
                //TODO: update progress
                //updateProgress(numBytesProcessed, outputFileSize);
            }
            cipherOutputStream.close();
            outputStream.close();
            inputStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void decrypt(){


    }

    private static Cipher createCipher(String cipherMode, int blockSize, SecretKey sessionKey) {
        Cipher cipher = null;
        try {
            if (cipherMode.equals(Modes.CFB) || cipherMode.equals(Modes.OFB)) {
                cipher = Cipher.getInstance(Modes.SAMPLE + cipherMode + blockSize + Modes.PADDING);
            } else {
                cipher = Cipher.getInstance(Modes.SAMPLE + cipherMode + Modes.PADDING);
            }
            cipher.init(1, sessionKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return cipher;
    }
}
