package app.view

import app.App
import javafx.application.Platform.runLater
import javafx.fxml.FXML
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode.COPY
import javafx.scene.layout.Pane
import java.io.DataInputStream
import java.nio.file.Files
import kotlin.concurrent.thread

class IdentityController {
    @FXML
    private lateinit var identityFilePane: Pane
    @FXML
    private lateinit var verifyPane: Pane
    @Volatile
    private var workThread: Thread = Thread()

    @FXML
    private fun identityFilePaneDragDetected(event: MouseEvent) {
        App.identityFile?.let {
            val dragBoard = identityFilePane.startDragAndDrop(COPY)
            val content = ClipboardContent()

            content.putFiles(listOf(it))
            dragBoard.setContent(content)
        }
        event.consume()
    }

    @FXML
    private fun identityFilePaneDragDone(event: DragEvent) {
        event.consume()
    }

    @FXML
    private fun verifyPaneDragOver(event: DragEvent) {
        if (!workThread.isAlive && event.dragboard.files.size == 1) {
            event.acceptTransferModes(COPY)
        }
        event.consume()
    }

    @FXML
    private fun verifyPaneDragDropped(event: DragEvent) {
        verifyPane.setStyleClass(P_PROCESSING)
        val file = event.dragboard.files[0]

        synchronized(this) {
            workThread = thread(isDaemon = true, name = "verify id file") {
                try {
                    DataInputStream(Files.newInputStream(file.toPath())).use { input ->
                        App.rsa?.let {
                            if (App.ID_STRING == it.decryptObj(input.readAllBytes())) {
                                runLater { verifyPane.setStyleClass(P_SUCCESS) }
                            } else {
                                runLater { verifyPane.setStyleClass(P_WARNING) }
                            }
                        } ?: run {
                            throw NullPointerException("You have to set RSA Key file first!")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runLater { verifyPane.setStyleClass(P_WARNING) }
                }
            }
        }

        event.isDropCompleted = true
        event.consume()
    }
}
