package sample.cipher;

import sample.Constants.Modes;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;

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
        byte[] buffer = new byte[1000];
        long numBytesProcessed = 0L;
        int numBytesRead;
        while ((numBytesRead = inputStream.read(buffer)) >= 0) {
          cipherOutputStream.write(buffer, 0, numBytesRead);
          numBytesProcessed += numBytesRead;
          System.out.println("Wykonano " + numBytesProcessed * 100.0 / outputFileSize + "%");
          // TODO: update progress
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

  public static byte[] decrypt(
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
      byte[] encodedPrivateKey =
          UsersRegistrar.loadPrivateKey(
              "KeyPair" + File.separator + "privateKey" + File.separator + user.getEmail(),
              password);
      byte[] encryptedSessionKeyBytes = user.getEncryptedSessionKey();
      PrivateKey privateKey =
          KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encodedPrivateKey));
      // byte[] sessionKeyBytes2 = SessionKeyEncryptor.decrypt(encryptedSessionKeyBytes,
      // (RSAPrivateKey) privateKey);

      byte[] sessionKeyBytes = Base64.getDecoder().decode(fileHeader.getSessionKey());
      SecretKey sessionKey = new SecretKeySpec(sessionKeyBytes, "Blowfish");

      Cipher cipher = createCipherForFilesEncrypt(fileHeader, sessionKey, Cipher.DECRYPT_MODE);
      CipherOutputStream decryptionStream = new CipherOutputStream(outputStream, cipher);
      long fileSize = inputFile.length();
      try {
        byte[] buffer = new byte[10000];
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

      return encodedPrivateKey;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (InvalidKeySpecException e) {
      e.printStackTrace();
    }
    return null;
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
      if (fileHeader.getCipherMode().equals(Modes.CFB)
          || fileHeader.getCipherMode().equals(Modes.OFB)) {
        cipher =
            Cipher.getInstance(
                Modes.BLOWFISH
                    + fileHeader.getCipherMode()
                    + fileHeader.getBlockSize()
                    + Modes.PADDING);
      } else {
        cipher = Cipher.getInstance(Modes.BLOWFISH + fileHeader.getCipherMode() + Modes.PADDING);
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
