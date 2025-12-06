package com.library.controller;

import com.library.dao.*;
import com.library.model.*;
import com.library.util.*;
import com.library.util.UILayoutConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.application.Platform;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Advanced SearchController provides Google/YouTube-like search functionality.
 * Features unified results view, relevance scoring, advanced filters, and modern UI.
 */
public class SearchController {
    private Stage stage;
    private TextField searchField;
    private ComboBox<SearchResult.EntityType> entityFilter;
    private DatePicker dateFromPicker;
    private DatePicker dateToPicker;
    private ListView<SearchResult> resultsListView;
    private ObservableList<SearchResult> allResults = FXCollections.observableArrayList();
    private ObservableList<SearchResult> filteredResults = FXCollections.observableArrayList();
    private ObservableList<String> suggestions = FXCollections.observableArrayList();

    // Search enhancements
    private ScheduledExecutorService debounceExecutor = Executors.newSingleThreadScheduledExecutor();
    private Map<String, List<String>> suggestionCache = new HashMap<>();
    private ProgressIndicator loadingIndicator;
    private Label resultsCountLabel;
    private boolean isLoading = false;

    public SearchController(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle(UILayoutConstants.FULL_BACKGROUND_STYLE);

        // Header
        HBox header = createHeader();
        mainLayout.setTop(header);

        // Search and filters
        VBox searchSection = createSearchSection();
        mainLayout.setCenter(searchSection);

        // Results area
        VBox resultsSection = createResultsSection();
        mainLayout.setBottom(resultsSection);

        // Load initial data
        loadAllData();

        return new Scene(mainLayout, 1200, 800);
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95);");

        Label title = new Label("Advanced Search");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-padding: 10 20;");
        backBtn.setOnAction(e -> UIUtil.switchScene(stage, new DashboardController(stage).getScene()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, backBtn);
        return header;
    }

