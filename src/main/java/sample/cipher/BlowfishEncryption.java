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
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class BlowfishEncryption {

  public static void encrypt(
      final FileHeader fileHeader, final Path inputPath, final Path outputPath) {
    SecretKey sessionKey = SessionKeyGenerator.generateSessionKey();
    Cipher cipher =
        createCipher(
            fileHeader.getCipherMode(), fileHeader.getBlockSize(), sessionKey, Cipher.ENCRYPT_MODE);
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
        System.out.println("Wykonano " + numBytesProcessed * 100.0 / outputFileSize + "%");
        // TODO: update progress
        // updateProgress(numBytesProcessed, outputFileSize);
      }
      cipherOutputStream.close();
      outputStream.close();
      inputStream.close();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  public static void decrypt(
      final User checkedUser, final String password, String inputFilePath, String outputFilePath) {
    File inputFile = new File(inputFilePath);
    InputStream inputStream = null;
    OutputStream outputStream = null;
    try {
      inputStream = Files.newInputStream(Paths.get(inputFilePath));
      outputStream = Files.newOutputStream(Paths.get(outputFilePath));
      int headerSize = readHeaderSize(inputStream);
      byte[] headerBytes = new byte[headerSize];
      inputStream.read(headerBytes);
      FileHeader fileHeader = new FileHeader(headerBytes);
      User user = chooseUser(fileHeader.getApprovedUsers(), checkedUser.getEmail());
      byte[] encryptedPrivateKey = UsersRegistrar.loadPrivateKey(
          "KeyPair" + File.separator + "privateKey" + File.separator + user.getEmail(), password);
      byte[] encodedPrivateKey = RSAKeyEncoder.decode(encryptedPrivateKey, password);

      //Cipher cipher = createCipher(fileHeader.getCipherMode(), fileHeader.getBlockSize(), )
      //TODO: check if session key is encrypted correctly

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

  private static Cipher createCipher(
      String cipherMode, int blockSize, SecretKey sessionKey, int mode) {
    Cipher cipher = null;
    try {
      if (cipherMode.equals(Modes.CFB) || cipherMode.equals(Modes.OFB)) {
        cipher = Cipher.getInstance(Modes.SAMPLE + cipherMode + blockSize + Modes.PADDING);
      } else {
        cipher = Cipher.getInstance(Modes.SAMPLE + cipherMode + Modes.PADDING);
      }
      cipher.init(mode, sessionKey);
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
