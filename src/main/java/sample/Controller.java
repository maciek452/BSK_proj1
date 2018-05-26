package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.cipher.User;
import sample.cipher.UsersManager;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static sample.Constants.Constants.*;

public class Controller implements Initializable {

  // Lists
  public static final ObservableList<User> listReceiverEncrypt =
      FXCollections.observableArrayList();
  public static final ObservableList<User> listReceiverDecrypt =
      FXCollections.observableArrayList();
  public static final ObservableList<TextArea> textAreasPathsList =
      FXCollections.observableArrayList();
  private Stage stage;
  private Task encryptionTask, decryptionTask;
  private File encryptInputFile, encryptOutputFile, decryptInputFile, decryptOutputFile;
  // ChoiceBoxes
  @FXML private ChoiceBox modeChoiceBox;
  @FXML private ChoiceBox<Integer> subblockChoiceBox;

  // MenuItems
  @FXML private MenuItem creatorsMenuItem;
  @FXML private MenuItem algorithmDescriptionMenuItem;
  @FXML private MenuItem createRsaMenuItem;

  // Buttons
  @FXML private Button encryptButton;
  @FXML private Button decryptButton;
  @FXML private Button encryptSourceButton;
  @FXML private Button encryptOutputButton;
  @FXML private Button decryptSourceButton;
  @FXML private Button decryptOutputButton;
  @FXML private Button addReceiverButton;
  @FXML private Button removeReceiverButton;
  @FXML private Button cancelEncryptionButton;
  @FXML private Button cancelDecryptionButton;

  // TextAreas
  @FXML private TextArea encryptInTextArea;
  @FXML private TextArea encryptOutTextArea;
  @FXML private TextArea decryptInTextArea;
  @FXML private TextArea decryptOutTextArea;
  @FXML private TextArea encryptionErrorsTextArea;
  @FXML private TextArea encryptionOutputTextArea;
  @FXML private TextArea decryptionErrorsTextArea;
  @FXML private TextArea decryptionOutputTextArea;

  // ProgressBars
  @FXML private ProgressBar encryptionProgressBar;
  @FXML private ProgressBar decryptionProgressBar;

  // Lists
  @FXML private ListView<User> encryptUsersListView;
  @FXML private ListView<User> decryptUsersListView;

  // PasswordFields
  @FXML private PasswordField passwordField;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeChoiceBoxes();
    initializeLists();
    initializeCredits();
  }

  public void chooseEncryptInputFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Wybierz plik do zaszyfrowania");
    encryptInputFile = fileChooser.showOpenDialog(stage);
    if (encryptInputFile != null) encryptInTextArea.setText(encryptInputFile.getPath());
  }

  public void chooseEncryptOutputFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Wybierz lokalizację zapisania wynikowego pliku");
    encryptOutputFile = fileChooser.showSaveDialog(stage);
    if (encryptOutTextArea != null) encryptOutTextArea.setText(encryptOutputFile.getPath());
  }

  public void addReceiver() {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Wybierz odbiorcę");
    chooser.setInitialDirectory(new File(PUBLIC_PATH));
    List<File> files = chooser.showOpenMultipleDialog(stage);
    if (files != null) {
      for (File file : files) {
        String newReceiverEmail = file.getName();
        if (isUserAlreadyOnList(newReceiverEmail, listReceiverEncrypt)) {
          addOutputMsg("Odbiorca już jest na liście.");
          return;
        }
        User user = new User(newReceiverEmail, UsersManager.loadPublicKey(newReceiverEmail));
        if (user.getPublicKey() == null) {
          showWarning("Nie udało się odczytać klucza publicznego dla odbiorcy: " + newReceiverEmail);
        } else {
          listReceiverEncrypt.add(user);
          addOutputMsg("Dodano odbiorców.");
        }
      }
    }
  }

  private boolean isUserAlreadyOnList(String userEmail, List<User> list) {
    for (User user : list) {
      if (user.getEmail().equals(userEmail)) {
        return true;
      }
    }
    return false;
  }

  public void removeReceiver(){
      ObservableList<User> selectedRecipients = this.encryptUsersListView.getSelectionModel().getSelectedItems();
      if (!selectedRecipients.isEmpty()) {
          this.listReceiverEncrypt.removeAll(selectedRecipients);
          addOutputMsg("Usunięto odbiorcę.");
      }
  }

  public void chooseDecryptInputFile() {}

  public void chooseDecryptOutputFile() {}

  public void openAddReceiverStage() {
    AddUserStage.display();
  }

  private void initializeChoiceBoxes() {
    modeChoiceBox.setItems(FXCollections.observableArrayList(ECB, CBC, CFB, OFB));
    modeChoiceBox.getSelectionModel().selectFirst();
    modeChoiceBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if ((newValue.equals(CFB)) || (newValue.equals(OFB))) {
                subblockChoiceBox.setDisable(false);
              } else {
                subblockChoiceBox.setDisable(true);
              }
            });
    subblockChoiceBox.setItems(FXCollections.observableArrayList(8, 16, 24, 32, 48));
    subblockChoiceBox.getSelectionModel().selectFirst();
    subblockChoiceBox.setDisable(true);
  }

  private void initializeLists() {
    encryptUsersListView.setItems(listReceiverEncrypt);
    decryptUsersListView.setItems(listReceiverDecrypt);
  }

  private void initializeCredits() {
    creatorsMenuItem.setOnAction(e -> About.display("Twórcy programu", "tworcy"));
    algorithmDescriptionMenuItem.setOnAction(e -> About.display("Opis Algorytmu", "opis"));
  }

  public void addErrorMsg(String text) {
    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    Date date = new Date();
    encryptionErrorsTextArea.appendText(dateFormat.format(date) + " " + text + "\n");
    decryptionErrorsTextArea.appendText(dateFormat.format(date) + " " + text + "\n");
  }

  public void showWarning(String text) {
    new AlertMessage(Alert.AlertType.WARNING, text);
    addErrorMsg(text);
  }

  public void addOutputMsg(String text) {
    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    Date date = new Date();
    encryptionOutputTextArea.appendText(dateFormat.format(date) + " " + text + "\n");
    decryptionOutputTextArea.appendText(dateFormat.format(date) + " " + text + "\n");
  }
}
