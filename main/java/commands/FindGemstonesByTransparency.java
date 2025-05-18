package commands;

import gemstones.Gemstone;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import necklace.Necklace;

import java.util.List;
import java.util.Optional;

public class FindGemstonesByTransparency implements Command {
    private Necklace necklace;
    private TextArea outputArea;
    protected TextInputDialog minTransparencyDialog = new TextInputDialog();
    protected TextInputDialog maxTransparencyDialog = new TextInputDialog();

    public FindGemstonesByTransparency(Necklace necklace, TextArea outputArea) {
        this.necklace = necklace;
        this.outputArea = outputArea;
    }

    @Override
    public void execute() {
        TextInputDialog minDialog = (minTransparencyDialog != null) ? minTransparencyDialog : new TextInputDialog();
        minDialog.setTitle("Пошук каменів");
        minDialog.setHeaderText(null);
        minDialog.setContentText("Мінімальна прозорість (%):");
        Optional<String> minResult = minDialog.showAndWait();

        TextInputDialog maxDialog = (maxTransparencyDialog != null) ? maxTransparencyDialog : new TextInputDialog();
        maxDialog.setTitle("Пошук каменів");
        maxDialog.setHeaderText(null);
        maxDialog.setContentText("Максимальна прозорість (%):");
        Optional<String> maxResult = maxDialog.showAndWait();

        if (minResult.isPresent() && maxResult.isPresent()) {
            try {
                double min = Double.parseDouble(minResult.get());
                double max = Double.parseDouble(maxResult.get());

                outputArea.appendText("Камені з прозорістю в діапазоні " + min + "% - " + max + "%:\n");
                for (Gemstone gemstone : necklace.findGemstonesByTransparency(min, max)) {
                    outputArea.appendText(gemstone.toString() + "\n");
                }
            } catch (NumberFormatException e) {
                outputArea.appendText("Будь ласка, введіть коректні числа.\n");
            }
        }
    }

    public void executeWithParams(String minTransparencyInput, String maxTransparencyInput) {
        try {
            int minTransparency = Integer.parseInt(minTransparencyInput);
            int maxTransparency = Integer.parseInt(maxTransparencyInput);

            if (minTransparency > maxTransparency) {
                outputArea.appendText("Invalid range: min > max\n");
                return;
            }

            List<Gemstone> filtered = necklace.getGemstones().stream()
                .filter(g -> g.getTransparency() >= minTransparency && g.getTransparency() <= maxTransparency)
                .toList();

            if (filtered.isEmpty()) {
                outputArea.appendText("No gemstones found\n");
            } else {
                for (Gemstone g : filtered) {
                    outputArea.appendText(g.toString() + "\n");
                }
            }
        } catch (NumberFormatException e) {
            outputArea.appendText("Invalid input: must be integers\n");
        }
    }
}