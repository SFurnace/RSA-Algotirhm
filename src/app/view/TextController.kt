package app.view

import app.App
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.WARNING
import javafx.scene.control.TextArea
import java.util.*

class TextController {
    @FXML
    private val textInputE: TextArea? = null
    @FXML
    private val textInputD: TextArea? = null

    private var timerE: Timer? = null
    private var timerD: Timer? = null

    @FXML
    @Synchronized
    private fun startEncryptText() {
        clearTextAreaBColor()
        textInputD!!.text = ""

        if (timerE != null) {
            timerE!!.cancel()
        }
        timerE = Timer(true)

        val timerTaskE = object : TimerTask() {
            override fun run() {
                encryptText()
            }
        }
        timerE!!.schedule(timerTaskE, DELAY.toLong())
    }

    private fun encryptText() {
        App.getRsa().ifPresentOrElse(
                { rsa ->
                    Platform.runLater {
                        try {
                            textInputD!!.text = Arrays.toString(rsa.encryptObj(textInputE!!.text))
                            textInputE.styleClass.setAll("textarea-success")
                        } catch (e: Exception) {
                            e.printStackTrace()
                            textInputE!!.styleClass.setAll("textarea-warning")
                        }
                    }
                }
        ) { Platform.runLater { Alert(WARNING, "Please choose a RSA key file.").showAndWait() } }
    }

    @FXML
    @Synchronized
    private fun startDecryptText() {
        clearTextAreaBColor()
        textInputE!!.text = ""

        if (timerD != null) {
            timerD!!.cancel()
        }
        timerD = Timer(true)

        val timerTaskD = object : TimerTask() {
            override fun run() {
                decryptText()
            }
        }
        timerD!!.schedule(timerTaskD, DELAY.toLong())
    }

    private fun decryptText() {
        App.getRsa().ifPresentOrElse(
                { rsa ->
                    val integers = Arrays.stream<String>(textInputD!!.text.split("\\[|]|\\s+|,".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray())
                            .filter { s -> s.matches("-?\\d+".toRegex()) }
                            .mapToInt(ToIntFunction<String> { Integer.valueOf(it) })
                            .toArray()
                    val bytes = ByteArray(integers.size)
                    for (i in integers.indices) {
                        bytes[i] = integers[i].toByte()
                    }

                    Platform.runLater {
                        try {
                            textInputE!!.text = rsa.decryptObj(bytes)
                            textInputD.styleClass.setAll("textarea-success")
                        } catch (e: Exception) {
                            e.printStackTrace()
                            textInputD.styleClass.setAll("textarea-warning")
                        }
                    }
                }
        ) { Platform.runLater { Alert(WARNING, "Please choose a RSA key file.").showAndWait() } }
    }

    private fun clearTextAreaBColor() {
        textInputE!!.styleClass.clear()
        textInputD!!.styleClass.clear()
    }

    companion object {
        private val DELAY = 1000
    }
}
