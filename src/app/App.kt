package app

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.ERROR
import javafx.stage.Stage
import org.jetbrains.annotations.Contract
import rsa.RSA
import rsa.RSAKey
import rsa.RSAKeyPair
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class App : Application() {

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        App.primaryStage = primaryStage

        Config.lastRSAKeyPath.ifPresent({ p ->
            if (p.toFile().exists()) {
                App.setRSA(p)
            }
        })

        val root = FXMLLoader.load<Parent>(javaClass.getResource("view/MainPane.fxml"))
        primaryStage.title = "RSA"
        primaryStage.scene = Scene(root)
        primaryStage.isResizable = false
        primaryStage.show()
    }

    companion object {
        private var rsa: RSA? = null
        @get:Contract(pure = true)
        var primaryStage: Stage? = null
            private set

        fun getRsa(): Optional<RSA> {
            return Optional.ofNullable(rsa)
        }

        fun setRSA(keyFilePath: Path) {
            Config.setLastRSAKeyPath(keyFilePath)
            try {
                ObjectInputStream(Files.newInputStream(keyFilePath)).use { `in` -> App.rsa = RSA(`in`.readObject() as RSAKey) }
            } catch (e: Exception) {
                e.printStackTrace()
                Alert(ERROR, e.message).showAndWait()
            }

        }

        fun generateKeyFiles(pubPath: Path, priPath: Path) {
            val rsaKeyPair = RSAKeyPair.generateKeyPair()
            try {
                ObjectOutputStream(Files.newOutputStream(pubPath)).use { out -> out.writeObject(rsaKeyPair.publicKey) }
                ObjectOutputStream(Files.newOutputStream(priPath)).use { out -> out.writeObject(rsaKeyPair.privateKey) }
            } catch (e: IOException) {
                e.printStackTrace()
                Alert(ERROR, e.message).showAndWait()
            }

        }

        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(*args)
        }
    }
}
