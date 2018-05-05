package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.cipher.UsersRegistrar;

public class AddUserStage {
  private static Stage stage;
  private static Label label1, label2;
  private static TextField emailTextField, passTextField;
  private static TextField[] texFields;
  private static Button closeButton, addButton;

  public static void display() {
    prepareStage();
    pinComponents();
  }

  private static void prepareStage() {
    createStage();
    initLabels();
    initTextFields();
    initButtons();
  }

  private static void createStage() {
    stage =
        new Stage() {
          {
            initModality(Modality.APPLICATION_MODAL);
            setTitle("Dodawanie nowego klucza");
            setMinWidth(500);
            setMaxWidth(1000);
            setMinHeight(200);
          }
        };
  }

  private static void initLabels() {
    label1 = new Label("e-mail:");
    label2 = new Label("hasło:");
  }

  private static void initTextFields() {
    emailTextField =
        new TextField() {
          {
            setMinWidth(100);
            setMaxWidth(300);
          }
        };
    passTextField =
        new PasswordField() {
          {
            setMinWidth(100);
            setMaxWidth(300);
          }
        };
  }

  private static void initButtons() {
    closeButton =
        new Button("Zamknij") {
          {
            setOnAction(e -> stage.close());
          }
        };
    addButton =
        new Button("Dodaj") {
          {
            setOnAction(
                e -> {
                  int result =
                      UsersRegistrar.createRsaForUser(emailTextField.getText(), passTextField.getText());
                  if (result == 0)
                    new Alert(Alert.AlertType.INFORMATION, "Poprawnie utworzono", new ButtonType[0])
                        .show();
                  else
                    new Alert(
                            Alert.AlertType.ERROR,
                            "Nie udało się dodać kluczaRSA",
                            new ButtonType[0])
                        .show();
                });
          }
        };
  }

  private static void pinComponents() {
    VBox layout =
        new VBox(10) {
          {
            getChildren()
                .addAll(label1, emailTextField, label2, passTextField, addButton, closeButton);
            setAlignment(Pos.CENTER);
          }
        };
    Scene scene = new Scene(layout);
    stage.setScene(scene);
    stage.showAndWait();
  }
}
