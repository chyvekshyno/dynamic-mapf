package tuki.diploma.tmo.controllers.dialogs;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MapSizeController {

    private Stage dialogStage;
    private Pair<Integer, Integer> result;

    @FXML
    public DialogPane dialogMapSize;
    @FXML
    public TextField tfHeight;
    @FXML
    public TextField tfWidth;
    @FXML
    public Button btnDialogOK;
    @FXML
    public Button btnDialogCancel;

    @FXML
    public void handleClickOK(final MouseEvent mouseEvent) {
        this.result = new Pair<>(
                Integer.valueOf(tfHeight.getText()),
                Integer.valueOf(tfWidth.getText()));
        dialogStage.close();
    }

    @FXML
    public void handleClickCancel(final MouseEvent mouseEvent) {
        dialogStage.close();
    }

    public Optional<Pair<Integer, Integer>> getSize() {
        if (this.result == null)
            return Optional.empty();

        return Optional.of(this.result);
    }

    public void setDialogStage(final Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

}
