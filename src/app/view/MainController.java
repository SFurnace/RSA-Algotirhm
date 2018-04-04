package app.view;

import app.App;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

import static javafx.scene.control.Alert.AlertType.ERROR;

public class MainController {
    private static MainController mainController;

    @FXML
    private Pane mainPane;
    @FXML
    private Button returnButton;
    @FXML
    private Circle returnCircle;
    @FXML
    private Circle keyCircle;
    @FXML
    private Button keyButton;

    private Node functionsPane;

    private Node textPane;
    private TextController textController;

    private Node identityPane;

    private Node filePane;

    {
        try {
            functionsPane = FXMLLoader.load(getClass().getResource("FunctionsPane.fxml"));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("TextPane.fxml"));
            textPane = loader.load();
            textController = loader.getController();

            identityPane = FXMLLoader.load(getClass().getResource("IdentityPane.fxml"));
            filePane = FXMLLoader.load(getClass().getResource("FilePane.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(ERROR, e.getMessage()).showAndWait();
            System.exit(-1);
        }
    }

    static MainController getMainController() {
        return mainController;
    }

    @FXML
    public void clickKeyBtn() {
        KeyAlert alert = new KeyAlert();
        alert.showAndWait().ifPresent(r -> {
            if (r.equals(KeyAlert.CHOOSE)) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Choose RSA Key File");
                chooser.getExtensionFilters().setAll(new ExtensionFilter("RSA Key File", "*.pub", "*.pri"));

                File file = chooser.showOpenDialog(App.getPrimaryStage());
                if (file != null) {
                    App.setRSA(file.toPath());
                    changeKeyBtnBackground();
                }
            } else if (r.equals(KeyAlert.GENERATE)) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Generate RSA Key Files");
                chooser.getExtensionFilters().setAll(new ExtensionFilter("RSA Key File", "*.pub"));
                chooser.setInitialFileName("key.pub");

                File file1 = chooser.showSaveDialog(App.getPrimaryStage());
                if (file1 != null) {
                    File file2 = file1.toPath().resolveSibling(file1.getName().replaceAll("pub$", "pri")).toFile();
                    App.generateKeyFiles(file1.toPath(), file2.toPath());
                }
            }
        });
    }

    @FXML
    public void clickReturnBtn() {
        changeToFunctionsPane();
    }

    public void initialize() {
        MainController.mainController = this;

        changeKeyBtnBackground();
        changeToFunctionsPane();
    }

    private void changePane(Node pane) {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(pane);

        Timeline timeline = new Timeline();
        Duration duration = Duration.seconds(0.5);

        KeyFrame paneStart = new KeyFrame(Duration.ZERO, new KeyValue(pane.opacityProperty(), 0));
        KeyFrame paneEnd = new KeyFrame(duration, new KeyValue(pane.opacityProperty(), 1));

        timeline.getKeyFrames().addAll(paneStart, paneEnd);
        if (pane == functionsPane) {
            KeyFrame frame = new KeyFrame(
                    duration,
                    new KeyValue(keyCircle.radiusProperty(), 35),
                    new KeyValue(keyCircle.layoutXProperty(), 300),
                    new KeyValue(keyCircle.layoutYProperty(), 200),
                    new KeyValue(keyButton.layoutXProperty(), 270),
                    new KeyValue(keyButton.layoutYProperty(), 170),

                    new KeyValue(returnCircle.radiusProperty(), 35),
                    new KeyValue(returnCircle.layoutXProperty(), 300),
                    new KeyValue(returnCircle.layoutYProperty(), 200),
                    new KeyValue(returnCircle.visibleProperty(), false),
                    new KeyValue(returnButton.layoutXProperty(), 270),
                    new KeyValue(returnButton.layoutYProperty(), 170),
                    new KeyValue(returnButton.visibleProperty(), false)
            );
            timeline.getKeyFrames().addAll(frame);
        } else {
            KeyFrame frame0 = new KeyFrame(
                    duration.divide(100),
                    new KeyValue(returnCircle.visibleProperty(), true),
                    new KeyValue(returnButton.visibleProperty(), true)
            );

            KeyFrame frame1 = new KeyFrame(
                    duration,
                    new KeyValue(keyCircle.radiusProperty(), 80),
                    new KeyValue(keyCircle.layoutXProperty(), 600),
                    new KeyValue(keyCircle.layoutYProperty(), 0),
                    new KeyValue(keyButton.layoutXProperty(), 540),
                    new KeyValue(keyButton.layoutYProperty(), 5),

                    new KeyValue(returnCircle.radiusProperty(), 80),
                    new KeyValue(returnCircle.layoutXProperty(), 600),
                    new KeyValue(returnCircle.layoutYProperty(), 400),
                    new KeyValue(returnButton.layoutXProperty(), 540),
                    new KeyValue(returnButton.layoutYProperty(), 340)
            );
            timeline.getKeyFrames().addAll(frame0, frame1);
        }

        timeline.play();
    }

    private void changeToFunctionsPane() {
        changePane(functionsPane);
    }

    void changeToFilePane() {
        changePane(filePane);
    }

    void changeToTextPane() {
        textController.prepare();
        changePane(textPane);
    }

    void changeToIdentityPane() {
        changePane(identityPane);
    }

    private void changeKeyBtnBackground() {
        App.getRsa().ifPresentOrElse(
                r -> {
                    keyButton.getStyleClass().removeAll("keyButton-not-changed");
                    keyButton.getStyleClass().addAll("keyButton-changed");
                },
                () -> {
                    keyButton.getStyleClass().removeAll("keyButton-changed");
                    keyButton.getStyleClass().addAll("keyButton-not-changed");
                }
        );
    }
}
