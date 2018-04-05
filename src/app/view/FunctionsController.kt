package app.view

import javafx.fxml.FXML

class FunctionsController {
    @FXML
    private fun clickTextBtn() {
        MainController.changeToTextPane()
    }

    @FXML
    private fun clickIdentityBtn() {
        MainController.changeToIdentityPane()
    }

    @FXML
    private fun clickFileBtn() {
        MainController.changeToFilePane()
    }
}