    private VBox createSearchSection() {
        VBox searchSection = new VBox(15);
        searchSection.setPadding(new Insets(20));
        searchSection.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 10;");

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search books, students, faculty, categories...");
        searchField.setPrefWidth(600);
        searchField.setStyle("-fx-font-size: 16px; -fx-padding: 12; -fx-background-radius: 25; -fx-border-radius: 25;");

        // Filters row
        HBox filtersRow = new HBox(15);
        filtersRow.setAlignment(Pos.CENTER_LEFT);

        Label filterLabel = new Label("Filter by:");
        filterLabel.setStyle("-fx-font-weight: bold;");

        entityFilter = new ComboBox<>();
        entityFilter.setPromptText("All Types");
        entityFilter.getItems().addAll(SearchResult.EntityType.values());
        entityFilter.setOnAction(e -> applyFilters());

        Label dateLabel = new Label("Date Range:");
        dateFromPicker = new DatePicker();
        dateFromPicker.setPromptText("From");
        dateToPicker = new DatePicker();
        dateToPicker.setPromptText("To");

        Button clearFiltersBtn = new Button("Clear Filters");
        clearFiltersBtn.setOnAction(e -> clearFilters());

        filtersRow.getChildren().addAll(filterLabel, entityFilter, dateLabel, dateFromPicker, dateToPicker, clearFiltersBtn);

        // Search field with debounced listener
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            debounceExecutor.shutdownNow();
            debounceExecutor = Executors.newSingleThreadScheduledExecutor();
            debounceExecutor.schedule(() -> Platform.runLater(() -> performSearch(newText)), 300, TimeUnit.MILLISECONDS);
        });

        searchSection.getChildren().addAll(searchField, filtersRow);
        return searchSection;
    }

    private VBox createResultsSection() {
        VBox resultsSection = new VBox(10);
        resultsSection.setPadding(new Insets(20));
        resultsSection.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 10;");

        // Results header
        HBox resultsHeader = new HBox();
        resultsHeader.setAlignment(Pos.CENTER_LEFT);

        resultsCountLabel = new Label("No results");
        resultsCountLabel.setStyle("-fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(20, 20);
        loadingIndicator.setVisible(false);

        resultsHeader.getChildren().addAll(resultsCountLabel, spacer, loadingIndicator);

        // Results list
        resultsListView = new ListView<>();
        resultsListView.setItems(filteredResults);
        resultsListView.setPrefHeight(400);
        resultsListView.setCellFactory(listView -> new SearchResultCell());
        resultsListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                SearchResult selected = resultsListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showDetails(selected);
                }
            }
        });

        resultsSection.getChildren().addAll(resultsHeader, resultsListView);
        return resultsSection;
    }

    private void loadAllData() {
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(true);
                    isLoading = true;
                });

                allResults.clear();

                // Load books
                try {
                    BookDAO bookDAO = new BookDAO();
                    List<Book> books = bookDAO.getAllBooks();
                    for (Book book : books) {
                        allResults.add(new SearchResult(
                            SearchResult.EntityType.BOOK,
                            book.getName(),
                            "by " + book.getAuthor(),
                            "Barcode: " + book.getBarcode() + " | Category: " + (book.getCategory() != null ? book.getCategory().getName() : "N/A"),
                            1.0,
                            book
                        ));
                    }
                } catch (Exception e) {
                    System.err.println("Error loading books: " + e.getMessage());
                }

                // Load students
                try {
                    StudentDAO studentDAO = new StudentDAO();
                    List<Student> students = studentDAO.getAllStudents(true); // Load active students
                    for (Student student : students) {
                        allResults.add(new SearchResult(
                            SearchResult.EntityType.STUDENT,
                            student.getName(),
                            "ID: " + student.getStudentId(),
                            "Email: " + student.getEmail() + " | Phone: " + student.getMobile(),
                            1.0,
                            student
                        ));
                    }
                } catch (Exception e) {
                    System.err.println("Error loading students: " + e.getMessage());
                }

                // Load faculty
                try {
                    FacultyDAO facultyDAO = new FacultyDAO();
                    List<Faculty> faculty = facultyDAO.getAllFaculty(true); // Load active faculty
                    for (Faculty fac : faculty) {
                        allResults.add(new SearchResult(
                            SearchResult.EntityType.FACULTY,
                            fac.getName(),
                            "ID: " + fac.getFacultyId(),
                            "Email: " + fac.getEmail() + " | Phone: " + fac.getMobile(),
                            1.0,
                            fac
                        ));
                    }
                } catch (Exception e) {
                    System.err.println("Error loading faculty: " + e.getMessage());
                }

                // Load categories
                try {
                    CategoryDAO categoryDAO = new CategoryDAO();
                    List<Category> categories = categoryDAO.getAllCategories();
                    for (Category cat : categories) {
                        allResults.add(new SearchResult(
                            SearchResult.EntityType.CATEGORY,
                            cat.getName(),
                            "ID: " + cat.getId(),
                            "Book category",
                            1.0,
                            cat
                        ));
                    }
                } catch (Exception e) {
                    System.err.println("Error loading categories: " + e.getMessage());
                }

                Platform.runLater(() -> {
                    filteredResults.setAll(allResults);
                    updateResultsCount();
                    loadingIndicator.setVisible(false);
                    isLoading = false;
                });

                return null;
            }
        };

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    private void performSearch(String query) {
        if (isLoading) return;

        String lowerQuery = query.toLowerCase().trim();

        List<SearchResult> results = allResults.stream()
            .map(result -> {
                double score = calculateRelevanceScore(result, lowerQuery);
                result.setRelevanceScore(score);
                if (score > 0) {
                    result.setHighlightedTitle(highlightText(result.getTitle(), lowerQuery));
                    result.setHighlightedSubtitle(highlightText(result.getSubtitle(), lowerQuery));
                }
                return result;
            })
            .filter(result -> result.getRelevanceScore() > 0)
            .sorted()
            .collect(Collectors.toList());

        filteredResults.setAll(results);
        applyFilters();
    }

    private double calculateRelevanceScore(SearchResult result, String query) {
        if (query.isEmpty()) return 1.0;

        String title = result.getTitle().toLowerCase();
        String subtitle = result.getSubtitle().toLowerCase();
        String details = result.getDetails().toLowerCase();

        double score = 0;

        // Exact matches get highest score
        if (title.equals(query)) score += 10;
        if (subtitle.contains(query)) score += 5;
        if (title.contains(query)) score += 3;
        if (details.contains(query)) score += 1;

        // Word matches
        String[] queryWords = query.split("\\s+");
        for (String word : queryWords) {
            if (title.contains(word)) score += 2;
            if (subtitle.contains(word)) score += 1.5;
            if (details.contains(word)) score += 0.5;
        }

        return score;
    }

    private String highlightText(String text, String query) {
        if (query.isEmpty() || text == null) return text;

        String lowerText = text.toLowerCase();
        String lowerQuery = query.toLowerCase();

        int index = lowerText.indexOf(lowerQuery);
        if (index == -1) return text;

        String before = text.substring(0, index);
        String match = text.substring(index, index + query.length());
        String after = text.substring(index + query.length());

        return before + "**" + match + "**" + after;
    }

    private void applyFilters() {
        List<SearchResult> filtered = new ArrayList<>(filteredResults);

        // Entity type filter
        SearchResult.EntityType selectedType = entityFilter.getValue();
        if (selectedType != null) {
            filtered = filtered.stream()
                .filter(result -> result.getEntityType() == selectedType)
                .collect(Collectors.toList());
        }

        // Date filters (if applicable - would need date fields in models)
        // For now, skip date filtering as models don't have date fields

        filteredResults.setAll(filtered);
        updateResultsCount();
    }

    private void clearFilters() {
        entityFilter.setValue(null);
        dateFromPicker.setValue(null);
        dateToPicker.setValue(null);
        filteredResults.setAll(allResults.stream()
            .filter(result -> result.getRelevanceScore() > 0)
            .sorted()
            .collect(Collectors.toList()));
        updateResultsCount();
    }

    private void updateResultsCount() {
        int count = filteredResults.size();
        resultsCountLabel.setText(count + " result" + (count != 1 ? "s" : "") + " found");
    }

    private void showDetails(SearchResult result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Details - " + result.getEntityType().getDisplayName());
        alert.setHeaderText(result.getTitle());
        alert.setContentText(result.getSubtitle() + "\n\n" + result.getDetails());
        alert.showAndWait();
    }

    // Custom ListCell for search results
    private static class SearchResultCell extends ListCell<SearchResult> {
        private VBox content;
        private Label titleLabel;
        private Label subtitleLabel;
        private Label detailsLabel;
        private Label iconLabel;

        public SearchResultCell() {
            content = new VBox(5);
            content.setPadding(new Insets(10));

            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);

            iconLabel = new Label();
            iconLabel.setStyle("-fx-font-size: 18px;");

            titleLabel = new Label();
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            header.getChildren().addAll(iconLabel, titleLabel);

            subtitleLabel = new Label();
            subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

            detailsLabel = new Label();
            detailsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #9ca3af;");
            detailsLabel.setWrapText(true);

            content.getChildren().addAll(header, subtitleLabel, detailsLabel);

            setGraphic(content);
        }

        @Override
        protected void updateItem(SearchResult item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
            } else {
                iconLabel.setText(item.getEntityType().getIcon());
                titleLabel.setText(item.getHighlightedTitle());
                subtitleLabel.setText(item.getHighlightedSubtitle());
                detailsLabel.setText(item.getDetails());

                // Style based on relevance
                if (item.getRelevanceScore() >= 5) {
                    content.setStyle("-fx-background-color: #fef3c7; -fx-background-radius: 5;");
                } else {
                    content.setStyle("-fx-background-color: transparent;");
                }

                setGraphic(content);
            }
        }
    }
}
