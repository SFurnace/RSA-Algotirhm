package app.view;

import app.App;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.scene.control.Alert.AlertType.WARNING;

public class TextController {
    @FXML
    private TextArea textInputE;
    @FXML
    private TextArea textInputD;

    private Timer timerE;
    private Timer timerD;

    private static String arrayToString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            builder.append(bytes[i]);
            builder.append(',');
            if (i % 60 == 59) {
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    @FXML
    private synchronized void startEncryptText() {
        clearTextAreaBColor();
        textInputD.setText("");

        if (timerE != null) {
            timerE.cancel();
        }
        timerE = new Timer(true);

        TimerTask timerTaskE = new TimerTask() {
            @Override
            public void run() {
                encryptText();
            }
        };
        timerE.schedule(timerTaskE, 1000);
    }

    private void encryptText() {
        App.getRsa().ifPresentOrElse(
                rsa -> {
                    try {
                        textInputD.setText(arrayToString(rsa.encryptObj(textInputE.getText())));
                        textInputE.setStyle("text-area-background: rgba(188,255,209,0.5)");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Platform.runLater(() -> textInputE.setStyle("text-area-background: rgba(255,181,174,0.51)"));
                    }
                },
                () -> Platform.runLater(
                        () -> new Alert(WARNING, "Please choose a RSA key file.").showAndWait()
                )
        );
    }

    @FXML
    private synchronized void startDecryptText() {
        clearTextAreaBColor();
        textInputE.setText("");

        if (timerD != null) {
            timerD.cancel();
        }
        timerD = new Timer(true);

        TimerTask timerTaskD = new TimerTask() {
            @Override
            public void run() {
                decryptText();
            }
        };
        timerD.schedule(timerTaskD, 1000);
    }

    private void decryptText() {
        App.getRsa().ifPresentOrElse(
                rsa -> {
                    int[] integers = Arrays.stream(textInputD.getText().split("\\[|]|\\s+|,"))
                            .filter(s -> s.matches("-?\\d+"))
                            .mapToInt(Integer::valueOf)
                            .toArray();
                    byte[] bytes = new byte[integers.length];
                    for (int i = 0; i < integers.length; i++) {
                        bytes[i] = (byte) integers[i];
                    }

                    try {
                        textInputE.setText(rsa.decryptObj(bytes));
                        textInputD.setStyle("text-area-background: rgba(188,255,209,0.51)");
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        Platform.runLater(() -> textInputD.setStyle("text-area-background: rgba(255,181,174,0.5)"));
                    }
                },
                () -> Platform.runLater(
                        () -> new Alert(WARNING, "Please choose a RSA key file.").showAndWait()
                )
        );
    }

    // todo: 将代码中的CSS部分写到css文件里面。
    private void clearTextAreaBColor() {
        textInputE.setStyle("-fx-background-color: #ffffff");
        textInputD.setStyle("-fx-background-color: #ffffff");
    }
}
