package commands;

import gemstones.Gemstone;
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
            Optional<String> typeResult = showChoiceDialog(
                "Додавання каменя",
                "Виберіть тип каменя або 'Вихід' для завершення",
                new String[]{"Дорогоцінний", "Напівкоштовний", "Вихід"}
            );

            if (typeResult.isEmpty() || typeResult.get().equals("Вихід")) {
                break;
            }

            String gemType = typeResult.get();
            Gemstone gemstone = createGemstone(gemType);
            if (gemstone != null) {
                necklace.addGemstone(gemstone);
                outputArea.appendText("Камінь додано до намиста.\n");
            } else {
                outputArea.appendText("Невірний вибір.\n");
            }
        }
    }

    public Gemstone createGemstone(String type) {
        String[] gemOptions;
        if (type.equals("Дорогоцінний")) {
            gemOptions = new String[]{"Алмаз", "Рубін", "Сапфір"};
        } else if (type.equals("Напівкоштовний")) {
            gemOptions = new String[]{"Бірюза", "Опал", "Аметист"};
        } else {
            return null;
        }

        Optional<String> gemResult = showChoiceDialog(
            "Вибір каменя",
            "Виберіть конкретний камінь:",
            gemOptions
        );

        if (gemResult.isEmpty()) return null;

        String name = gemResult.get();
        double weight = promptDouble("Вага (карати):", "Введіть вагу каменя");
        double price = promptDouble("Ціна:", "Введіть ціну каменя");
        double transparency = promptDouble("Прозорість (%):", "Введіть прозорість каменя");

        return new Gemstone(type, name, weight, price, transparency);
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