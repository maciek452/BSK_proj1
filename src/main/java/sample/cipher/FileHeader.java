package sample.cipher;

import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sample.Constants.Modes;

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
import java.util.ArrayList;
import java.util.Base64;

@Getter
public class FileHeader {

  DocumentBuilder documentBuilder;
  Document document;
  private String algorithm;
  private int keySize;
  private int blockSize;
  private String cipherMode;
  private byte[] IV;
  private ArrayList<User> approvedUsers;
  private byte[] sessionKey; //TODO: Remove

  public FileHeader(
      String algorithm,
      int keySize,
      int blockSize,
      String cipherMode,
      byte[] IV,
      ArrayList<User> approvedUsers) {
    this.algorithm = algorithm;
    this.keySize = keySize;
    this.blockSize = blockSize;
    this.cipherMode = cipherMode;
    this.IV = IV;
    this.approvedUsers = approvedUsers;
    initializeFields();
  }

  public FileHeader(byte[] fileHeaderBytes) {
    approvedUsers = new ArrayList<>();
    initializeFields();
    try {
      document = documentBuilder.parse(new ByteArrayInputStream(fileHeaderBytes));
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
                  document.getElementsByTagName("BlockSize").item(0).getFirstChild().getNodeValue())
              .intValue();
    }
    if (!cipherMode.equals("ECB")) {
      IV = document.getElementsByTagName("IV").item(0).getFirstChild().getNodeValue().getBytes();
    }
    sessionKey = document.getElementsByTagName("SessionKey").item(0).getFirstChild().getNodeValue().getBytes();
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
        User u = new User(receiverName);
        u.setEncryptedSessionKey(encryptedKey);
        approvedUsers.add(u);
      }
    }
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
    if (cipherMode.equals(Modes.CFB) || cipherMode.equals(Modes.OFB))
      root = addSimpleChild(root, "BlockSize", Integer.toString(blockSize));
    root = addSimpleChild(root, "CipherMode", cipherMode);
    if (IV != null) root = addSimpleChild(root, "IV", new String(IV));
    root = addSimpleChild(root, "SessionKey", Base64.getEncoder().encodeToString(sessionKey.getEncoded()));

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
    byte[] rsaEncodedSessionKey = SessionKeyEncryptor.encrypt(sessionKey, user);
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
