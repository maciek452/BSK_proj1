package sample.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.About;
import sample.AddUserStage;
import sample.AlertMessage;
import sample.cipher.BlowfishEncryption;
import sample.cipher.FileHeader;
import sample.cipher.User;
import sample.cipher.UsersManager;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
    if (encryptOutputFile != null) encryptOutTextArea.setText(encryptOutputFile.getPath());
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
          showWarning(
              "Nie udało się odczytać klucza publicznego dla odbiorcy: " + newReceiverEmail);
        } else {
          listReceiverEncrypt.add(user);
          addOutputMsg("Dodano odbiorce.");
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

  public void removeReceiver() {
    ObservableList<User> selectedRecipients =
        this.encryptUsersListView.getSelectionModel().getSelectedItems();
    if (!selectedRecipients.isEmpty()) {
      this.listReceiverEncrypt.removeAll(selectedRecipients);
      addOutputMsg("Usunięto odbiorce");
    }
  }

  public void chooseDecryptInputFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Wybierz plik do odszyfrowania");
    decryptInputFile = fileChooser.showOpenDialog(stage);
    if (decryptInputFile != null) decryptInTextArea.setText(decryptInputFile.getPath());
      addAvailableReceiversToList(decryptInputFile);
  }

  public void chooseDecryptOutputFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Wybierz lokalizację zapisania wynikowego pliku");
    decryptOutputFile = fileChooser.showSaveDialog(stage);
    if (decryptOutTextArea != null) decryptOutTextArea.setText(decryptOutputFile.getPath());

  }

  public void addAvailableReceiversToList(File file) {
      listReceiverDecrypt.clear();
      InputStream inputStream = null;
      try {
          inputStream = Files.newInputStream(file.toPath());
      } catch (IOException e) {
          e.printStackTrace();
      }
      FileHeader fileHeader = new FileHeader(inputStream);
      listReceiverDecrypt.addAll(fileHeader.getApprovedUsers());
      decryptUsersListView.getSelectionModel().selectFirst();
  }

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

  public void showWarning(String text) {
    new AlertMessage(Alert.AlertType.WARNING, text);
    addOutputMsg(text);
  }

  public void addOutputMsg(String text) {
    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    Date date = new Date();
    encryptionOutputTextArea.appendText(dateFormat.format(date) + " " + text + "\n");
    decryptionOutputTextArea.appendText(dateFormat.format(date) + " " + text + "\n");
  }

  public void encrypt() {
    if ((encryptionTask == null) || (!encryptionTask.isRunning())) {
      if (areEncryptPathsEmpty()) return;
      if (isListOfReceiversEmpty()) return;

      FileHeader header =
          new FileHeader(
              modeChoiceBox.getSelectionModel().getSelectedItem().toString(),
              subblockChoiceBox.getSelectionModel().getSelectedItem(),
              listReceiverEncrypt);
      this.encryptionTask =
          BlowfishEncryption.encrypt(
              header,
              Paths.get(encryptInTextArea.getText()),
              Paths.get(encryptOutTextArea.getText()));
      encryptionProgressBar.progressProperty().unbind();
      encryptionProgressBar.progressProperty().bind(encryptionTask.progressProperty());
      this.encryptionTask
          .runningProperty()
          .addListener(
              (observable, oldValue, newValue) -> {
                if (oldValue.toString().equals("true") && newValue.toString().equals("false")) {
                  encryptionProgressBar.progressProperty().unbind();
                  encryptionProgressBar.setProgress(0.0D);
                  addOutputMsg("Zakończono szyfrowanie.");
                  if ((encryptionTask != null) && (encryptionTask.getException() != null)) {
                    new AlertMessage(
                        Alert.AlertType.WARNING,
                        encryptionTask.getException().getLocalizedMessage());
                  }
                }
              });
      addOutputMsg("Rozpoczęto szyfrowanie.");
      new Thread(encryptionTask).start();
    }
  }

  public void cancelEncryption() {
    if (encryptionTask != null && encryptionTask.isRunning()) {
      encryptionTask.cancel(true);
      encryptionProgressBar.progressProperty().unbind();
      encryptionProgressBar.setProgress(0.0D);
      addOutputMsg("Anulowano szyfrowanie.");
    }
  }

  private boolean areEncryptPathsEmpty() {
    if (encryptInTextArea.getText().isEmpty() || encryptOutTextArea.getText().isEmpty()) {
      showWarning("Uzupełnij ścieżki plików");
      return true;
    }
    return false;
  }

  private boolean isListOfReceiversEmpty() {
    if (listReceiverEncrypt.size() <= 0) {
      showWarning("Uzupełnij odbiorców");
      return true;
    }
    return false;
  }

  public void decrypt(){
      if ((decryptionTask == null) || (!decryptionTask.isRunning())) {

          if(areDecryptPathsEmpty()) return;
          this.decryptionTask = BlowfishEncryption.decrypt(decryptUsersListView.getSelectionModel().getSelectedItems().get(0), passwordField.getText(), decryptInTextArea.getText(), decryptOutTextArea.getText());
          decryptionProgressBar.progressProperty().unbind();
          decryptionProgressBar.progressProperty().bind(decryptionTask.progressProperty());
          this.decryptionTask.runningProperty().addListener((observable, oldValue, newValue) -> {
              if (oldValue.toString().equals("true") && newValue.toString().equals("false")) {
                  decryptionProgressBar.progressProperty().unbind();
                  decryptionProgressBar.setProgress(0.0D);
                  addOutputMsg("Zakończono deszyfrowanie.");
                  if ((decryptionTask != null) && (decryptionTask.getException() != null)) {
                      new Alert(Alert.AlertType.WARNING, decryptionTask.getException().getLocalizedMessage(), new ButtonType[0]).show();
                  }
              }
          });
          addOutputMsg("Rozpoczęto deszyfrowanie.");
          new Thread(decryptionTask).start();
      }
  }

    private boolean areDecryptPathsEmpty() {
        if (decryptInTextArea.getText().isEmpty() || decryptOutTextArea.getText().isEmpty()) {
            showWarning("Uzupełnij pola wyboru plików");
            return true;
        }
        return false;
    }
}
