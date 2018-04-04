package app.view;

import javafx.fxml.FXML;

import static app.view.MainController.getMainController;

public class FunctionsController {
    @FXML
    private void clickTextBtn() {
        getMainController().changeToTextPane();
    }

    @FXML
    private void clickIdentityBtn() {
        getMainController().changeToIdentityPane();
    }

    @FXML
    private void clickFileBtn() {
        getMainController().changeToFilePane();
    }
}
