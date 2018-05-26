package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Security.addProvider(new BouncyCastleProvider());
    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
    primaryStage.setTitle("Blowfish");
    primaryStage.setScene(new Scene(root, 600, 600));
    primaryStage.show();
  }
}
