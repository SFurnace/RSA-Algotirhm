package app.view

import app.App
import javafx.application.Platform
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
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class FileController {
    @FXML
    private val encryptPane: Pane? = null
    @FXML
    private val decryptPane: Pane? = null
    @Volatile
    private var workThread: Thread? = null

    @FXML
    private fun dragOver(event: DragEvent) {
        if (event.gestureSource !== decryptPane && event.gestureSource !== encryptPane &&
                event.dragboard.files.size == 1 &&
                (workThread == null || !workThread!!.isAlive)) {
            event.acceptTransferModes(COPY)
        }
        event.consume()
    }

    private fun dragDetected(pane: Pane, event: MouseEvent) {
        val data = pane.userData as File
        if (data != null && data.exists()) {
            val dragboard = pane.startDragAndDrop(MOVE)

            val content = ClipboardContent()
            content.putFiles(List.of<File>(data))
            dragboard.setContent(content)
        }
        event.consume()
    }

    private fun dragDone(pane: Pane, event: DragEvent) {
        try {
            Thread.sleep(100)
            val data = pane.userData as File
            if (!Files.exists(data.toPath())) {
                pane.userData = null
                showDefault()
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } finally {
            event.consume()
        }
    }

    @FXML
    private fun dragDetectedE(event: MouseEvent) {
        dragDetected(encryptPane!!, event)
    }

    @FXML
    private fun dragDoneE(event: DragEvent) {
        dragDone(encryptPane!!, event)
    }

    @FXML
    private fun dragDroppedE(event: DragEvent) {
        val dragboard = event.dragboard
        val file = dragboard.files[0]

        synchronized(this) {
            if (workThread == null || !workThread!!.isAlive) {
                showEncryptingFile()

                workThread = Thread {
                    try {
                        DataInputStream(Files.newInputStream(file.toPath())).use { `in` ->
                            val bytes = `in`.readAllBytes()
                            if (App.getRsa().isPresent) {
                                val rsa = App.getRsa().get()
                                val encrypted = rsa.encryptObj(RSAEncryptedFile(file.name, bytes))

                                val path = Files.createTempFile("rsa", "encrypted")
                                DataOutputStream(Files.newOutputStream(path)).use { out -> out.write(encrypted) }
                                Platform.runLater { showEncryptedFile(path) }
                            } else {
                                throw NullPointerException("You have to set RSA Key file first!")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Platform.runLater {
                            showDefault()
                            Alert(ERROR, e.message).showAndWait()
                        }
                    }
                }

                workThread!!.isDaemon = true
                workThread!!.start()
            }
        }

        event.isDropCompleted = true
        event.consume()
    }

    @FXML
    private fun dragDetectedD(event: MouseEvent) {
        dragDetected(decryptPane!!, event)
    }

    @FXML
    private fun dragDoneD(event: DragEvent) {
        dragDone(decryptPane!!, event)
    }

    @FXML
    private fun dragDroppedD(event: DragEvent) {
        val dragboard = event.dragboard
        val file = dragboard.files[0]

        synchronized(this) {
            if (workThread == null || !workThread!!.isAlive) {
                showDecryptingFile()

                workThread = Thread {
                    try {
                        DataInputStream(Files.newInputStream(file.toPath())).use { `in` ->
                            val bytes = `in`.readAllBytes()
                            if (App.getRsa().isPresent) {
                                val rsa = App.getRsa().get()
                                val encryptedFile = rsa.decryptObj<RSAEncryptedFile>(bytes)

                                val path = Files.createTempFile(encryptedFile.fileName, "")
                                DataOutputStream(Files.newOutputStream(path)).use { out -> out.write(encryptedFile.encryptedContent) }
                                Platform.runLater { showDecryptedFile(path) }
                            } else {
                                throw NullPointerException("You have to set RSA Key file first!")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Platform.runLater {
                            showDefault()
                            Alert(ERROR, e.message).showAndWait()
                        }
                    }
                }

                workThread!!.isDaemon = true
                workThread!!.start()
            }
        }

        event.isDropCompleted = true
        event.consume()
    }

    private fun showDefault() {
        setPaneClass(encryptPane!!, CAN_DROP_CLASS)
        setPaneClass(decryptPane!!, CAN_DROP_CLASS)
    }

    private fun showEncryptingFile() {
        setPaneClass(encryptPane!!, PROCESSING_CLASS)
        setPaneClass(decryptPane!!, CANNOT_DROP_CLASS)
    }

    private fun showDecryptingFile() {
        setPaneClass(encryptPane!!, CANNOT_DROP_CLASS)
        setPaneClass(decryptPane!!, PROCESSING_CLASS)
    }

    private fun showEncryptedFile(path: Path) {
        decryptPane!!.userData = path.toFile()
        setPaneClass(encryptPane!!, SUCCESS_CLASS)
        setPaneClass(decryptPane, HAS_FILE_CLASS)
    }

    private fun showDecryptedFile(path: Path) {
        encryptPane!!.userData = path.toFile()
        setPaneClass(encryptPane, HAS_FILE_CLASS)
        setPaneClass(decryptPane!!, SUCCESS_CLASS)
    }

    companion object {
        internal var CANNOT_DROP_CLASS = "dropPane-cannot-drop"
        internal var SUCCESS_CLASS = "dropPane-success"
        internal var CAN_DROP_CLASS = "dropPane-can-drop"
        internal var PROCESSING_CLASS = "dropPane-processing"
        internal var HAS_FILE_CLASS = "dropPane-has-file"
        internal var WARNING_CLASS = "dropPane-warning"

        private val ALL_CLASS = Set.of<String>(CAN_DROP_CLASS, CANNOT_DROP_CLASS, SUCCESS_CLASS,
                PROCESSING_CLASS, HAS_FILE_CLASS, WARNING_CLASS)

        internal fun setPaneClass(pane: Pane, clazz: String) {
            pane.styleClass.removeAll(classesExcluded(clazz))
            pane.styleClass.add(clazz)
        }

        private fun classesExcluded(clazz: String): Collection<String> {
            return ALL_CLASS.stream().filter({ c -> c != clazz }).collect(Collectors.toList<String>())
        }
    }
}
