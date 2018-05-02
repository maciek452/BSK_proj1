package sample.cipher;

import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sample.Constants.Modes;

import javax.crypto.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
    private String IV;
    private ArrayList<User> approvedUsers;

    public FileHeader(String algorithm, int keySize, int blockSize, String cipherMode, String IV, ArrayList<User> approvedUsers) {
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.blockSize = blockSize;
        this.cipherMode = cipherMode;
        this.IV = IV;
        this.approvedUsers = approvedUsers;

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
        if (IV != null)
            root = addSimpleChild(root, "IV", IV);

        Element approvedUsersElement = document.createElement("ApprovedUsers");
        root.appendChild(approvedUsersElement);
        for (User user : approvedUsers) {
            approvedUsersElement = addUser(approvedUsersElement, user, sessionKey);
        }
        return transformToByteArray();
    }

    private byte[] transformToByteArray(){
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

    private Element addUser(Element approvedUsersElement, User user, SecretKey sessionKey){
        try {
            Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            rsa.init(1, user.getPublicKey());
            byte[] rsaEncodedSessionKey = rsa.doFinal(sessionKey.getEncoded());
            Element userElement = document.createElement("User");
            userElement = addSimpleChild(userElement, "Email", user.getEmail());
            userElement = addSimpleChild(userElement, "SessionKey", Base64.getEncoder().encodeToString(rsaEncodedSessionKey));
            approvedUsersElement.appendChild(userElement);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return approvedUsersElement;
    }

    private Element addSimpleChild(Element root, String name, String value) {
        Element simpleChild = document.createElement(name);
        simpleChild.appendChild(document.createTextNode(value.toString()));
        root.appendChild(simpleChild);
        return root;
    }
}
