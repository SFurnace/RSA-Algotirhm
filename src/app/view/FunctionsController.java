package app.view;

import javafx.fxml.FXML;

public class FunctionsController {
    @FXML
    private void clickTextBtn() {
        MainController.changeToTextPane();
    }

    @FXML
    private void clickIdentityBtn() {
        MainController.changeToIdentityPane();
    }

    @FXML
    private void clickFileBtn() {
        MainController.changeToFilePane();
    }
}
