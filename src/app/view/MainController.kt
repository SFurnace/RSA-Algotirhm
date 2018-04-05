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
import kotlin.system.exitProcess

class MainController {
    @FXML
    private lateinit var mainPane: Pane
    @FXML
    private lateinit var returnButton: Button
    @FXML
    private lateinit var returnCircle: Circle
    @FXML
    private lateinit var keyCircle: Circle
    @FXML
    private lateinit var keyButton: Button

    @FXML
    fun clickReturnBtn() {
        changeToFunctionsPane()
    }

    @FXML
    fun clickKeyBtn() {
        KeyAlert().showAndWait().ifPresent { buttonType ->
            val chooser = FileChooser()
            when (buttonType) {
                KeyAlert.CHOOSE -> {
                    chooser.title = "Choose RSA Key File"
                    chooser.extensionFilters.setAll(ExtensionFilter("RSA Key File", "*.pub", "*.pri"))

                    chooser.showOpenDialog(App.primaryStage)?.let {
                        App.setRSAFromFile(it.toPath())
                        changeKeyBtnBackground()
                    }
                }
                KeyAlert.GENERATE -> {
                    chooser.title = "Generate RSA Key Files"
                    chooser.extensionFilters.setAll(ExtensionFilter("RSA Key File", "*.pub"))
                    chooser.initialFileName = "key.pub"

                    chooser.showSaveDialog(App.primaryStage)?.let {
                        val file2 = it.toPath().resolveSibling(it.name.replace("pub$".toRegex(), "pri")).toFile()
                        App.generateKeyFiles(it.toPath(), file2.toPath())
                    }
                }
            }
        }
    }

    private fun changePane(fileName: String, toFunctionsPane: Boolean) {
        if (App.rsa != null || toFunctionsPane) {
            try {
                val node = FXMLLoader.load<Node>(javaClass.getResource(fileName))
                mainPane.children.clear()
                mainPane.children.add(node)

                if (toFunctionsPane) {
                    toFunctionsPaneAnimate(node)
                } else {
                    toAnotherPaneAnimate(node)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Alert(ERROR, e.message).showAndWait()
                exitProcess(-1)
            }
        } else {
            Alert(WARNING, "Please select a RSA Key file.").showAndWait()
        }
    }

    private fun toFunctionsPaneAnimate(node: Node) {
        val timeLine = Timeline()
        val startFrame = KeyFrame(Duration.ZERO, KeyValue(node.opacityProperty(), 0))
        val endFrame = KeyFrame(MainController.ANIMATE_DURATION,
                KeyValue(node.opacityProperty(), 1),

                KeyValue(keyCircle.radiusProperty(), 35),
                KeyValue(keyCircle.layoutXProperty(), 300),
                KeyValue(keyCircle.layoutYProperty(), 200),
                KeyValue(keyButton.layoutXProperty(), 270),
                KeyValue(keyButton.layoutYProperty(), 170),

                KeyValue(returnCircle.radiusProperty(), 35),
                KeyValue(returnCircle.layoutXProperty(), 300),
                KeyValue(returnCircle.layoutYProperty(), 200),
                KeyValue(returnCircle.visibleProperty(), false),
                KeyValue(returnButton.layoutXProperty(), 270),
                KeyValue(returnButton.layoutYProperty(), 170),
                KeyValue(returnButton.visibleProperty(), false)
        )

        timeLine.keyFrames.addAll(startFrame, endFrame)
        timeLine.play()
    }

    private fun toAnotherPaneAnimate(node: Node) {
        val timeLine = Timeline()
        val startFrame = KeyFrame(Duration.ZERO, KeyValue(node.opacityProperty(), 0))
        val frame1 = KeyFrame(
                MainController.ANIMATE_DURATION.divide(100.0),
                KeyValue(returnCircle.visibleProperty(), true),
                KeyValue(returnButton.visibleProperty(), true)
        )
        val endFrame = KeyFrame(
                MainController.ANIMATE_DURATION,
                KeyValue(node.opacityProperty(), 1),

                KeyValue(keyCircle.radiusProperty(), 80),
                KeyValue(keyCircle.layoutXProperty(), 600),
                KeyValue(keyCircle.layoutYProperty(), 0),
                KeyValue(keyButton.layoutXProperty(), 540),
                KeyValue(keyButton.layoutYProperty(), 5),

                KeyValue(returnCircle.radiusProperty(), 80),
                KeyValue(returnCircle.layoutXProperty(), 600),
                KeyValue(returnCircle.layoutYProperty(), 400),
                KeyValue(returnButton.layoutXProperty(), 540),
                KeyValue(returnButton.layoutYProperty(), 340)
        )

        timeLine.keyFrames.addAll(startFrame, frame1, endFrame)
        timeLine.play()
    }

    private fun changeKeyBtnBackground() {
        App.rsa?.run {
            keyButton.styleClass.removeAll("key-button-inactive")
            keyButton.styleClass.addAll("key-button-active")
        } ?: run {
            keyButton.styleClass.removeAll("key-button-active")
            keyButton.styleClass.addAll("key-button-inactive")
        }
    }

    fun initialize() {
        MainController.singleton = this

        changeKeyBtnBackground()
        changeToFunctionsPane()
    }

    companion object {
        private val ANIMATE_DURATION = Duration.seconds(0.5)
        private lateinit var singleton: MainController

        internal fun changeToFunctionsPane() {
            singleton.changePane("fxml/FunctionsPane.fxml", true)
        }

        internal fun changeToFilePane() {
            singleton.changePane("fxml/FilePane.fxml", false)
        }

        internal fun changeToTextPane() {
            singleton.changePane("fxml/TextPane.fxml", false)
        }

        internal fun changeToIdentityPane() {
            singleton.changePane("fxml/IdentityPane.fxml", false)
        }
    }
}
