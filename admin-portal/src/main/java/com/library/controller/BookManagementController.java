
package com.library.controller;

import com.library.dao.BookDAO;
import com.library.dao.CategoryDAO;
import com.library.model.Book;
import com.library.model.Category;
import com.library.model.Session;
import com.library.util.UIUtil;
import com.library.util.UILayoutConstants;
import animatefx.animation.Shake;
import animatefx.animation.BounceIn;
import animatefx.animation.Pulse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

/**
 * BookManagementController handles the book management scene for the Library Management System.
 * It allows admins to add, edit, delete books and manage categories.
 */
public class BookManagementController {
    private Stage stage;
    private BookDAO bookDAO = new BookDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private ObservableList<Book> books = FXCollections.observableArrayList();
    private ObservableList<Book> filteredBooks = FXCollections.observableArrayList();
    private ObservableList<Category> categories = FXCollections.observableArrayList();
    private TableView<Book> bookTable = new TableView<>();
    private ListView<Category> categoryListView = new ListView<>();
    private TextField searchField = new TextField();
    private TextField titleField = new TextField();
    private TextField authorField = new TextField();
    private TextField isbnField = new TextField();
    private TextField quantityField = new TextField();
    private ComboBox<Category> categoryComboBox = new ComboBox<>();
    private TextField categoryNameField = new TextField();

    private VBox bookForm;

    private ObservableList<Category> filteredCategories = FXCollections.observableArrayList();

    // Add drag selection for range (like desktop file selection)
    private int dragStartIndex = -1;

    public BookManagementController(Stage stage) {
        this.stage = stage;
        loadBooks();
        loadCategories();
        searchField.setPromptText("Search books...");
        searchField.setStyle("-fx-background-radius: 8; -fx-padding: 8 12 8 12; -fx-pref-width: 300; -fx-font-weight: bold;");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterBooks(newValue));

        filteredCategories.addAll(categories);
        categoryComboBox.setItems(filteredCategories);
        categoryComboBox.setEditable(true);

