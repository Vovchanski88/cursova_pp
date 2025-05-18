package main;

import commands.*;
import database.DatabaseManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import necklace.Necklace;
import utils.LoggerUtil;

public class Main extends Application {
    public Necklace necklace;
    public TextArea outputArea = new TextArea();

    @Override
    public void start(Stage stage) {
        try {
            LoggerUtil.logInfo("Запуск додатку");

            // Ініціалізація намиста
            initializeNecklace();

            // Створення головного контейнера
            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: #f5f5f5;");

            // Додаємо верхню панель з заголовком
            root.setTop(createHeader());

            // Додаємо центр з областю виводу
            root.setCenter(createOutputArea());

            // Додаємо нижню панель з кнопками
            root.setBottom(createButtonPanel());

            // Налаштування сцени
            Scene scene = new Scene(root, 900, 650);

            // Налаштування вікна
            stage.setScene(scene);
            stage.setTitle("Управління намистом з каменями");
            stage.setOnCloseRequest(e -> {
                LoggerUtil.logInfo("Додаток закривається");
                DatabaseManager.close();
            });
            stage.show();

            LoggerUtil.logInfo("Інтерфейс додатку успішно ініціалізовано");
        } catch (Exception e) {
            LoggerUtil.logError("Помилка ініціалізації додатку", e);
            showErrorAlert("Сталася критична помилка. Дивіться лог для деталей.");
        }
    }

    public void initializeNecklace() {
        necklace = DatabaseManager.loadNecklace();
        if (necklace.getGemstones().isEmpty()) {
            LoggerUtil.logInfo("Створено нове порожнє намисто");
            outputArea.setText("Створено нове намисто. Додайте камені.");
        } else {
            LoggerUtil.logInfo("Завантажено намисто з БД з " + necklace.getGemstones().size() + " каменями");
            outputArea.setText("Завантажено намисто з БД:\n" + necklace);
        }
    }

    public HBox createHeader() {
        Label title = new Label("Управління намистом з каменями");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.DARKSLATEBLUE);

        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: linear-gradient(to right, #e6f7ff, #ffffff);");

        return header;
    }

    public ScrollPane createOutputArea() {
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setFont(Font.font("Consolas", 14));
        outputArea.setStyle("-fx-control-inner-background: #fffaf0; -fx-text-fill: #333;");

        ScrollPane scrollPane = new ScrollPane(outputArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPadding(new Insets(10));

        return scrollPane;
    }

    public GridPane createButtonPanel() {
        GridPane buttonGrid = new GridPane();
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setHgap(15);
        buttonGrid.setVgap(15);
        buttonGrid.setPadding(new Insets(20));
        buttonGrid.setStyle("-fx-background-color: #e6e6fa; -fx-background-radius: 10;");

        // Визначаємо спільні стилі для кнопок
        String buttonStyle = "-fx-background-radius: 5; -fx-border-radius: 5; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1); "
                + "-fx-cursor: hand; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-pref-width: 200; -fx-pref-height: 40;";

        // Ряд 1
        buttonGrid.add(createButton("Додати камінь", buttonStyle + "-fx-background-color: #4CAF50;",
            () -> new CreateNecklaceAndAddGemstoneCommand(necklace, outputArea).execute()), 0, 0);

        buttonGrid.add(createButton("Видалити камінь", buttonStyle + "-fx-background-color: #f44336;",
            () -> new RemoveGemstoneCommand(necklace, outputArea).execute()), 1, 0);

        buttonGrid.add(createButton("Показати намисто", buttonStyle + "-fx-background-color: #2196F3;",
            () -> new ShowNecklaceCommand(necklace, outputArea).execute()), 2, 0);

        // Ряд 2
        buttonGrid.add(createButton("Загальна вартість", buttonStyle + "-fx-background-color: #FF9800;",
            () -> new ShowTotalWeightAndPriceCommand(necklace, outputArea).execute()), 0, 1);

        buttonGrid.add(createButton("Сортувати за прозорістю", buttonStyle + "-fx-background-color: #9C27B0;",
            () -> new SortByTransperency(necklace, outputArea).execute()), 1, 1);

        buttonGrid.add(createButton("Знайти за прозорістю", buttonStyle + "-fx-background-color: #009688;",
            () -> new FindGemstonesByTransparency(necklace, outputArea).execute()), 2, 1);

        // Ряд 3 (одна кнопка по центру)
        Button saveBtn = createButton("Зберегти в БД", buttonStyle + "-fx-background-color: #607D8B;",
            () -> {
                try {
                    DatabaseManager.saveNecklace(necklace);
                    outputArea.appendText("\nНамисто збережено в БД!");
                    LoggerUtil.logInfo("Намисто успішно збережено в БД");
                    showInfoAlert("Намисто успішно збережено в БД");
                } catch (Exception e) {
                    LoggerUtil.logError("Помилка збереження намиста в БД", e);
                    outputArea.appendText("\nПомилка збереження: " + e.getMessage());
                    showErrorAlert("Помилка збереження: " + e.getMessage());
                }
            });
        buttonGrid.add(saveBtn, 1, 2);

        return buttonGrid;
    }

    public Button createButton(String text, String style, Runnable action) {
        Button button = new Button(text);
        button.setStyle(style);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setOnAction(e -> {
            LoggerUtil.logDebug("Натиснуто кнопку: " + text);
            action.run();
        });
        return button;
    }

    public void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Інформація");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        try {
            LoggerUtil.logInfo("Запуск головного методу");
            LoggerUtil.logError("ТЕСТОВА КРИТИЧНА ПОМИЛКА: Це тестовий лист",
                    new RuntimeException("Тестова помилка"));
            launch(args);
        } catch (Exception e) {
            LoggerUtil.logError("Неконтрольована помилка в додатку", e);
        } finally {
            LoggerUtil.logInfo("Завершення роботи додатку");
            DatabaseManager.close();
        }
    }
}