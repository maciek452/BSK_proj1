package sample.Constants;

import java.io.File;

public final class Constants {

  public static final String ECB = "ECB";
  public static final String ECB_SPI = "Blowfish/ECB";
  public static final String CBC = "CBC";
  public static final String CBC_SPI = "Blowfish/CBC";
  public static final String CFB = "CFB";
  public static final String CFB_SPI = "Blowfish/CFB";
  public static final String OFB = "OFB";
  public static final String OFB_SPI = "Blowfish/OFB";
  public static final String MAIN_ALGORITHM = "Blowfish";
  public static final String PROVIDER = "BC";
  public static final String PADDING = "/PKCS5Padding";
  public static final String PUBLIC_PATH =
      "KeyPair" + File.separator + "publicKey" + File.separator;
  public static final String PRIVATE_PATH =
      "KeyPair" + File.separator + "privateKey" + File.separator;
    public static final int SESSION_KEY_SIZE = 128;
  private Constants() {}
}
