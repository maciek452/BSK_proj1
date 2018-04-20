package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private Stage stage;
    private Task encryptionTask, decryptionTask;
    private File encryptInputFile, encryptOutputFile, decryptInputFile, decryptOutputFile;

    //Lists
    public static final ObservableList<User> listReceiverEncrypt = FXCollections.observableArrayList();
    public static final ObservableList<User> listReceiverDecrypt = FXCollections.observableArrayList();
    public static final ObservableList<TextArea> textAreasPathsList = FXCollections.observableArrayList();

    //ChoiceBoxes
    @FXML
    private ChoiceBox modeChoiceBox;
    @FXML
    private ChoiceBox<Integer> subblockChoiceBox;

    //MenuItems
    @FXML
    private MenuItem creatorsMenuItem;
    @FXML
    private MenuItem algorithmDescriptionMenuItem;
    @FXML
    private MenuItem createRsaMenuItem;

    //Buttons
    @FXML
    private Button encryptButton;
    @FXML
    private Button decryptButton;
    @FXML
    private Button encryptSourceButton;
    @FXML
    private Button encryptOutputButton;
    @FXML
    private Button decryptSourceButton;
    @FXML
    private Button decryptOutputButton;
    @FXML
    private Button addReceiverButton;
    @FXML
    private Button removeReceiverButton;
    @FXML
    private Button cancelEncryptionButton;
    @FXML
    private Button cancelDecryptionButton;

    //TextAreas
    @FXML
    private TextArea encryptInTextArea;
    @FXML
    private TextArea encryptOutTextArea;
    @FXML
    private TextArea decryptInTextArea;
    @FXML
    private TextArea decryptOutTextArea;
    @FXML
    private TextArea encryptionErrorsTextArea;
    @FXML
    private TextArea encryptionOutputTextArea;
    @FXML
    private TextArea decryptionErrorsTextArea;
    @FXML
    private TextArea decryptionOutputTextArea;


    //ProgressBars
    @FXML
    private ProgressBar encryptionProgressBar;
    @FXML
    private ProgressBar decryptionProgressBar;

    //Lists
    @FXML
    private ListView<User> encryptUsersListView;
    @FXML
    private ListView<User> decryptUsersListView;

    //PasswordFields
    @FXML
    private PasswordField passwordField;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeChoiceBoxes();
        initializeCredits();

    }


    public void chooseEncryptInputFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        encryptInputFile = fileChooser.showOpenDialog(stage);
        encryptInTextArea.setText(encryptInputFile.getPath());
    }

    public void chooseEncryptOutputFile(){

    }
    public void chooseDecryptInputFile(){

    }
    public void chooseDecryptOutputFile(){

    }

    public void openAddReceiverStage(){
        AddReceiverStage.display();
    }



    private void initializeChoiceBoxes(){
        modeChoiceBox.setItems(FXCollections.observableArrayList("ECB", "CBC", "CFB", "OFB"));
        modeChoiceBox.getSelectionModel().selectFirst();
        modeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String cipherMode = newValue.toString();
            if ((cipherMode.equals("CFB")) || (cipherMode.equals("OFB"))) {
                subblockChoiceBox.setDisable(false);
            } else {
                subblockChoiceBox.setDisable(true);
            }
        });
        subblockChoiceBox.setItems(FXCollections.observableArrayList(new Integer[]{Integer.valueOf(8), Integer.valueOf(16), Integer.valueOf(24), Integer.valueOf(32), Integer.valueOf(48)}));
        subblockChoiceBox.getSelectionModel().selectFirst();
        subblockChoiceBox.setDisable(true);
    }

    private void initializeCredits(){
        creatorsMenuItem.setOnAction(e -> About.display("Twórca programu", "tworca"));
        algorithmDescriptionMenuItem.setOnAction(e -> About.display("Opis Algorytmu", "opis"));
    }
}
