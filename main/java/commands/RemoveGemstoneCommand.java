package commands;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import necklace.Necklace;
import utils.LoggerUtil;

import java.util.Optional;

public class RemoveGemstoneCommand implements Command {
    private Necklace necklace;
    private TextArea outputArea;
    public TextInputDialog inputDialog;

    public RemoveGemstoneCommand(Necklace necklace, TextArea outputArea) {
        this.necklace = necklace;
        this.outputArea = outputArea;
    }

    public void setDialog(TextInputDialog dialog) {
        this.inputDialog = dialog;
    }

    @Override
    public void execute() {
        TextInputDialog dialog = new TextInputDialog();
        LoggerUtil.logInfo("Спроба видалення каменя");
        dialog.setTitle("Видалення каменя");
        dialog.setHeaderText("Введіть ID каменя для видалення");
        dialog.setContentText("ID каменя:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int id = Integer.parseInt(result.get());
                boolean removed = necklace.removeGemstone(id);
                if (removed) {
                    outputArea.appendText("Камінь з ID " + id + " був видалений.\n");
                    LoggerUtil.logInfo("Камінь з ID " + id + " успішно видалено");
                } else {
                    outputArea.appendText("Камінь з ID " + id + " не знайдено.\n");
                    LoggerUtil.logWarning("Камінь з ID " + id + " не знайдено");
                }
            } catch (NumberFormatException e) {
                outputArea.appendText("Будь ласка, введіть коректний ID (число).\n");
            }
        }
    }
}