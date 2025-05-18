package commands;

import gemstones.Gemstone;
import javafx.scene.control.TextArea;
import necklace.Necklace;
import java.util.List;

public class SortByTransperency implements Command {
    public Necklace necklace;
    public TextArea outputArea;

    public SortByTransperency(Necklace necklace, TextArea outputArea) {
        this.necklace = necklace;
        this.outputArea = outputArea;
    }

    @Override
    public void execute() {
        List<Gemstone> sortedList = necklace.getSortedByTransparency();
        StringBuilder sb = new StringBuilder("Камені в намисті відсортовані за прозорістю:\n");

        for (Gemstone gemstone : sortedList) {
            sb.append(gemstone).append("\n");
        }

        outputArea.setText(sb.toString());
    }
}