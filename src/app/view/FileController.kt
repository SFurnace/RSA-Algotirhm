package app.view

import app.App
import javafx.application.Platform.runLater
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.ERROR
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode.COPY
import javafx.scene.input.TransferMode.MOVE
import javafx.scene.layout.Pane
import rsa.RSAEncryptedFile
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.lang.Thread.sleep
import java.nio.file.Files
import java.nio.file.Path
import kotlin.concurrent.thread

class FileController {
    @FXML
    private lateinit var encryptPane: Pane
    @FXML
    private lateinit var decryptPane: Pane
    @Volatile
    private var workThread: Thread = Thread()

    private fun dragDetected(pane: Pane, event: MouseEvent) {
        pane.userData?.let {
            it as File
            if (it.exists()) {
                val dragBoard = pane.startDragAndDrop(MOVE)
                val content = ClipboardContent()

                content.putFiles(listOf(it))
                dragBoard.setContent(content)
            }
        }
        event.consume()
    }

    @FXML
    private fun dragDetectedE(event: MouseEvent) {
        dragDetected(encryptPane, event)
    }

    @FXML
    private fun dragDetectedD(event: MouseEvent) {
        dragDetected(decryptPane, event)
    }

    private fun dragDone(pane: Pane, event: DragEvent) {
        try {
            sleep(DRAG_DONE_CHECK_DELAY)
            pane.userData?.let {
                it as File
                if (!Files.exists(it.toPath())) {
                    pane.userData = null
                    showDefault()
                }
            }
        } finally {
            event.consume()
        }
    }

    @FXML
    private fun dragDoneE(event: DragEvent) {
        dragDone(encryptPane, event)
    }

    @FXML
    private fun dragDoneD(event: DragEvent) {
        dragDone(decryptPane, event)
    }

    @FXML
    private fun dragOver(event: DragEvent) {
        if (event.gestureSource !== decryptPane &&
                event.gestureSource !== encryptPane &&
                event.dragboard.files.size == 1 &&
                !workThread.isAlive) {
            event.acceptTransferModes(COPY)
        }
        event.consume()
    }

    @FXML
    private fun dragDroppedE(event: DragEvent) {
        showEncryptingFile()
        val file = event.dragboard.files[0]

        synchronized(this) {
            workThread = thread(isDaemon = true, name = "encrypt file") {
                try {
                    DataInputStream(Files.newInputStream(file.toPath())).use { input ->
                        App.rsa?.let {
                            val encrypted = it.encryptObj(RSAEncryptedFile(file.name, input.readAllBytes()))
                            val path = Files.createTempFile("rsa", "encrypted")

                            DataOutputStream(Files.newOutputStream(path)).use { out ->
                                out.write(encrypted)
                            }
                            runLater { showEncryptedFile(path) }
                        } ?: run {
                            throw NullPointerException("You have to set RSA Key file first!")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runLater {
                        showDefault()
                        Alert(ERROR, e.message).showAndWait()
                    }
                }
            }
        }

        event.isDropCompleted = true
        event.consume()
    }

    @FXML
    private fun dragDroppedD(event: DragEvent) {
        showDecryptingFile()
        val file = event.dragboard.files[0]

        synchronized(this) {
            workThread = thread(isDaemon = true, name = "decrypted file") {
                try {
                    DataInputStream(Files.newInputStream(file.toPath())).use { input ->
                        App.rsa?.let {
                            val encryptedFile = it.decryptObj<RSAEncryptedFile>(input.readAllBytes())
                            val path = Files.createTempFile(encryptedFile.fileName, "")

                            DataOutputStream(Files.newOutputStream(path)).use { out ->
                                out.write(encryptedFile.encryptedContent)
                            }
                            runLater { showDecryptedFile(path) }
                        } ?: run {
                            throw NullPointerException("You have to set RSA Key file first!")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runLater {
                        showDefault()
                        Alert(ERROR, e.message).showAndWait()
                    }
                }
            }
        }

        event.isDropCompleted = true
        event.consume()
    }

    private fun showDefault() {
        encryptPane.setStyleClass(P_ENABLE)
        decryptPane.setStyleClass(P_ENABLE)
    }

    private fun showEncryptingFile() {
        encryptPane.setStyleClass(P_PROCESSING)
        decryptPane.setStyleClass(P_DISABLE)
    }

    private fun showDecryptingFile() {
        encryptPane.setStyleClass(P_DISABLE)
        decryptPane.setStyleClass(P_PROCESSING)
    }

    private fun showEncryptedFile(path: Path) {
        decryptPane.userData = path.toFile()
        encryptPane.setStyleClass(P_SUCCESS)
        decryptPane.setStyleClass(P_MARKED)
    }

    private fun showDecryptedFile(path: Path) {
        encryptPane.userData = path.toFile()
        encryptPane.setStyleClass(P_MARKED)
        decryptPane.setStyleClass(P_SUCCESS)
    }

    companion object {
        private const val DRAG_DONE_CHECK_DELAY = 100L
    }
}