        // Filter as user types in the combo box editor
        categoryComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            filterCategoryList(newVal);
        });

        // When the combo box is shown, reset the filter to show all items
        // that match the current text
        categoryComboBox.setOnShowing(e -> {
            String currentText = categoryComboBox.getEditor().getText();
            filterCategoryList(currentText);
        });
    }

    public Scene getScene() {
        if (Session.getLoggedInAdmin() == null) {
            UIUtil.switchScene(stage, new LoginController(stage).getScene());
            return null;
        }

        BorderPane mainLayout = new BorderPane();

        // Top bar with back button
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(0, 0, 10, 0));

        Button backBtn = new Button("⬅ Back to Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #6b7280; -fx-text-fill: #6b7280; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;");
        backBtn.setOnAction(e -> UIUtil.switchScene(stage, new DashboardController(stage).getScene()));
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #374151; -fx-text-fill: #374151; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #6b7280; -fx-text-fill: #6b7280; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;"));

        topBar.getChildren().add(backBtn);

        // Center layout
        HBox centerLayout = new HBox(20);
        centerLayout.setPadding(UILayoutConstants.PADDING);
        centerLayout.setAlignment(UILayoutConstants.CENTER_ALIGNMENT);

        // Left side: Book form
        bookForm = new VBox(15);
        bookForm.setPadding(UILayoutConstants.PADDING);
        bookForm.setAlignment(Pos.TOP_LEFT);
        bookForm.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 4); -fx-pref-width: 400;");

        Label formTitle = new Label("Book Details");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        titleField.setPromptText("Title");
        authorField.setPromptText("Author");
        isbnField.setPromptText("Barcode");
        quantityField.setPromptText("Quantity");
        categoryComboBox.setPromptText("Select Category");
        categoryComboBox.setConverter(new CategoryStringConverter());

        Button addBookBtn = new Button("Add Book");
        addBookBtn.setOnAction(e -> addBook());

        Button updateBookBtn = new Button("Update Book");
        updateBookBtn.setOnAction(e -> updateBook());

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> clearForm());

        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);
        buttonGrid.add(addBookBtn, 0, 0);
        buttonGrid.add(updateBookBtn, 1, 0);
        buttonGrid.add(clearBtn, 1, 1);

        addBookBtn.setMaxWidth(Double.MAX_VALUE);
        updateBookBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setMaxWidth(Double.MAX_VALUE);

        bookForm.getChildren().addAll(formTitle, titleField, authorField, isbnField, quantityField, categoryComboBox, buttonGrid);

        // Right side: Book table and categories
        BorderPane rightSide = new BorderPane();
        rightSide.setPadding(UILayoutConstants.PADDING);
        rightSide.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 4); -fx-pref-width: 600;");
        rightSide.setFocusTraversable(false);

        Label tableTitle = new Label("Books");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        setupBookTable();
        setupBookTableContextMenu();
        bookTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        bookTable.setItems(books);
        bookTable.setOnMouseClicked(e -> {
            // bookTable.requestFocus();  // Removed to fix focus issue and enable proper multiple selection
            if (e.getClickCount() == 1 && bookTable.getSelectionModel().getSelectedItems().size() == 1) {
                selectBook();
            }
        });

        // Add drag selection for range (like desktop file selection)
        bookTable.setOnMousePressed(e -> {
            if (e.getTarget() instanceof TableCell) {
                TableCell cell = (TableCell) e.getTarget();
                int row = cell.getIndex();
                if (e.isSecondaryButtonDown()) {
                    // Right-click: select the item if not already selected (for context menu)
                    if (!bookTable.getSelectionModel().isSelected(row)) {
                        bookTable.getSelectionModel().select(row);
                    }
                } else if (!e.isControlDown() && !e.isShiftDown()) {
                    // Left-click without modifiers: clear and select
                    bookTable.getSelectionModel().clearSelection();
                    bookTable.getSelectionModel().select(row);
                }
                dragStartIndex = row;
            }
        });

        bookTable.setOnMouseDragged(e -> {
            if (dragStartIndex != -1 && e.getTarget() instanceof TableCell) {
                TableCell cell = (TableCell) e.getTarget();
                int currentRow = cell.getIndex();
                bookTable.getSelectionModel().selectRange(Math.min(dragStartIndex, currentRow), Math.max(dragStartIndex, currentRow) + 1);
            }
        });

        bookTable.setOnMouseReleased(e -> {
            dragStartIndex = -1;  // Reset
        });

        Label categoryTitle = new Label("Manage Categories");
        categoryTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        categoryListView.setItems(categories);
        categoryListView.setPrefHeight(150);
        categoryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setupCategoryContextMenu();

        // Ensure context menu works properly by selecting item on right-click
        categoryListView.setOnContextMenuRequested(e -> {
            // Find the item under the mouse cursor
            int index = categoryListView.getSelectionModel().getSelectedIndex();
            if (index == -1) {
                // No item selected, try to find item at mouse position
                double y = e.getY();
                int itemHeight = 24; // Approximate item height
                int clickedIndex = (int) (y / itemHeight);
                if (clickedIndex >= 0 && clickedIndex < categories.size()) {
                    categoryListView.getSelectionModel().select(clickedIndex);
                }
            }
        });

        // Add drag selection for range (like desktop file selection) for categories
        categoryListView.setOnMousePressed(e -> {
            if (e.getTarget() instanceof ListCell) {
                ListCell cell = (ListCell) e.getTarget();
                int row = cell.getIndex();
                if (e.isSecondaryButtonDown()) {
                    // Right-click: select the item if not already selected (for context menu)
                    if (!categoryListView.getSelectionModel().isSelected(row)) {
                        categoryListView.getSelectionModel().select(row);
                    }
                } else if (!e.isControlDown() && !e.isShiftDown()) {
                    // Left-click without modifiers: clear and select
                    categoryListView.getSelectionModel().clearSelection();
                    categoryListView.getSelectionModel().select(row);
                }
                dragStartIndex = row;
            }
        });

        categoryListView.setOnMouseDragged(e -> {
            if (dragStartIndex != -1 && e.getTarget() instanceof ListCell) {
                ListCell cell = (ListCell) e.getTarget();
                int currentRow = cell.getIndex();
                categoryListView.getSelectionModel().selectRange(Math.min(dragStartIndex, currentRow), Math.max(dragStartIndex, currentRow) + 1);
            }
        });

        categoryListView.setOnMouseReleased(e -> {
            dragStartIndex = -1;  // Reset
        });

        HBox categoryControls = new HBox(10);
        categoryControls.setAlignment(Pos.CENTER_LEFT);
        categoryNameField.setPromptText("Category Name");
        Button addCategoryBtn = new Button("Add Category");
        addCategoryBtn.setOnAction(e -> addCategory());

        categoryControls.getChildren().addAll(categoryNameField, addCategoryBtn);

        VBox topBox = new VBox(15);
        topBox.getChildren().addAll(tableTitle, searchField);

        VBox centerBox = new VBox(15);
        centerBox.getChildren().addAll(bookTable, categoryTitle, categoryListView, categoryControls);

        rightSide.setTop(topBox);
        rightSide.setCenter(centerBox);
        centerLayout.getChildren().addAll(bookForm, rightSide);

        // Apply consistent button styles
        UIUtil.setButtonStyle(addBookBtn, "#10b981", "#059669");
        UIUtil.setButtonStyle(updateBookBtn, "#f59e0b", "#f97316");
        UIUtil.setButtonStyle(clearBtn, "#6b7280", "#4b5563");
        UIUtil.setButtonStyle(addCategoryBtn, "#3b82f6", "#2563eb");

        return UIUtil.createScene(topBar, centerLayout);
    }


    private void setupBookTable() {
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        titleCol.setPrefWidth(150);

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(120);

        TableColumn<Book, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        barcodeCol.setPrefWidth(120);

        TableColumn<Book, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory().getName()));
        categoryCol.setPrefWidth(120);

        TableColumn<Book, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(80);

        bookTable.getColumns().setAll(titleCol, authorCol, barcodeCol, categoryCol, quantityCol);
        bookTable.setPrefHeight(300);
    }

    private void setupCategoryContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");
        editItem.setOnAction(e -> editCategory());
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> deleteCategory());
        contextMenu.getItems().addAll(editItem, deleteItem);
        categoryListView.setContextMenu(contextMenu);
    }

    private void loadBooks() {
        books.clear();
        books.addAll(bookDAO.getAllBooks());
    }

    private void loadCategories() {
        categories.clear();
        categories.addAll(categoryDAO.getAllCategories());
        // Update filteredCategories as well to keep combo box list up to date
        filteredCategories.clear();
        filteredCategories.addAll(categories);
    }

    private void filterCategoryList(String filter) {
        filteredCategories.clear();
        if (filter == null || filter.isEmpty()) {
            filteredCategories.addAll(categories);
        } else {
            String lowerCaseFilter = filter.toLowerCase();
            categories.stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerCaseFilter))
                .forEach(filteredCategories::add);
        }
    }

    private void addBook() {
        try {
            String title = titleField.getText();
            String author = authorField.getText();
            String barcode = isbnField.getText();
            int quantity = Integer.parseInt(quantityField.getText());

            Category selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();

            if (selectedCategory == null) {
                UIUtil.showError("Invalid Category", "Please select a valid category from the list.");
                new Shake(categoryComboBox).play();
                return;
            }

            if (title.isEmpty() || author.isEmpty() || barcode.isEmpty() || selectedCategory == null) {
                UIUtil.showError("Missing Information", "Please fill all fields and select a category.");
                new Shake(bookForm).play();
                return;
            }

            // Validate the category exists in DB
            Category dbCategory = categoryDAO.getCategoryById(selectedCategory.getId());
            if (dbCategory == null) {
                UIUtil.showError("Invalid Category", "Selected category does not exist. Please refresh and try again.");
                new Shake(categoryComboBox).play();
                return;
            }

            Book book = new Book(0, title, author, barcode, selectedCategory.getId(), quantity);
            boolean success = bookDAO.addBook(book);

            if (success) {
                loadBooks(); // Refresh table on success
                clearForm();
                UIUtil.showSuccess("Success", "Book added successfully!");
                new Pulse(bookTable).play();
            } else {
                UIUtil.showError("Database Error", "Failed to add book. A book with the same barcode might already exist.");
                new Shake(bookForm).play();
            }
        } catch (NumberFormatException e) {
            UIUtil.showError("Invalid Input", "Quantity must be a valid number.");
            new Shake(quantityField).play();
        } catch (Exception e) {
            UIUtil.showError("Unexpected Error", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateBook() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtil.showWarning("No Selection", "Please select a book from the table to update.");
            return;
        }

        try {
            String title = titleField.getText();
            String author = authorField.getText();
            String isbn = isbnField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            Category selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();

            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || selectedCategory == null) {
                UIUtil.showError("Missing Information", "Please fill all fields and select a category.");
                new Shake(bookForm).play();
                return;
            }

            selected.setName(title);
            selected.setAuthor(author);
            selected.setBarcode(isbn);
            selected.setCategoryId(selectedCategory.getId());
            selected.setQuantity(quantity);

            if (bookDAO.updateBook(selected)) {
                loadBooks();
                clearForm();
                UIUtil.showSuccess("Success", "Book updated successfully!");
                new Pulse(bookTable).play();
            } else {
                UIUtil.showError("Database Error", "Failed to update the book.");
            }
        } catch (NumberFormatException e) {
            UIUtil.showError("Invalid Input", "Quantity must be a valid number.");
            new Shake(quantityField).play();
        }
    }

    private void selectBook() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            titleField.setText(selected.getName());
            authorField.setText(selected.getAuthor());
            isbnField.setText(selected.getBarcode());
            quantityField.setText(String.valueOf(selected.getQuantity()));
            // Find and select the category in the ComboBox
            for (Category category : categories) {
                if (category.getId() == selected.getCategoryId()) {
                    categoryComboBox.setValue(category);
                    break;
                }
            }
        }
    }

    private void clearForm() {
        titleField.clear();
        authorField.clear();
        isbnField.clear();
        quantityField.clear();
        categoryComboBox.setValue(null);
        bookTable.getSelectionModel().clearSelection();
    }

    private void addCategory() {
        String name = categoryNameField.getText();
        if (name.isEmpty()) {
            UIUtil.showWarning("Input Required", "Please enter a category name.");
            new Shake(categoryNameField).play();
            return;
        }

        if (categoryDAO.addCategory(name)) {
            loadCategories();
            categoryNameField.clear();
            UIUtil.showSuccess("Success", "Category '" + name + "' added successfully.");
            new Pulse(categoryListView).play();
        } else {
            UIUtil.showError("Database Error", "Failed to add category. It might already exist.");
        }
    }

    private void editCategory() {
        Category selected = categoryListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtil.showWarning("No Selection", "Please select a category from the list to edit.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getName());
        dialog.setTitle("Edit Category");
        dialog.setHeaderText("Edit Category Name");
        dialog.setContentText("Name:");
        dialog.showAndWait().ifPresent(name -> {
            if (!name.isEmpty()) {
                if (!name.equals(selected.getName())) {
                    selected.setName(name);
                    if (categoryDAO.updateCategory(selected)) {
                        loadCategories();
                        UIUtil.showSuccess("Success", "Category updated successfully.");
                    } else {
                        UIUtil.showError("Database Error", "Failed to update category.");
                    }
                }
            }
        });
    }

    private void setupBookTableContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete Selected");
        deleteItem.setOnAction(e -> deleteBook());
        contextMenu.getItems().add(deleteItem);
        bookTable.setContextMenu(contextMenu);
    }

    private void deleteBook() {
        ObservableList<Book> selectedBooks = bookTable.getSelectionModel().getSelectedItems();
        if (selectedBooks == null || selectedBooks.isEmpty()) {
            UIUtil.showWarning("No Selection", "Please select one or more books from the table to delete.");
            return;
        }

        int count = selectedBooks.size();
        String message = count == 1
            ? "Are you sure you want to delete the book '" + selectedBooks.get(0).getName() + "'? This action cannot be undone."
            : "Are you sure you want to delete " + count + " selected books? This action cannot be undone.";

        UIUtil.showConfirmation("Delete Books", message,
            () -> {
                // This code runs if the user confirms
                int successCount = 0;
                int failCount = 0;
                StringBuilder failedBooks = new StringBuilder();

                for (Book book : selectedBooks) {
                    if (bookDAO.deleteBook(book.getId())) {
                        successCount++;
                    } else {
                        failCount++;
                        if (failedBooks.length() > 0) failedBooks.append(", ");
                        failedBooks.append(book.getName());
                    }
                }

                loadBooks();
                clearForm();

                if (successCount > 0) {
                    UIUtil.showSuccess("Success", successCount + " book(s) deleted successfully.");
                    new Pulse(bookTable).play();
                }

                if (failCount > 0) {
                    UIUtil.showError("Deletion Failed", "Could not delete " + failCount + " book(s): " + failedBooks.toString() +
                        ". These books may be currently issued or have a history.");
                }
            });
    }

    private void deleteCategory() {
        ObservableList<Category> selectedCategories = categoryListView.getSelectionModel().getSelectedItems();
        if (selectedCategories == null || selectedCategories.isEmpty()) {
            UIUtil.showWarning("No Selection", "Please select one or more categories from the list to delete.");
            return;
        }

        int count = selectedCategories.size();
        String message = count == 1
            ? "Are you sure you want to delete the category '" + selectedCategories.get(0).getName() + "'? This may affect books in this category."
            : "Are you sure you want to delete " + count + " selected categories? This may affect books in these categories.";

        UIUtil.showConfirmation("Delete Categories", message,
            () -> {
                // This code runs if the user confirms
                int successCount = 0;
                int failCount = 0;
                StringBuilder failedCategories = new StringBuilder();

                for (Category category : selectedCategories) {
                    if (categoryDAO.deleteCategory(category.getId())) {
                        successCount++;
                    } else {
                        failCount++;
                        if (failedCategories.length() > 0) failedCategories.append(", ");
                        failedCategories.append(category.getName());
                    }
                }

                loadCategories();
                loadBooks(); // Also reload books as their category might be affected

                if (successCount > 0) {
                    UIUtil.showSuccess("Success", successCount + " category(ies) deleted successfully.");
                    new Pulse(categoryListView).play();
                }

                if (failCount > 0) {
                    UIUtil.showError("Deletion Failed", "Could not delete " + failCount + " category(ies): " + failedCategories.toString() +
                        ". These categories may still contain books.");
                }
            });
    }

    private void filterBooks(String query) {
        if (query == null || query.isEmpty()) {
            bookTable.setItems(books);
        } else {
            filteredBooks.clear();
            String lowerQuery = query.toLowerCase();
            for (Book book : books) {
                if (book.getName().toLowerCase().contains(lowerQuery) ||
                    book.getAuthor().toLowerCase().contains(lowerQuery) ||
                    book.getBarcode().toLowerCase().contains(lowerQuery) ||
                    book.getCategory().getName().toLowerCase().contains(lowerQuery) ||
                    String.valueOf(book.getQuantity()).contains(lowerQuery)) {
                    filteredBooks.add(book);
                }
            }
            bookTable.setItems(filteredBooks);
        }
    }

    /**
     * StringConverter for the Category ComboBox.
     */
    private class CategoryStringConverter extends javafx.util.StringConverter<Category> {
        @Override
        public String toString(Category category) {
            return category == null ? "" : category.getName();
        }

        @Override
        public Category fromString(String string) {
            if (string == null || string.trim().isEmpty()) {
                return null;
            }
            return categories.stream()
                .filter(c -> c.getName().equalsIgnoreCase(string.trim()))
                .findFirst()
                .orElse(null);
        }
    }
}
