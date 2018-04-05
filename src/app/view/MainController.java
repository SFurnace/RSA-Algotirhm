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
import static javafx.scene.control.Alert.AlertType.WARNING;

public class MainController {
    private static final Duration ANIMATE_DURATION = Duration.seconds(0.5);
    private static MainController singleton;
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

    static void changeToFunctionsPane() {
        singleton.changePane("FunctionsPane.fxml", true);
    }

    static void changeToFilePane() {
        singleton.changePane("FilePane.fxml", false);
    }

    static void changeToTextPane() {
        singleton.changePane("TextPane.fxml", false);
    }

    static void changeToIdentityPane() {
        singleton.changePane("IdentityPane.fxml", false);
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
        MainController.singleton = this;

        changeKeyBtnBackground();
        changeToFunctionsPane();
    }

    private void changePane(String fileName, boolean toFunctionsPane) {
        if (App.getRsa().isPresent() || toFunctionsPane) {
            try {
                Node node = FXMLLoader.load(getClass().getResource(fileName));
                mainPane.getChildren().clear();
                mainPane.getChildren().add(node);

                if (toFunctionsPane) {
                    toFunctionsPaneAnimate(node);
                } else {
                    toAnotherPaneAnimate(node);
                }
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(ERROR, e.getMessage()).showAndWait();
                System.exit(-1);
            }
        } else {
            new Alert(WARNING, "Please select a RSA Key file.").showAndWait();
        }
    }

    private void toFunctionsPaneAnimate(Node node) {
        Timeline timeline = new Timeline();
        KeyFrame startFrame = new KeyFrame(Duration.ZERO, new KeyValue(node.opacityProperty(), 0));
        KeyFrame endFrame = new KeyFrame(MainController.ANIMATE_DURATION,
                new KeyValue(node.opacityProperty(), 1),

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

        timeline.getKeyFrames().addAll(startFrame, endFrame);
        timeline.play();
    }

    private void toAnotherPaneAnimate(Node node) {
        Timeline timeline = new Timeline();
        KeyFrame startFrame = new KeyFrame(Duration.ZERO, new KeyValue(node.opacityProperty(), 0));
        KeyFrame frame1 = new KeyFrame(
                MainController.ANIMATE_DURATION.divide(100),
                new KeyValue(returnCircle.visibleProperty(), true),
                new KeyValue(returnButton.visibleProperty(), true)
        );
        KeyFrame endFrame = new KeyFrame(
                MainController.ANIMATE_DURATION,
                new KeyValue(node.opacityProperty(), 1),

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

        timeline.getKeyFrames().addAll(startFrame, frame1, endFrame);
        timeline.play();
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
