package app.view

import app.App
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.ERROR
import javafx.scene.control.Alert.AlertType.WARNING
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.scene.shape.Circle
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import javafx.util.Duration
import java.io.IOException

class MainController {
    @FXML
    private val mainPane: Pane? = null
    @FXML
    private val returnButton: Button? = null
    @FXML
    private val returnCircle: Circle? = null
    @FXML
    private val keyCircle: Circle? = null
    @FXML
    private val keyButton: Button? = null

    @FXML
    fun clickKeyBtn() {
        val alert = KeyAlert()
        alert.showAndWait().ifPresent { r ->
            if (r == KeyAlert.CHOOSE) {
                val chooser = FileChooser()
                chooser.title = "Choose RSA Key File"
                chooser.extensionFilters.setAll(ExtensionFilter("RSA Key File", "*.pub", "*.pri"))

                val file = chooser.showOpenDialog(App.primaryStage)
                if (file != null) {
                    App.setRSA(file.toPath())
                    changeKeyBtnBackground()
                }
            } else if (r == KeyAlert.GENERATE) {
                val chooser = FileChooser()
                chooser.title = "Generate RSA Key Files"
                chooser.extensionFilters.setAll(ExtensionFilter("RSA Key File", "*.pub"))
                chooser.initialFileName = "key.pub"

                val file1 = chooser.showSaveDialog(App.primaryStage)
                if (file1 != null) {
                    val file2 = file1.toPath().resolveSibling(file1.name.replace("pub$".toRegex(), "pri")).toFile()
                    App.generateKeyFiles(file1.toPath(), file2.toPath())
                }
            }
        }
    }

    @FXML
    fun clickReturnBtn() {
        changeToFunctionsPane()
    }

    fun initialize() {
        MainController.singleton = this

        changeKeyBtnBackground()
        changeToFunctionsPane()
    }

    private fun changePane(fileName: String, toFunctionsPane: Boolean) {
        if (App.getRsa().isPresent || toFunctionsPane) {
            try {
                val node = FXMLLoader.load<Node>(javaClass.getResource(fileName))
                mainPane!!.children.clear()
                mainPane.children.add(node)

                if (toFunctionsPane) {
                    toFunctionsPaneAnimate(node)
                } else {
                    toAnotherPaneAnimate(node)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Alert(ERROR, e.message).showAndWait()
                System.exit(-1)
            }

        } else {
            Alert(WARNING, "Please select a RSA Key file.").showAndWait()
        }
    }

    private fun toFunctionsPaneAnimate(node: Node) {
        val timeline = Timeline()
        val startFrame = KeyFrame(Duration.ZERO, KeyValue(node.opacityProperty(), 0))
        val endFrame = KeyFrame(MainController.ANIMATE_DURATION,
                KeyValue(node.opacityProperty(), 1),

                KeyValue(keyCircle!!.radiusProperty(), 35),
                KeyValue(keyCircle.layoutXProperty(), 300),
                KeyValue(keyCircle.layoutYProperty(), 200),
                KeyValue(keyButton!!.layoutXProperty(), 270),
                KeyValue(keyButton.layoutYProperty(), 170),

                KeyValue(returnCircle!!.radiusProperty(), 35),
                KeyValue(returnCircle.layoutXProperty(), 300),
                KeyValue(returnCircle.layoutYProperty(), 200),
                KeyValue(returnCircle.visibleProperty(), false),
                KeyValue(returnButton!!.layoutXProperty(), 270),
                KeyValue(returnButton.layoutYProperty(), 170),
                KeyValue(returnButton.visibleProperty(), false)
        )

        timeline.keyFrames.addAll(startFrame, endFrame)
        timeline.play()
    }

    private fun toAnotherPaneAnimate(node: Node) {
        val timeline = Timeline()
        val startFrame = KeyFrame(Duration.ZERO, KeyValue(node.opacityProperty(), 0))
        val frame1 = KeyFrame(
                MainController.ANIMATE_DURATION.divide(100.0),
                KeyValue(returnCircle!!.visibleProperty(), true),
                KeyValue(returnButton!!.visibleProperty(), true)
        )
        val endFrame = KeyFrame(
                MainController.ANIMATE_DURATION,
                KeyValue(node.opacityProperty(), 1),

                KeyValue(keyCircle!!.radiusProperty(), 80),
                KeyValue(keyCircle.layoutXProperty(), 600),
                KeyValue(keyCircle.layoutYProperty(), 0),
                KeyValue(keyButton!!.layoutXProperty(), 540),
                KeyValue(keyButton.layoutYProperty(), 5),

                KeyValue(returnCircle.radiusProperty(), 80),
                KeyValue(returnCircle.layoutXProperty(), 600),
                KeyValue(returnCircle.layoutYProperty(), 400),
                KeyValue(returnButton.layoutXProperty(), 540),
                KeyValue(returnButton.layoutYProperty(), 340)
        )

        timeline.keyFrames.addAll(startFrame, frame1, endFrame)
        timeline.play()
    }

    private fun changeKeyBtnBackground() {
        App.getRsa().ifPresentOrElse(
                { r ->
                    keyButton!!.styleClass.removeAll("keyButton-not-changed")
                    keyButton.styleClass.addAll("keyButton-changed")
                }
        ) {
            keyButton!!.styleClass.removeAll("keyButton-changed")
            keyButton.styleClass.addAll("keyButton-not-changed")
        }
    }

    companion object {
        private val ANIMATE_DURATION = Duration.seconds(0.5)
        private var singleton: MainController? = null

        internal fun changeToFunctionsPane() {
            singleton!!.changePane("FunctionsPane.fxml", true)
        }

        internal fun changeToFilePane() {
            singleton!!.changePane("FilePane.fxml", false)
        }

        internal fun changeToTextPane() {
            singleton!!.changePane("TextPane.fxml", false)
        }

        internal fun changeToIdentityPane() {
            singleton!!.changePane("IdentityPane.fxml", false)
        }
    }
}
