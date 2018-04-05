package app.view

import app.App
import app.Config
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.ButtonType

internal class KeyAlert : Alert(Alert.AlertType.CONFIRMATION) {
    init {
        this.title = "Choose an action"
        this.buttonTypes.setAll(CANCEL, CHOOSE, GENERATE)

        App.getRsa().ifPresentOrElse(
                { rsa ->
                    this.headerText = "Current RSA Key file is: " + Config.lastRSAKeyPath
                    this.contentText = "Modulus: " + rsa.key.modulus + "\nPower: " + rsa.key.power
                }
        ) { this.headerText = "RSA Key is not set." }
    }

    companion object {
        var GENERATE: ButtonType
        var CHOOSE: ButtonType
        private var CANCEL: ButtonType? = null

        init {
            GENERATE = ButtonType("Generate key files")
            CHOOSE = ButtonType("Choose a key file")
            CANCEL = ButtonType("Cancel", CANCEL_CLOSE)
        }
    }
}
