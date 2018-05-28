package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class About {
  static String authors = "Maciej Nawrocki\nFilip Plombon";
  static String algorithmDescription =
      "\t\tBlowfish - szyfr blokowy stworzony przez Bruce'a Schneiera w 1993 roku \n"
          + "\tjako szybka i bezpłatna alternatywa dla istniejących ówcześnie algorytmów.\t\t\n\n"
          + "\t\tAlgorytm operuje na 64-bitowych blokach i używa kluczy od 32 do 448 bitów.\n"
          + "\tMa on postać szyfru Feistela z 16. rundami z SBOX-ami zależnymi od klucza.\n"
          + "\tKażda zmiana klucza wymaga wielu wstępnych obliczeń w celu ustalenia SBOX-ów.\n"
          + "\tZ tego powodu atak brute-force trwa znacznie dłużej, niż można byłoby się spodziewać.\t\t";

  public static void display(String title, String message) {
    Stage window = new Stage();
    window.initModality(Modality.APPLICATION_MODAL);
    window.setTitle(title);
    window.setMinWidth(500);
    window.setMaxWidth(1000);
    window.setMinHeight(200);
    Label label = new Label();
    if (message.equals("opis")) label.setText(algorithmDescription);
    else label.setText(authors);
    Button closeButton = new Button("Zamknij okno");
    closeButton.setOnAction(e -> window.close());
    VBox layout = new VBox(10);
    layout.getChildren().addAll(label, closeButton);
    layout.setAlignment(Pos.CENTER);
    Scene scene = new Scene(layout);
    window.setScene(scene);
    window.showAndWait();
  }
}
