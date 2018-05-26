package sample.cipher;

import static sample.Constants.Constants.*;

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
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;

public class BlowfishEncryption {

  public static void encrypt(
      final FileHeader fileHeader, final Path inputPath, final Path outputPath) {

    try {
      SecretKey sessionKey = SessionKeyGenerator.generateSessionKey();
      Cipher cipher = createCipherForFilesEncrypt(fileHeader, sessionKey, Cipher.ENCRYPT_MODE);
      byte[] fileHeaderXML = fileHeader.getAsByteArrayXML(sessionKey);
      InputStream inputStream = null;
      OutputStream outputStream = null;
      CipherOutputStream cipherOutputStream = null;
      try {
        inputStream = Files.newInputStream(inputPath);
        outputStream = Files.newOutputStream(outputPath);
        cipherOutputStream = new CipherOutputStream(outputStream, cipher);
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(fileHeaderXML.length);
        outputStream.write(byteBuffer.array());
        outputStream.write(fileHeaderXML);
        File inputFile = inputPath.toFile();
        long outputFileSize = inputFile.length();
        byte[] buffer = new byte[512];
        long numberOfBytesProcessed = 0L;
        int numberOfBytesRead;
        while ((numberOfBytesRead = inputStream.read(buffer)) >= 0) {
          cipherOutputStream.write(buffer);
          numberOfBytesProcessed += numberOfBytesRead;
          System.out.println(
              "Zaszyfrowano " + numberOfBytesProcessed * 100.0 / outputFileSize + "%");
          // TODO: update progress
          buffer = new byte[512];
        }
      } finally {
        if (cipherOutputStream != null) cipherOutputStream.close();
        if (outputStream != null) outputStream.close();
        if (inputStream != null) inputStream.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void decrypt(
      final User checkedUser, final String password, String inputFilePath, String outputFilePath) {
    try {
      File inputFile = new File(inputFilePath);
      InputStream inputStream = Files.newInputStream(Paths.get(inputFilePath));
      OutputStream outputStream = Files.newOutputStream(Paths.get(outputFilePath));
      int headerSize = readHeaderSize(inputStream);
      byte[] headerBytes = new byte[headerSize];
      inputStream.read(headerBytes);
      FileHeader fileHeader = new FileHeader(headerBytes);
      User user = chooseUser(fileHeader.getApprovedUsers(), checkedUser.getEmail());
      RSAPrivateKey privateKey = UsersManager.loadPrivateKey(user.getEmail(), password);
      byte[] encryptedSessionKeyBytes = user.getEncryptedSessionKey();

      SecretKey sessionKey = SessionKeyEncryptor.decrypt(encryptedSessionKeyBytes, privateKey);

      Cipher cipher = createCipherForFilesEncrypt(fileHeader, sessionKey, Cipher.DECRYPT_MODE);
      CipherOutputStream decryptionStream = new CipherOutputStream(outputStream, cipher);
      long fileSize = inputFile.length();
      try {
        byte[] buffer = new byte[512];
        long numBytesProcessed = 0L;
        int numBytesRead;
        while ((numBytesRead = inputStream.read(buffer)) >= 0) {
          decryptionStream.write(buffer, 0, numBytesRead);
          numBytesProcessed += numBytesRead;
          System.out.println("Odszyfrowano: " + numBytesProcessed * 100.0 / fileSize + "%");
          // TODO: update progress
        }
      } catch (IOException e) {
        throw new IOException("Nie mozna odczytac pliku: " + e.getLocalizedMessage(), e);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static User chooseUser(ArrayList<User> users, String email) {
    return users.stream().filter(user -> user.getEmail().equals(email)).findAny().orElse(null);
  }

  private static int readHeaderSize(InputStream inputStream) {
    byte[] headerSizeBytes = new byte[4];
    try {
      if (inputStream.read(headerSizeBytes) != 4) {
        throw new IOException("Nie można odczytać rozmiaru nagłowka.");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return headerSizeBytes.length != 4 ? 0 : ByteBuffer.wrap(headerSizeBytes).getInt();
  }

  private static Cipher createCipherForFilesEncrypt(
      FileHeader fileHeader, SecretKey sessionKey, int mode) {
    Cipher cipher = null;
    try {
      if (fileHeader.getCipherMode().equals(CFB)
          || fileHeader.getCipherMode().equals(OFB)) {
        cipher =
            Cipher.getInstance(
                BLOWFISH
                    + fileHeader.getCipherMode()
                    + fileHeader.getBlockSize()
                    + PADDING, PROVIDER);
      } else {
        cipher = Cipher.getInstance(BLOWFISH + fileHeader.getCipherMode() + PADDING, PROVIDER);
      }
      cipher.init(mode, sessionKey);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (NoSuchProviderException e) {
        e.printStackTrace();
    }
      return cipher;
  }
}
