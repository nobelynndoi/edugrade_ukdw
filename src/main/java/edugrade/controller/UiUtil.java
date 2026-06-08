package edugrade.controller;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.util.function.UnaryOperator;

public final class UiUtil {
    public static final DecimalFormat DECIMAL = new DecimalFormat("#0.00");

    private UiUtil() {
    }

    static TextField input(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("text-field-style");
        return field;
    }

    static Label label(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    static Button primaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("btn-primary");
        return button;
    }

    static Button secondaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("btn-secondary");
        return button;
    }

    static Button dangerButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("btn-danger");
        return button;
    }

    static VBox card(String title, Node... children) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        Label label = new Label(title);
        label.getStyleClass().add("section-title");
        card.getChildren().add(label);
        card.getChildren().addAll(children);
        return card;
    }

    static TextFormatter<String> numericTextFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (text.matches("\\d{0,3}([,.]\\d{0,2})?")) {
                return change;
            }
            return null;
        };
        return new TextFormatter<>(filter);
    }

    static double parseDouble(TextField field) {
        return Double.parseDouble(field.getText().trim().replace(",", "."));
    }

    static double parseScoreText(String text) {
        double value = Double.parseDouble(text.trim().replace(",", "."));
        if (value < 0 || value > 100) {
            throw new NumberFormatException("score out of range");
        }
        return value;
    }

    static boolean isBlank(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText() == null || field.getText().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    static void alert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
