package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertMessage {

  public AlertMessage(Alert.AlertType type, String text) {
    new Alert(type, text, new ButtonType[0]).show();
  }
}
