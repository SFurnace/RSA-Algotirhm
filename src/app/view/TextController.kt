package app.view

import app.App
import javafx.application.Platform.runLater
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.WARNING
import javafx.scene.control.TextArea
import java.util.*

class TextController {
    @FXML
    private lateinit var textInputE: TextArea
    @FXML
    private lateinit var textInputD: TextArea

    private var timerE: Timer? = null
    private var timerD: Timer? = null

    private fun clearTextAreaBColor() {
        textInputE.styleClass.clear()
        textInputD.styleClass.clear()
    }

    @FXML
    private fun startEncryptText() {
        clearTextAreaBColor()
        textInputD.text = ""
        timerE?.cancel()

        timerE = Timer(true)
        timerE!!.schedule(object : TimerTask() {
            override fun run() {
                encryptText()
            }
        }, TIMER_DELAY)
    }

    @FXML
    private fun startDecryptText() {
        clearTextAreaBColor()
        textInputE.text = ""
        timerD?.cancel()

        timerD = Timer(true)
        timerD!!.schedule(object : TimerTask() {
            override fun run() {
                decryptText()
            }
        }, TIMER_DELAY)
    }

    private fun encryptText() {
        App.rsa?.let {
            try {
                val encrypted = it.encryptObj(textInputE.text).contentToString()
                runLater {
                    textInputD.text = encrypted
                    textInputE.setStyleClass(T_SUCCESS)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runLater {
                    textInputE.setStyleClass(T_WARNING)
                }
            }
        } ?: run {
            runLater {
                Alert(WARNING, "Please choose a RSA key file.").showAndWait()
            }
        }
    }

    private fun decryptText() {
        App.rsa?.let {
            try {
                val bytes = textInputD.text
                        .split("\\[|]|\\s+|,".toRegex())
                        .filter { it.matches("-?\\d+".toRegex()) }
                        .map { it.toByte() }
                        .toByteArray()
                val decrypted = it.decryptObj<String>(bytes)

                runLater {
                    textInputE.text = decrypted
                    textInputD.styleClass.setAll("textarea-success")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runLater {
                    textInputD.styleClass.setAll("textarea-warning")
                }
            }
        } ?: run {
            Alert(WARNING, "Please choose a RSA key file.").showAndWait()
        }
    }

    companion object {
        private const val TIMER_DELAY = 1000L
    }
}
