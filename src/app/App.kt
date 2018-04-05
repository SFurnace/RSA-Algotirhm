package app

import javafx.application.Application
import javafx.fxml.FXMLLoader.load
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.ERROR
import javafx.stage.Stage
import rsa.RSA
import rsa.RSAKey
import rsa.RSAKeyPair
import java.io.*
import java.nio.file.Files
import java.nio.file.Path

class App : Application() {
    override fun start(primaryStage: Stage) {
        App.primaryStage = primaryStage

        AppConfig.lastRSAKeyPath?.let {
            if (it.toFile().exists()) {
                App.setRSAFromFile(it)
            }
        }

        primaryStage.title = "RSA"
        primaryStage.scene = Scene(load<Parent>(javaClass.getResource("view/fxml/MainPane.fxml")))
        primaryStage.isResizable = false
        primaryStage.show()
    }

    companion object {
        lateinit var primaryStage: Stage
        var rsa: RSA? = null

        const val ID_STRING = "Hello World!"
        var identityFile: File? = null

        fun setRSAFromFile(keyFilePath: Path) {
            try {
                AppConfig.lastRSAKeyPath = keyFilePath
                ObjectInputStream(Files.newInputStream(keyFilePath)).use { input ->
                    rsa = RSA(input.readObject() as RSAKey)
                    identityFile = Files.createTempFile("", "").also {
                        DataOutputStream(Files.newOutputStream(it)).use { out ->
                            out.write(rsa!!.encryptObj(Companion.ID_STRING))
                        }
                    }.toFile()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Alert(ERROR, e.message).showAndWait()
            }

        }

        fun generateKeyFiles(pubPath: Path, priPath: Path) {
            val rsaKeyPair = RSAKeyPair.generateKeyPair()
            try {
                ObjectOutputStream(Files.newOutputStream(pubPath)).use { it.writeObject(rsaKeyPair.publicKey) }
                ObjectOutputStream(Files.newOutputStream(priPath)).use { it.writeObject(rsaKeyPair.privateKey) }
            } catch (e: IOException) {
                e.printStackTrace()
                Alert(ERROR, e.message).showAndWait()
            }

        }
    }
}

fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}
