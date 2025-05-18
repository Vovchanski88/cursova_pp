import commands.CreateNecklaceAndAddGemstoneCommand;
import javafx.scene.control.TextArea;
import necklace.Necklace;

import java.util.*;

public class TestableCommand extends CreateNecklaceAndAddGemstoneCommand {

    private final List<String> inputs;
    private int index = 0;

    public TestableCommand(Necklace necklace, TextArea outputArea, List<String> inputs) {
        super(necklace, outputArea);
        this.inputs = inputs;
    }

    @Override
    public Optional<String> showTextInputDialog(String title, String header, String content, String defaultValue) {
        if (index >= inputs.size()) return Optional.empty();
        return Optional.of(inputs.get(index++));
    }

    @Override
    public Optional<String> showChoiceDialog(String title, String header, String[] options) {
        if (index >= inputs.size()) return Optional.empty();

        String input = inputs.get(index++);

        // Шукаємо, який варіант у options відповідає input (як у твоєму тесті: "1. Алмаз" і т.д.)
        for (String option : options) {
            if (option.equalsIgnoreCase(input)) {
                return Optional.of(option);
            }
        }
        return Optional.empty();
    }
}
