package app.view

import app.App
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.WARNING
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode.COPY
import javafx.scene.layout.Pane
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.nio.file.Files

class IdentityController {
    private val ID = "Hello World!"

    @FXML
    private val identityFilePane: Pane? = null
    @FXML
    private val verifyPane: Pane? = null

    private var identityFile: File? = null
    private var workThread: Thread? = null

    fun initialize() {
        App.getRsa().ifPresentOrElse(
                { rsa ->
                    try {
                        val path = Files.createTempFile("", "")
                        DataOutputStream(Files.newOutputStream(path)).use { out -> out.write(rsa.encryptObj(ID)) }
                        identityFile = path.toFile()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Alert(WARNING, "Cannot generate a identity file.").showAndWait()
                        MainController.changeToFunctionsPane()
                    }
                }
        ) { System.exit(-1) }
    }

    @FXML
    private fun identityFilePaneDragDetected(event: MouseEvent) {
        val dragboard = identityFilePane!!.startDragAndDrop(COPY)

        val content = ClipboardContent()
        content.putFiles(List.of<File>(identityFile!!))
        dragboard.setContent(content)

        event.consume()
    }

    @FXML
    private fun identityFilePaneDragDone(event: DragEvent) {
        event.consume()
    }

    @FXML
    private fun verifyPaneDragOver(event: DragEvent) {
        if ((workThread == null || !workThread!!.isAlive) && event.dragboard.files.size == 1) {
            event.acceptTransferModes(COPY)
        }
        event.consume()
    }

    @FXML
    internal fun verifyPaneDragDropped(event: DragEvent) {
        val dragboard = event.dragboard
        val file = dragboard.files[0]

        synchronized(this) {
            if (workThread == null || !workThread!!.isAlive) {
                setPaneClass(verifyPane!!, PROCESSING_CLASS)

                workThread = Thread {
                    try {
                        DataInputStream(Files.newInputStream(file.toPath())).use { `in` ->
                            val bytes = `in`.readAllBytes()
                            if (App.getRsa().isPresent) {
                                val rsa = App.getRsa().get()
                                if (ID == rsa.decryptObj<Serializable>(bytes)) {
                                    Platform.runLater { setPaneClass(verifyPane, SUCCESS_CLASS) }
                                } else {
                                    Platform.runLater { setPaneClass(verifyPane, WARNING_CLASS) }
                                }
                            } else {
                                throw NullPointerException("You have to set RSA Key file first!")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Platform.runLater { setPaneClass(verifyPane, WARNING_CLASS) }
                    }
                }

                workThread!!.isDaemon = true
                workThread!!.start()
            }
        }

        event.isDropCompleted = true
        event.consume()
    }
}
