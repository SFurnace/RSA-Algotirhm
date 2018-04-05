package app.view;

import app.App;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.scene.control.Alert.AlertType.WARNING;

public class TextController {
    private static final int DELAY = 1000;
    @FXML
    private TextArea textInputE;
    @FXML
    private TextArea textInputD;

    private Timer timerE;
    private Timer timerD;

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
        timerE.schedule(timerTaskE, DELAY);
    }

    private void encryptText() {
        App.getRsa().ifPresentOrElse(
                rsa -> Platform.runLater(() -> {
                    try {
                        textInputD.setText(Arrays.toString(rsa.encryptObj(textInputE.getText())));
                        textInputE.getStyleClass().setAll("textarea-success");
                    } catch (Exception e) {
                        e.printStackTrace();
                        textInputE.getStyleClass().setAll("textarea-warning");
                    }
                }),
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
        timerD.schedule(timerTaskD, DELAY);
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

                    Platform.runLater(() -> {
                        try {
                            textInputE.setText(rsa.decryptObj(bytes));
                            textInputD.getStyleClass().setAll("textarea-success");
                        } catch (Exception e) {
                            e.printStackTrace();
                            textInputD.getStyleClass().setAll("textarea-warning");
                        }
                    });
                },
                () -> Platform.runLater(
                        () -> new Alert(WARNING, "Please choose a RSA key file.").showAndWait()
                )
        );
    }

    private void clearTextAreaBColor() {
        textInputE.getStyleClass().clear();
        textInputD.getStyleClass().clear();
    }
}
