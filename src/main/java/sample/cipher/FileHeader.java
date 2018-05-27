package sample.cipher;

import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static sample.Constants.Constants.*;

@Getter
public class FileHeader {

  private DocumentBuilder documentBuilder;
  private Document document;

  private String algorithm;
  private int keySize;
  private int blockSize;
  private String cipherMode;
  private byte[] IV;
  private List<User> approvedUsers;

  public FileHeader(String cipherMode, int blockSize, List<User> approvedUsers) {

    this.algorithm = MAIN_ALGORITHM;
    this.keySize = SESSION_KEY_SIZE;
    this.blockSize = blockSize;
    this.cipherMode = cipherMode;
    this.IV = randIV();
    this.approvedUsers = approvedUsers;
    initializeFields();
  }

  public FileHeader(InputStream inputStream) {
    int headerSize = readHeaderSize(inputStream);
    byte[] headerBytes = new byte[headerSize];
    try {
      inputStream.read(headerBytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
    approvedUsers = new ArrayList<>();
    initializeFields();
    try {
      document = documentBuilder.parse(new ByteArrayInputStream(headerBytes));
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    document.getDocumentElement().normalize();
    algorithm = document.getElementsByTagName("Algorithm").item(0).getFirstChild().getNodeValue();
    keySize =
        Integer.valueOf(
            document.getElementsByTagName("KeySize").item(0).getFirstChild().getNodeValue());
    cipherMode = document.getElementsByTagName("CipherMode").item(0).getFirstChild().getNodeValue();
    if ((cipherMode.equals("CFB")) || (cipherMode.equals("OFB"))) {
      blockSize =
          Integer.valueOf(
              document.getElementsByTagName("BlockSize").item(0).getFirstChild().getNodeValue());
    }
    if (!cipherMode.equals("ECB")) {
      IV = document.getElementsByTagName("IV").item(0).getFirstChild().getNodeValue().getBytes();
      IV = Base64.getDecoder().decode(IV);
    }
    NodeList userNodes = document.getElementsByTagName("User");
    for (int i = 0; i < userNodes.getLength(); i++) {
      Node userNode = userNodes.item(i);
      if (userNode.getNodeType() == 1) {
        Element userElement = (Element) userNode;
        String receiverName =
            userElement.getElementsByTagName("Email").item(0).getFirstChild().getNodeValue();
        String keyString =
            userElement.getElementsByTagName("SessionKey").item(0).getFirstChild().getNodeValue();
        byte[] encryptedKey = Base64.getDecoder().decode(keyString);
        User user = new User(receiverName);
        user.setEncryptedSessionKey(encryptedKey);
        approvedUsers.add(user);
      }
    }
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
    return ByteBuffer.wrap(headerSizeBytes).getInt();
  }

  private byte[] randIV() {
    byte[] randomBytes = new byte[8];
    try {
      SecureRandom.getInstanceStrong().nextBytes(randomBytes);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return randomBytes;
  }

  private void initializeFields() {
    try {
      documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      document = documentBuilder.newDocument();
      document.setXmlStandalone(true);
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
  }

  public byte[] getAsByteArrayXML(SecretKey sessionKey) {
    Element root = document.createElement("EncryptedFileHeader");
    document.appendChild(root);
    root = addSimpleChild(root, "Algorithm", algorithm);
    root = addSimpleChild(root, "KeySize", Integer.toString(keySize));
    if (cipherMode.equals(CFB) || cipherMode.equals(OFB))
      root = addSimpleChild(root, "BlockSize", Integer.toString(blockSize));
    root = addSimpleChild(root, "CipherMode", cipherMode);
    if (!cipherMode.equals(ECB)) root = addSimpleChild(root, "IV", Base64.getEncoder().encodeToString(IV));

    Element approvedUsersElement = document.createElement("ApprovedUsers");
    root.appendChild(approvedUsersElement);
    for (User user : approvedUsers) {
      approvedUsersElement = addUser(approvedUsersElement, user, sessionKey);
    }
    return transformToByteArray();
  }

  private byte[] transformToByteArray() {
    try {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty("indent", "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
      DOMSource source = new DOMSource(document);
      ByteArrayOutputStream xmlByteArrayOutputStream = new ByteArrayOutputStream();
      StreamResult result = new StreamResult(xmlByteArrayOutputStream);
      transformer.transform(source, result);
      return xmlByteArrayOutputStream.toByteArray();
    } catch (TransformerConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Element addUser(Element approvedUsersElement, User user, SecretKey sessionKey) {
    byte[] rsaEncodedSessionKey = SessionKeyEncryptor.encrypt(sessionKey, user.getPublicKey());
    Element userElement = document.createElement("User");
    userElement = addSimpleChild(userElement, "Email", user.getEmail());
    userElement =
        addSimpleChild(
            userElement, "SessionKey", Base64.getEncoder().encodeToString(rsaEncodedSessionKey));
    approvedUsersElement.appendChild(userElement);
    return approvedUsersElement;
  }

  private Element addSimpleChild(Element root, String name, String value) {
    Element simpleChild = document.createElement(name);
    simpleChild.appendChild(document.createTextNode(value));
    root.appendChild(simpleChild);
    return root;
  }
}
