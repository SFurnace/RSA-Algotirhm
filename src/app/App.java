package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import rsa.RSA;
import rsa.RSAKey;
import rsa.RSAKeyPair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.ERROR;

public class App extends Application {
    private static RSA rsa;
    private static Stage primaryStage;

    public static Optional<RSA> getRsa() {
        return Optional.ofNullable(rsa);
    }

    public static void setRSA(@NotNull Path keyFilePath) {
        Config.setLastRSAKeyPath(keyFilePath);
        try {
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(keyFilePath))) {
                App.rsa = new RSA((RSAKey) in.readObject());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    @Contract(pure = true)
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void generateKeyFiles(@NotNull Path pubPath, @NotNull Path priPath) {
        RSAKeyPair rsaKeyPair = RSAKeyPair.generateKeyPair();
        try {
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(pubPath))) {
                out.writeObject(rsaKeyPair.getPublicKey());
            }
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(priPath))) {
                out.writeObject(rsaKeyPair.getPrivateKey());
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        App.primaryStage = primaryStage;

        Config.getLastRSAKeyPath().ifPresent(p -> {
            if (p.toFile().exists()) {
                App.setRSA(p);
            }
        });

        Parent root = FXMLLoader.load(getClass().getResource("view/main.fxml"));
        primaryStage.setTitle("RSA");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
