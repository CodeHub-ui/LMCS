package com.library.util;

import animatefx.animation.FadeIn;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * AdvancedSearchBar is a reusable JavaFX component that provides instant live search functionality.
 * It supports manual typing, RFID scanner input, and barcode scanner input (all treated as text input).
 * Features debounced search, search and clear icons, rounded design, and smooth animations.
 *
 * @param <T> The type of data to search through.
 */
public class AdvancedSearchBar<T> extends HBox {

    private final TextField searchField;
    private final Button searchButton;
    private final Button clearButton;
    private ScheduledExecutorService debounceExecutor = Executors.newSingleThreadScheduledExecutor();

    private final Supplier<List<T>> dataSupplier;
    private final Function<T, String> toStringFunction;
    private final Consumer<List<T>> onSearchUpdate;

    private static final String SEARCH_ICON = "🔍";
    private static final String CLEAR_ICON = "✕";

    /**
     * Constructs an AdvancedSearchBar.
     *
     * @param dataSupplier    Supplier for the list of items to search.
     * @param toStringFunction Function to convert an item to its string representation for searching.
     * @param onSearchUpdate  Consumer called with the filtered list on search updates.
     * @param placeholder     Placeholder text for the search field.
     */
    public AdvancedSearchBar(Supplier<List<T>> dataSupplier, Function<T, String> toStringFunction, Consumer<List<T>> onSearchUpdate, String placeholder) {
        this.dataSupplier = dataSupplier;
        this.toStringFunction = toStringFunction;
        this.onSearchUpdate = onSearchUpdate;

        // Initialize UI components
        searchField = new TextField();
        searchField.setPromptText(placeholder);
        searchField.setPrefWidth(400);
        searchField.setStyle("-fx-background-color: white; -fx-border-color: #d1d5db; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 8 12 8 12; -fx-font-size: 14px; -fx-font-weight: bold;");

        searchButton = createIconButton(SEARCH_ICON, "#3b82f6");
        clearButton = createIconButton(CLEAR_ICON, "#ef4444");
        clearButton.setVisible(false);

        // Layout
        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(10));
        getChildren().addAll(searchButton, searchField, clearButton);

        // Event handlers
        searchField.textProperty().addListener((obs, oldText, newText) -> handleTextChange(newText));
        clearButton.setOnAction(e -> clearSearch());

        // Initial search with empty query
        performSearch("");
    }

    private Button createIconButton(String iconText, String color) {
        Button button = new Button(iconText);
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 50%%; -fx-min-width: 30px; -fx-min-height: 30px; -fx-max-width: 30px; -fx-max-height: 30px;", color));
        button.setOnMouseEntered(e -> button.setStyle(String.format("-fx-background-color: derive(%s, -20%%); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 50%%; -fx-min-width: 30px; -fx-min-height: 30px; -fx-max-width: 30px; -fx-max-height: 30px;", color)));
        button.setOnMouseExited(e -> button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 50%%; -fx-min-width: 30px; -fx-min-height: 30px; -fx-max-width: 30px; -fx-max-height: 30px;", color)));
        return button;
    }

    private void handleTextChange(String newText) {
        clearButton.setVisible(!newText.isEmpty());

        // Cancel previous debounce task
        debounceExecutor.shutdownNow();
        debounceExecutor = Executors.newSingleThreadScheduledExecutor();

        // Debounce search by 300ms
        debounceExecutor.schedule(() -> Platform.runLater(() -> performSearch(newText)), 300, TimeUnit.MILLISECONDS);
    }

    private void performSearch(String query) {
        List<T> allItems = dataSupplier.get();
        List<T> filteredItems;

        if (query == null || query.trim().isEmpty()) {
            filteredItems = allItems;
        } else {
            String lowerQuery = query.toLowerCase();
            filteredItems = allItems.stream()
                    .filter(item -> toStringFunction.apply(item).toLowerCase().contains(lowerQuery))
                    .collect(Collectors.toList());
        }

        // Animate update
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), this);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.8);
        fadeOut.setOnFinished(e -> {
            onSearchUpdate.accept(filteredItems);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(100), this);
            fadeIn.setFromValue(0.8);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void clearSearch() {
        searchField.clear();
        clearButton.setVisible(false);
        performSearch("");
    }

    /**
     * Sets the focus to the search field.
     */
    public void requestFocus() {
        searchField.requestFocus();
    }

    /**
     * Gets the current search query.
     * @return The current text in the search field.
     */
    public String getQuery() {
        return searchField.getText();
    }

    /**
     * Sets the search query programmatically.
     * @param query The query to set.
     */
    public void setQuery(String query) {
        searchField.setText(query);
    }

    /**
     * Gets the prompt text of the search field.
     * @return The prompt text.
     */
    public String getPromptText() {
        return searchField.getPromptText();
    }
}
