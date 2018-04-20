package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddReceiverStage {
    private static Stage window;
    private static Label label1, label2;
    private static TextField nameTextField, passTextField;
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
        window = new Stage() {{
            initModality(Modality.APPLICATION_MODAL);
            setTitle("Dodawanie nowego klucza");
            setMinWidth(500);
            setMaxWidth(1000);
            setMinHeight(200);
        }};
    }

    private static void initLabels() {
        label1 = new Label("Nazwa użytkownika");
        label2 = new Label("Hasło");
    }

    private static void initTextFields() {
        nameTextField = new TextField() {{
            setMinWidth(100);
            setMaxWidth(300);
        }};
        passTextField = new PasswordField() {{
            setMinWidth(100);
            setMaxWidth(300);
        }};
        texFields = new TextField[2];
        texFields[0] = nameTextField;
        texFields[1] = passTextField;
    }

    private static void initButtons() {
        closeButton = new Button("Zamknij okno") {{
            setOnAction(e -> window.close());
        }};
        addButton = new Button("Dodaj") {{
            setOnAction(e -> {
                int result = RSA.createRsaForUser(nameTextField.getText(), passTextField.getText());
                if (result == 0)
                    new Alert(Alert.AlertType.INFORMATION, "Poprawnie utworzono", new ButtonType[0]).show();
                else
                    new Alert(Alert.AlertType.ERROR, "Nie udało się dodać kluczaRSA", new ButtonType[0]).show();
            });
        }};
    }

    private static void pinComponents() {
        VBox layout = new VBox(10) {{
            getChildren().addAll(label1, nameTextField, label2, passTextField, addButton, closeButton);
            setAlignment(Pos.CENTER);
        }};
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

}
