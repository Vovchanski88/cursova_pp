package commands;

import javafx.scene.control.TextArea;
import necklace.Necklace;

public class ShowNecklaceCommand implements Command {
    public Necklace necklace;
    public TextArea outputArea;

    public ShowNecklaceCommand(Necklace necklace, TextArea outputArea) {
        this.necklace = necklace;
        this.outputArea = outputArea;
    }

    @Override
    public void execute() {
        outputArea.setText(necklace.toString()); // Вивід у TextArea замість консолі
    }
}