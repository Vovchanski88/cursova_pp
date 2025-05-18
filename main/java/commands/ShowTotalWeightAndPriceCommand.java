package commands;

import javafx.scene.control.TextArea;
import necklace.Necklace;

public class ShowTotalWeightAndPriceCommand implements Command {
    public Necklace necklace;
    public TextArea outputArea;

    public ShowTotalWeightAndPriceCommand(Necklace necklace, TextArea outputArea) {
        this.necklace = necklace;
        this.outputArea = outputArea;
    }

    @Override
    public void execute() {
        String result = String.format("Загальна вага: %.2f карат, Загальна ціна: %.2f\n",
                                    necklace.calculateTotalWeight(),
                                    necklace.calculateTotalPrice());
        outputArea.setText(result);
    }
}