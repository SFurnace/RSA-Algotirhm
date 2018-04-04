package app.view;

import app.App;
import app.Config;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;

class KeyAlert extends Alert {
    static ButtonType GENERATE;
    static ButtonType CHOOSE;
    private static ButtonType CANCEL;

    static {
        GENERATE = new ButtonType("Generate key files");
        CHOOSE = new ButtonType("Choose a key file");
        CANCEL = new ButtonType("Cancel", CANCEL_CLOSE);
    }

    KeyAlert() {
        super(AlertType.CONFIRMATION);
        this.setTitle("Choose an action");
        this.getButtonTypes().setAll(CANCEL, CHOOSE, GENERATE);

        App.getRsa().ifPresentOrElse(
                rsa -> {
                    this.setHeaderText("Current RSA Key file is: " + Config.getLastRSAKeyPath());
                    this.setContentText("Modulus: " + rsa.getKey().getModulus() + "\nPower: " + rsa.getKey().getPower());
                },
                () -> this.setHeaderText("RSA Key is not set.")
        );
    }
}
