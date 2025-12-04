package com.library.controller;

import com.library.dao.BookDAO;
import com.library.dao.StudentDAO;
import com.library.dao.FacultyDAO;
import com.library.dao.CategoryDAO;
import com.library.dao.IssuedBookDAO;
import com.library.model.Book;
import com.library.model.Student;
import com.library.model.Faculty;
import com.library.model.Category;
import com.library.model.Session;
import com.library.util.UIUtil;
import com.library.util.UILayoutConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SearchController provides a centralized search interface for books, students, faculty, categories, and issued books.
 * Features tabbed interface for different entity types with search functionality.
 */
public class SearchController {
    private Stage stage;
    private TabPane tabPane;
    private ComboBox<String> searchField;
    private ObservableList<String> suggestions = FXCollections.observableArrayList();
    private ObservableList<Book> bookResults = FXCollections.observableArrayList();
    private ObservableList<Student> studentResults = FXCollections.observableArrayList();
    private ObservableList<Faculty> facultyResults = FXCollections.observableArrayList();
    private ObservableList<Category> categoryResults = FXCollections.observableArrayList();
    private ObservableList<String[]> issuedBookResults = FXCollections.observableArrayList();

    public SearchController(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        if (Session.getLoggedInAdmin() == null) {
            UIUtil.switchScene(stage, new LoginController(stage).getScene());
            return null;
        }

        VBox contentBox = new VBox(20);
        contentBox.setPadding(UILayoutConstants.PADDING);
        contentBox.setAlignment(Pos.TOP_CENTER);

        Label heading = new Label("🔍 Centralized Search Hub");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        // --- Search Bar with Suggestions ---
        searchField = new ComboBox<>();
        searchField.setEditable(true);
        searchField.setPromptText("Enter search term...");
        searchField.setPrefWidth(500);
        searchField.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-border-color: #d1d5db; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 4 8 4 8;");
        searchField.setItems(suggestions);

        searchField.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isEmpty()) {
                updateSuggestions(newText);
                searchField.show(); // Show suggestions as user types
            } else {
                suggestions.clear();
                searchField.hide(); // Hide when input is empty
            }
        });

        searchField.getEditor().setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                performSearch();
                searchField.hide(); // Hide suggestions after search
            }
        });

        Button searchBtn = UIUtil.createStyledButton("Search", "#3b82f6", "#2563eb");
        searchBtn.setOnAction(e -> performSearch());

        HBox searchBox = new HBox(10, new Label("🔎"), searchField, searchBtn);
        searchBox.setAlignment(Pos.CENTER);
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-tab-min-height: 40px; -fx-tab-max-height: 40px;");

        // Books Tab
        Tab booksTab = new Tab("📚 Books");
        TableView<Book> booksTable = createBooksTable();
        booksTab.setContent(booksTable);

        // Students Tab
        Tab studentsTab = new Tab("👨‍🎓 Students");
        TableView<Student> studentsTable = createStudentsTable();
        studentsTab.setContent(studentsTable);

        // Faculty Tab
        Tab facultyTab = new Tab("👨‍🏫 Faculty");
        TableView<Faculty> facultyTable = createFacultyTable();
        facultyTab.setContent(facultyTable);

        // Categories Tab
        Tab categoriesTab = new Tab("📂 Categories");
        TableView<Category> categoriesTable = createCategoriesTable();
        categoriesTab.setContent(categoriesTable);

        // Issued Books Tab
        Tab issuedBooksTab = new Tab("📖 Issued Books");
        TableView<String[]> issuedBooksTable = createIssuedBooksTable();
        issuedBooksTab.setContent(issuedBooksTable);

        tabPane.getTabs().addAll(booksTab, studentsTab, facultyTab, categoriesTab, issuedBooksTab);

        // Wrap TabPane in a styled container for the card effect
        VBox tabContainer = new VBox(tabPane);
        tabContainer.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 4);");
        VBox.setVgrow(tabContainer, Priority.ALWAYS);

        Button backBtn = UIUtil.createStyledButton("Back to Dashboard", "#6b7280", "#374151");
        backBtn.setOnAction(e -> UIUtil.switchScene(stage, new DashboardController(stage).getScene()));

        contentBox.getChildren().addAll(heading, searchBox, tabContainer, backBtn);
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        // Initial search to populate tables
        performSearch();

        return UIUtil.createScene(null, contentBox);
    }

    private void performSearch() {
        String query = searchField.getEditor().getText().trim().toLowerCase();

        try {
            // Search Books
            List<Book> books = new BookDAO().searchBooks(query);
            bookResults.setAll(books);

            // Search Students
            List<Student> students = new StudentDAO().searchStudents(query);
            studentResults.setAll(students);

            // Search Faculty
            List<Faculty> faculty = new FacultyDAO().searchFaculty(query);
            facultyResults.setAll(faculty);

            // Search Categories
            List<Category> categories = new CategoryDAO().searchCategories(query);
            categoryResults.setAll(categories);

            // Search Issued Books
            List<String[]> issuedBooks = new IssuedBookDAO().searchIssuedBooks(query);
            issuedBookResults.setAll(issuedBooks);
        } catch (SQLException e) {
            UIUtil.showAlert("Error", "Database search failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private TableView<Book> createBooksTable() {
        TableView<Book> table = new TableView<>(bookResults);
        table.setPrefHeight(400);
        styleTable(table);

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        titleCol.setPrefWidth(200);

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getAuthor()));
        authorCol.setPrefWidth(150);

        TableColumn<Book, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getBarcode()));
        barcodeCol.setPrefWidth(120);

        TableColumn<Book, String> categoryCol = new TableColumn<>("Category");
        // This now correctly displays the category name instead of just the ID.
        categoryCol.setCellValueFactory(cell -> {
            Book book = cell.getValue();
            if (book != null && book.getCategory() != null) {
                return new javafx.beans.property.SimpleStringProperty(book.getCategory().getName());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A"); // Handle null category
        });
        categoryCol.setPrefWidth(100);

        TableColumn<Book, Integer> availableCol = new TableColumn<>("Quantity");
        availableCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        availableCol.setPrefWidth(80);

        table.getColumns().setAll(titleCol, authorCol, barcodeCol, categoryCol, availableCol);
        return table;
    }

    private TableView<Student> createStudentsTable() {
        TableView<Student> table = new TableView<>(studentResults);
        table.setPrefHeight(400);
        styleTable(table);

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        TableColumn<Student, String> mobileCol = new TableColumn<>("Mobile");
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        mobileCol.setPrefWidth(120);

        TableColumn<Student, String> studentIdCol = new TableColumn<>("Student ID");
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        studentIdCol.setPrefWidth(120);

        TableColumn<Student, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("course"));
        courseCol.setPrefWidth(100);

        table.getColumns().setAll(nameCol, emailCol, mobileCol, studentIdCol, courseCol);
        return table;
    }

    private TableView<Faculty> createFacultyTable() {
        TableView<Faculty> table = new TableView<>(facultyResults);
        table.setPrefHeight(400);
        styleTable(table);

        TableColumn<Faculty, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);

        TableColumn<Faculty, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        TableColumn<Faculty, String> mobileCol = new TableColumn<>("Mobile");
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        mobileCol.setPrefWidth(120);

        TableColumn<Faculty, String> facultyIdCol = new TableColumn<>("Faculty ID");
        facultyIdCol.setCellValueFactory(new PropertyValueFactory<>("facultyId"));
        facultyIdCol.setPrefWidth(120);

        table.getColumns().setAll(nameCol, emailCol, mobileCol, facultyIdCol);
        return table;
    }

    private TableView<Category> createCategoriesTable() {
        TableView<Category> table = new TableView<>(categoryResults);
        table.setPrefHeight(400);
        styleTable(table);

        TableColumn<Category, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(80);

        TableColumn<Category, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        table.getColumns().setAll(idCol, nameCol);
        return table;
    }

    private TableView<String[]> createIssuedBooksTable() {
        TableView<String[]> table = new TableView<>(issuedBookResults);
        table.setPrefHeight(400);
        styleTable(table);

        TableColumn<String[], String> bookNameCol = new TableColumn<>("Book Name");
        bookNameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()[0]));
        bookNameCol.setPrefWidth(200);

        TableColumn<String[], String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()[1]));
        barcodeCol.setPrefWidth(120);

        TableColumn<String[], String> userNameCol = new TableColumn<>("User");
        userNameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()[2]));
        userNameCol.setPrefWidth(150);

        TableColumn<String[], String> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()[3]));
        userIdCol.setPrefWidth(120);

        table.getColumns().setAll(bookNameCol, barcodeCol, userNameCol, userIdCol);
        return table;
    }

    private void updateSuggestions(String input) {
        if (input == null || input.trim().isEmpty()) {
            suggestions.clear();
            return;
        }

        String query = input.trim().toLowerCase();
        suggestions.clear();

        try {
            // Get suggestions from books
            List<Book> books = new BookDAO().searchBooks(query);
            suggestions.addAll(books.stream()
                .map(Book::getName)
                .distinct()
                .limit(5)
                .collect(Collectors.toList()));

            // Get suggestions from students
            List<Student> students = new StudentDAO().searchStudents(query);
            suggestions.addAll(students.stream()
                .map(Student::getName)
                .distinct()
                .limit(5)
                .collect(Collectors.toList()));

            // Get suggestions from faculty
            List<Faculty> faculty = new FacultyDAO().searchFaculty(query);
            suggestions.addAll(faculty.stream()
                .map(Faculty::getName)
                .distinct()
                .limit(5)
                .collect(Collectors.toList()));

            // Get suggestions from categories
            List<Category> categories = new CategoryDAO().searchCategories(query);
            suggestions.addAll(categories.stream()
                .map(Category::getName)
                .distinct()
                .limit(5)
                .collect(Collectors.toList()));

            // Remove duplicates and limit total suggestions
            suggestions.setAll(suggestions.stream()
                .distinct()
                .limit(10)
                .collect(Collectors.toList()));

        } catch (SQLException e) {
            // Silently handle database errors for suggestions
            suggestions.clear();
        }
    }

    /**
     * Applies a consistent style to all TableView instances.
     * @param table The table to be styled.
     */
    private void styleTable(TableView<?> table) {
        table.setStyle("-fx-selection-bar: #a5d8ff; -fx-selection-bar-non-focused: #d3e9ff; -fx-border-color: #e5e7eb; -fx-border-radius: 8;");
        VBox.setVgrow(table, Priority.ALWAYS);
    }
}
