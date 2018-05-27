package sample.cipher;

import javafx.concurrent.Task;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPrivateKey;
import java.util.List;

import static sample.Constants.Constants.*;

public class BlowfishEncryption {

  public static Task encrypt(
      final FileHeader fileHeader, final Path inputPath, final Path outputPath) {
    return new Task() {
      @Override
      protected Object call() {
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
            byte[] buffer = new byte[1024];
            long numberOfBytesProcessed = 0L;
            int numberOfBytesRead;
            while ((numberOfBytesRead = inputStream.read(buffer)) >= 0) {
              cipherOutputStream.write(buffer);
              numberOfBytesProcessed += numberOfBytesRead;
              updateProgress(numberOfBytesProcessed, outputFileSize);
              buffer = new byte[1024];
            }
          } finally {
            if (cipherOutputStream != null) cipherOutputStream.close();
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }
    };
  }

  public static Task decrypt(
      final User checkedUser, final String password, String inputFilePath, String outputFilePath) {
    return new Task() {
      @Override
      protected Object call() {
        try {
          File inputFile = new File(inputFilePath);
          InputStream inputStream = Files.newInputStream(Paths.get(inputFilePath));
          OutputStream outputStream = Files.newOutputStream(Paths.get(outputFilePath));

          FileHeader fileHeader = new FileHeader(inputStream);

          User user = chooseUser(fileHeader.getApprovedUsers(), checkedUser.getEmail());
          RSAPrivateKey privateKey = UsersManager.loadPrivateKey(user.getEmail(), password);
          if(privateKey == null) privateKey = KeysGenerator.getRandomPrivateKey();
          byte[] encryptedSessionKeyBytes = user.getEncryptedSessionKey();

          SecretKey sessionKey = SessionKeyEncryptor.decrypt(encryptedSessionKeyBytes, privateKey);
          Cipher cipher = createCipherForFilesEncrypt(fileHeader, sessionKey, Cipher.DECRYPT_MODE);
          CipherOutputStream decryptionStream = new CipherOutputStream(outputStream, cipher);
          long fileSize = inputFile.length();
          try {
            byte[] buffer = new byte[1024];
            long numBytesProcessed = 0L;
            int numBytesRead;
            while ((numBytesRead = inputStream.read(buffer)) >= 0) {
              decryptionStream.write(buffer, 0, numBytesRead);
              numBytesProcessed += numBytesRead;
              updateProgress(numBytesProcessed, fileSize);
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }
    };
  }

  private static User chooseUser(List<User> users, String email) {
    return users.stream().filter(user -> user.getEmail().equals(email)).findAny().orElse(null);
  }

  private static Cipher createCipherForFilesEncrypt(
      FileHeader fileHeader, SecretKey sessionKey, int mode) {
    Cipher cipher = null;
    IvParameterSpec ivParameterSpec;
    try {
      switch (fileHeader.getCipherMode()) {
        case ECB:
          cipher = Cipher.getInstance(ECB_SPI + PADDING, PROVIDER);
          cipher.init(mode, sessionKey);
          break;
        case CBC:
          cipher = Cipher.getInstance(CBC_SPI + PADDING, PROVIDER);
          ivParameterSpec = new IvParameterSpec(fileHeader.getIV());
          cipher.init(mode, sessionKey, ivParameterSpec);
          break;
        case CFB:
          cipher = Cipher.getInstance(CFB_SPI + fileHeader.getBlockSize() + PADDING, PROVIDER);
          ivParameterSpec = new IvParameterSpec(fileHeader.getIV());
          cipher.init(mode, sessionKey, ivParameterSpec);
          break;
        case OFB:
          cipher = Cipher.getInstance(OFB_SPI + fileHeader.getBlockSize() + PADDING, PROVIDER);
          ivParameterSpec = new IvParameterSpec(fileHeader.getIV());
          cipher.init(mode, sessionKey, ivParameterSpec);
          break;
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (NoSuchProviderException e) {
      e.printStackTrace();
    } catch (InvalidAlgorithmParameterException e) {
      e.printStackTrace();
    }
    return cipher;
  }
}
