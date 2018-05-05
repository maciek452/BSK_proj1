package sample.cipher;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class FileEncryptor {

  private final String ALGORITHM = "Blowfish";
  private String mode;
  private byte[] sessionKey;

  public FileEncryptor(String mode, byte[] sessionKey) {
    this.mode = mode;
    this.sessionKey = sessionKey;
  }

  //    public static void encryptFile(String input, String output, byte[] sessionKey, String mode)
  // {
  //
  //        SecretKey secret = new SecretKeySpec(sessionKey, ALGORITHM);
  //        try {
  //            Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
  //            cipher.init(Cipher.ENCRYPT_MODE, secret);
  //
  //            BufferedInputStream in = new BufferedInputStream(new FileInputStream(input));
  //            CipherOutputStream out = new CipherOutputStream(new BufferedOutputStream(new
  // FileOutputStream(
  //                    output)), cipher);
  //            int i;
  //            do {
  //                i = in.read();
  //                if (i != -1)
  //                    out.write(i);
  //            } while (i != -1);
  //            in.close();
  //            out.close();
  //
  //        } catch (NoSuchAlgorithmException e) {
  //            e.printStackTrace();
  //        } catch (NoSuchPaddingException e) {
  //            e.printStackTrace();
  //        } catch (InvalidKeyException e) {
  //            e.printStackTrace();
  //        } catch (FileNotFoundException e) {
  //            e.printStackTrace();
  //        } catch (IOException e) {
  //            e.printStackTrace();
  //        }
  //
  //    }
  //
  //    public static void decryptFile(String input, String output, byte[] sessionKey, String mode)
  // {
  //
  //    }

  public void encrypt(File inputFile, File outputFile) {
    doCrypto(Cipher.ENCRYPT_MODE, inputFile, outputFile);
    System.out.println("File encrypted successfully!");
  }

  public void decrypt(File inputFile, File outputFile) {
    doCrypto(Cipher.DECRYPT_MODE, inputFile, outputFile);
    System.out.println("File decrypted successfully!");
  }

  private void doCrypto(int cipherMode, File inputFile, File outputFile) {

    Key secretKey = new SecretKeySpec(sessionKey, ALGORITHM);
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(cipherMode, secretKey);

      FileInputStream inputStream = new FileInputStream(inputFile);
      byte[] inputBytes = new byte[(int) inputFile.length()];
      inputStream.read(inputBytes);

      byte[] outputBytes = cipher.doFinal(inputBytes);

      FileOutputStream outputStream = new FileOutputStream(outputFile);
      outputStream.write(outputBytes);

      inputStream.close();
      outputStream.flush();
      outputStream.close();

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      e.printStackTrace();
    }
  }
}
