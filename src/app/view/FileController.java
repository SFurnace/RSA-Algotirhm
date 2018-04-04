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
import rsa.RSAEncryptedFile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.input.TransferMode.COPY;
import static javafx.scene.input.TransferMode.MOVE;

public class FileController {
    static String CANNOT_DROP_CLASS = "dropPane-cannot-drop";
    static String SUCCESS_CLASS = "dropPane-success";
    static String CAN_DROP_CLASS = "dropPane-can-drop";
    static String PROCESSING_CLASS = "dropPane-processing";
    static String HAS_FILE_CLASS = "dropPane-has-file";
    static String WARNING_CLASS = "dropPane-warning";

    private static Set<String> ALL_CLASS = Set.of(CAN_DROP_CLASS, CANNOT_DROP_CLASS, SUCCESS_CLASS,
            PROCESSING_CLASS, HAS_FILE_CLASS, WARNING_CLASS);
    @FXML
    private Pane encryptPane;
    @FXML
    private Pane decryptPane;
    private volatile Thread workThread;

    static void setPaneClass(Pane pane, String clazz) {
        pane.getStyleClass().removeAll(classesExcluded(clazz));
        pane.getStyleClass().add(clazz);
    }

    private static Collection<String> classesExcluded(String clazz) {
        return ALL_CLASS.stream().filter(c -> !c.equals(clazz)).collect(Collectors.toList());
    }

    @FXML
    private void dragOver(DragEvent event) {
        if (event.getGestureSource() != decryptPane && event.getGestureSource() != encryptPane &&
                event.getDragboard().getFiles().size() == 1 &&
                (workThread == null || !workThread.isAlive())) {
            event.acceptTransferModes(COPY);
        }
        event.consume();
    }

    private void dragDetected(Pane pane, MouseEvent event) {
        File data = (File) pane.getUserData();
        if (data != null && data.exists()) {
            Dragboard dragboard = pane.startDragAndDrop(MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putFiles(List.of(data));
            dragboard.setContent(content);
        }
        event.consume();
    }

    private void dragDone(Pane pane, DragEvent event) {
        try {
            Thread.sleep(100);
            File data = (File) pane.getUserData();
            if (!Files.exists(data.toPath())) {
                pane.setUserData(null);
                showDefault();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            event.consume();
        }
    }

    @FXML
    private void dragDetectedE(MouseEvent event) {
        dragDetected(encryptPane, event);
    }

    @FXML
    private void dragDoneE(DragEvent event) {
        dragDone(encryptPane, event);
    }

    @FXML
    private void dragDroppedE(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        File file = dragboard.getFiles().get(0);

        synchronized (this) {
            if (workThread == null || !workThread.isAlive()) {
                showEncryptingFile();

                workThread = new Thread(() -> {
                    try (DataInputStream in = new DataInputStream(Files.newInputStream(file.toPath()))) {
                        byte[] bytes = in.readAllBytes();
                        if (App.getRsa().isPresent()) {
                            RSA rsa = App.getRsa().get();
                            byte[] encrypted = rsa.encryptObj(new RSAEncryptedFile(file.getName(), bytes));

                            Path path = Files.createTempFile("rsa", "encrypted");
                            try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(path))) {
                                out.write(encrypted);
                            }
                            Platform.runLater(() -> showEncryptedFile(path));
                        } else {
                            throw new NullPointerException("You have to set RSA Key file first!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            showDefault();
                            new Alert(ERROR, e.getMessage()).showAndWait();
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

    @FXML
    private void dragDetectedD(MouseEvent event) {
        dragDetected(decryptPane, event);
    }

    @FXML
    private void dragDoneD(DragEvent event) {
        dragDone(decryptPane, event);
    }

    @FXML
    private void dragDroppedD(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        File file = dragboard.getFiles().get(0);

        synchronized (this) {
            if (workThread == null || !workThread.isAlive()) {
                showDecryptingFile();

                workThread = new Thread(() -> {
                    try (DataInputStream in = new DataInputStream(Files.newInputStream(file.toPath()))) {
                        byte[] bytes = in.readAllBytes();
                        if (App.getRsa().isPresent()) {
                            RSA rsa = App.getRsa().get();
                            RSAEncryptedFile encryptedFile = rsa.decryptObj(bytes);

                            Path path = Files.createTempFile(encryptedFile.getFileName(), "");
                            try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(path))) {
                                out.write(encryptedFile.getEncryptedContent());
                            }
                            Platform.runLater(() -> showDecryptedFile(path));
                        } else {
                            throw new NullPointerException("You have to set RSA Key file first!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            showDefault();
                            new Alert(ERROR, e.getMessage()).showAndWait();
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

    private void showDefault() {
        setPaneClass(encryptPane, CAN_DROP_CLASS);
        setPaneClass(decryptPane, CAN_DROP_CLASS);
    }

    private void showEncryptingFile() {
        setPaneClass(encryptPane, PROCESSING_CLASS);
        setPaneClass(decryptPane, CANNOT_DROP_CLASS);
    }

    private void showDecryptingFile() {
        setPaneClass(encryptPane, CANNOT_DROP_CLASS);
        setPaneClass(decryptPane, PROCESSING_CLASS);
    }

    private void showEncryptedFile(Path path) {
        decryptPane.setUserData(path.toFile());
        setPaneClass(encryptPane, SUCCESS_CLASS);
        setPaneClass(decryptPane, HAS_FILE_CLASS);
    }

    private void showDecryptedFile(Path path) {
        encryptPane.setUserData(path.toFile());
        setPaneClass(encryptPane, HAS_FILE_CLASS);
        setPaneClass(decryptPane, SUCCESS_CLASS);
    }
}
