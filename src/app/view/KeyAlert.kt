package app.view

import app.App
import app.AppConfig
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.ButtonType

internal class KeyAlert : Alert(Alert.AlertType.CONFIRMATION) {
    init {
        this.title = "Choose an action"
        this.buttonTypes.setAll(CANCEL, CHOOSE, GENERATE)

        App.rsa?.let {
            this.headerText = "Current RSA Key file is: ${AppConfig.lastRSAKeyPath}"
            this.contentText = "Modulus: ${it.key.modulus}\nPower: ${it.key.power}"
        } ?: let {
            this.headerText = "RSA Key is not set."
        }
    }

    companion object {
        val GENERATE: ButtonType = ButtonType("Generate key files")
        val CHOOSE: ButtonType = ButtonType("Choose a key file")
        val CANCEL: ButtonType = ButtonType("Cancel", CANCEL_CLOSE)
    }
}
