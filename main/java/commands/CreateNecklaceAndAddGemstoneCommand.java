package commands;

import gemstones.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ChoiceDialog;
import necklace.Necklace;
import utils.LoggerUtil;

import java.util.Optional;

public class CreateNecklaceAndAddGemstoneCommand implements Command {
    private final Necklace necklace;
    private final TextArea outputArea;

    public CreateNecklaceAndAddGemstoneCommand(Necklace necklace, TextArea outputArea) {
        this.necklace = necklace;
        this.outputArea = outputArea;
    }

    @Override
    public void execute() {
        LoggerUtil.logInfo("Створення нового намиста та додавання каменів");
        outputArea.appendText("Намисто створено!\n");

        while (true) {
            Optional<String> result = showTextInputDialog("1",
                "Додавання каменя",
                "Виберіть тип каменя",
                "Тип каменя (1 - Дорогоцінний, 2 - Напівкоштовний або -1 для виходу):");

            if (result.isEmpty() || result.get().equals("-1")) {
                break;
            }

            try {
                int gemType = Integer.parseInt(result.get());
                Gemstone gemstone = createGemstone(gemType);
                if (gemstone != null) {
                    necklace.addGemstone(gemstone);
                    outputArea.appendText("Камінь додано до намиста.\n");
                } else {
                    outputArea.appendText("Невірний тип каменя.\n");
                }
            } catch (NumberFormatException e) {
                outputArea.appendText("Будь ласка, введіть число.\n");
            }
        }
    }

    public Gemstone createGemstone(int type) {
        Optional<String> gemResult = showChoiceDialog(
            "Вибір каменя", "Виберіть камінь:",
            type == 1
                ? new String[]{"1. Алмаз", "2. Рубін", "3. Сапфір"}
                : new String[]{"1. Бірюза", "2. Опал", "3. Аметист"}
        );

        if (gemResult.isEmpty()) return null;

        int gemChoice = Integer.parseInt(gemResult.get().substring(0, 1));
        String name = getGemstoneName(type, gemChoice);
        if (name == null) {
            outputArea.appendText("Невірний вибір.\n");
            return null;
        }

        double weight = promptDouble("Вага (карати):", "Введіть вагу каменя");
        double price = promptDouble("Ціна:", "Введіть ціну каменя");
        double transparency = promptDouble("Прозорість (%):", "Введіть прозорість каменя");

        return type == 1
            ? new PreciousGemstone(name, weight, price, transparency)
            : new SemiPreciousGemstone(name, weight, price, transparency);
    }

    public Optional<String> showTextInputDialog(String title, String header, String content, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        return dialog.showAndWait();
    }

    public Optional<String> showChoiceDialog(String title, String header, String[] options) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(options[0], options);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        return dialog.showAndWait();
    }

    protected double promptDouble(String content, String title) {
        Optional<String> input = showTextInputDialog("", title, null, content);
        try {
            return input.map(Double::parseDouble).orElse(0.0);
        } catch (NumberFormatException e) {
            outputArea.appendText("Неправильний формат числа, використано 0.0\n");
            return 0.0;
        }
    }

    public String getGemstoneName(int type, int gemChoice) {
        switch (type) {
            case 1:
                return switch (gemChoice) {
                    case 1 -> "Алмаз";
                    case 2 -> "Рубін";
                    case 3 -> "Сапфір";
                    default -> null;
                };
            case 2:
                return switch (gemChoice) {
                    case 1 -> "Бірюза";
                    case 2 -> "Опал";
                    case 3 -> "Аметист";
                    default -> null;
                };
            default:
                return null;
        }
    }

    public Necklace getNecklace() {
        return this.necklace;
    }
}