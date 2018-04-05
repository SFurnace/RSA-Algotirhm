package app.view;

import app.App;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import rsa.RSA;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static app.view.FileController.*;
import static javafx.scene.control.Alert.AlertType.WARNING;
import static javafx.scene.input.TransferMode.COPY;

public class IdentityController {
    private String ID = "Hello World!";

    @FXML
    private Pane identityFilePane;
    @FXML
    private Pane verifyPane;

    private File identityFile;
    private Thread workThread;

    public void initialize() {
        App.getRsa().ifPresentOrElse(
                rsa -> {
                    try {
                        Path path = Files.createTempFile("", "");
                        try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(path))) {
                            out.write(rsa.encryptObj(ID));
                        }
                        identityFile = path.toFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                        new Alert(WARNING, "Cannot generate a identity file.").showAndWait();
                        MainController.changeToFunctionsPane();
                    }
                },
                () -> System.exit(-1)
        );
    }

    @FXML
    private void identityFilePaneDragDetected(MouseEvent event) {
        Dragboard dragboard = identityFilePane.startDragAndDrop(COPY);

        ClipboardContent content = new ClipboardContent();
        content.putFiles(List.of(identityFile));
        dragboard.setContent(content);

        event.consume();
    }

    @FXML
    private void identityFilePaneDragDone(DragEvent event) {
        event.consume();
    }

    @FXML
    private void verifyPaneDragOver(DragEvent event) {
        if ((workThread == null || !workThread.isAlive()) && event.getDragboard().getFiles().size() == 1) {
            event.acceptTransferModes(COPY);
        }
        event.consume();
    }

    @FXML
    void verifyPaneDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        File file = dragboard.getFiles().get(0);

        synchronized (this) {
            if (workThread == null || !workThread.isAlive()) {
                setPaneClass(verifyPane, PROCESSING_CLASS);

                workThread = new Thread(() -> {
                    try (DataInputStream in = new DataInputStream(Files.newInputStream(file.toPath()))) {
                        byte[] bytes = in.readAllBytes();
                        if (App.getRsa().isPresent()) {
                            RSA rsa = App.getRsa().get();
                            if (ID.equals(rsa.decryptObj(bytes))) {
                                Platform.runLater(() -> setPaneClass(verifyPane, SUCCESS_CLASS));
                            } else {
                                Platform.runLater(() -> setPaneClass(verifyPane, WARNING_CLASS));
                            }
                        } else {
                            throw new NullPointerException("You have to set RSA Key file first!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            setPaneClass(verifyPane, WARNING_CLASS);
                        });
                    }
                });

                workThread.setDaemon(true);
                workThread.start();
            }
        }

        event.setDropCompleted(true);
        event.consume();
    }
}
