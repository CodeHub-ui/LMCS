package com.library.controller;

import com.library.dao.BookDAO;
import com.library.dao.IssuedBookDAO;
import com.library.model.Book;
import com.library.model.UserSession;
import com.library.util.UIUtil;
import com.library.util.UILayoutConstants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;

/**
 * UserManagementController provides the main UI for users
 * to view available books, their issued books, and navigate issue/return/logout.
 */
public class UserManagementController {
    private Stage stage;
    private BookDAO bookDAO = new BookDAO();
    private IssuedBookDAO issuedBookDAO = new IssuedBookDAO();

    private ObservableList<Book> availableBooks = FXCollections.observableArrayList();
    private ObservableList<Book> filteredAvailableBooks = FXCollections.observableArrayList();
    private ObservableList<Book> issuedBooks = FXCollections.observableArrayList();

    private TableView<Book> availableBooksTable = new TableView<>();
    private TableView<Book> issuedBooksTable = new TableView<>();
    private TextField searchField = new TextField();

    public UserManagementController(Stage stage) {
        this.stage = stage;
        loadAvailableBooks();
        loadIssuedBooks();
    }

    private void loadAvailableBooks() {
        try {
            List<Book> books = bookDAO.getAllBooks(); // Could refine to exclude issued books
            availableBooks.setAll(books);
            filteredAvailableBooks.setAll(books);
        } catch (java.sql.SQLException e) {
            System.err.println("Error loading available books: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadIssuedBooks() {
        List<String[]> issuedBookData = issuedBookDAO.getIssuedBooks(UserSession.getLoggedInUser(stage).getId());
        // Convert String[] to Book objects for table display
        issuedBooks.clear();
        for (String[] data : issuedBookData) {
            Book b = new Book();
            b.setName(data[0]);
            b.setBarcode(data[1]);
            b.setAuthor(data[2]);
            issuedBooks.add(b);
        }
    }

    private void setupAvailableBooksTable() {
        TableColumn<Book, String> nameCol = new TableColumn<>("Title");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(350);

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(250);

        TableColumn<Book, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        barcodeCol.setPrefWidth(200);

        availableBooksTable.getColumns().addAll(nameCol, authorCol, barcodeCol);
        availableBooksTable.setItems(filteredAvailableBooks);
        availableBooksTable.setPrefHeight(250);
    }

    private void setupIssuedBooksTable() {
        TableColumn<Book, String> nameCol = new TableColumn<>("Title");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(350);

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(250);

        TableColumn<Book, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        barcodeCol.setPrefWidth(200);

        issuedBooksTable.getColumns().addAll(nameCol, authorCol, barcodeCol);
        issuedBooksTable.setItems(issuedBooks);
        issuedBooksTable.setPrefHeight(250);
    }

    private void filterAvailableBooks(String query) {
        if (query == null || query.isEmpty()) {
            filteredAvailableBooks.setAll(availableBooks);
        } else {
            ObservableList<Book> filtered = FXCollections.observableArrayList();
            String lowerQuery = query.toLowerCase();
            for (Book book : availableBooks) {
                if (book.getName().toLowerCase().contains(lowerQuery) ||
                    book.getAuthor().toLowerCase().contains(lowerQuery) ||
                    book.getBarcode().toLowerCase().contains(lowerQuery)) {
                    filtered.add(book);
                }
            }
            filteredAvailableBooks.setAll(filtered);
        }
    }

public Scene getScene() {
        if (UserSession.getLoggedInUser(stage) == null) {
            UIUtil.switchScene(stage, new UserLoginController(stage).getScene());
            return null;
        }

        StackPane mainLayout = new StackPane();
        mainLayout.setStyle("-fx-background-image: url('https://images.unsplash.com/photo-1512820790803-83ca734da794?ixlib=rb-4.0.3&auto=format&fit=crop&w=1471&q=80'); -fx-background-size: cover;");

        VBox centerLayout = new VBox(15);
        centerLayout.setPadding(UILayoutConstants.PADDING);
        centerLayout.setStyle("-fx-background-color: rgba(255, 255, 255, 0.92); -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 8);");
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.setMaxWidth(900); // Constrain width for centering

        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #ef4444; -fx-text-fill: #ef4444; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;");
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle("-fx-background-color: #fef2f2; -fx-border-color: #dc2626; -fx-text-fill: #dc2626; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #ef4444; -fx-text-fill: #ef4444; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;"));
        logoutBtn.setOnAction(e -> {
            UserSession.logout(stage);
            UIUtil.switchScene(stage, new UserLoginController(stage).getScene());
        });

        Button issueBtn = new Button("📗 Issue Book");
        issueBtn.setStyle("-fx-background-color: linear-gradient(#1f7aec, #0f62fe); -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand; -fx-pref-width: 140;");
        issueBtn.setOnMouseEntered(e -> issueBtn.setStyle("-fx-background-color: linear-gradient(#0f62fe, #1f7aec); -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand; -fx-pref-width: 140;"));
        issueBtn.setOnMouseExited(e -> issueBtn.setStyle("-fx-background-color: linear-gradient(#1f7aec, #0f62fe); -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand; -fx-pref-width: 140;"));
        issueBtn.setOnAction(e -> UIUtil.switchScene(stage, new UserIssueController(stage).getScene()));

        Button returnBtn = new Button("📘 Return Book");
        returnBtn.setStyle("-fx-background-color: linear-gradient(#f59e0b, #f97316); -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand; -fx-pref-width: 140;");
        returnBtn.setOnMouseEntered(e -> returnBtn.setStyle("-fx-background-color: linear-gradient(#d97706, #b45309); -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand; -fx-pref-width: 140;"));
        returnBtn.setOnMouseExited(e -> returnBtn.setStyle("-fx-background-color: linear-gradient(#f59e0b, #f97316); -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand; -fx-pref-width: 140;"));
        returnBtn.setOnAction(e -> UIUtil.switchScene(stage, new UserReturnController(stage).getScene()));

        Label availableBooksLabel = new Label("Available Books");
        availableBooksLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        searchField.setPromptText("Search available books...");
        searchField.setStyle("-fx-background-radius: 8; -fx-padding: 8 12 8 12; -fx-pref-width: 350; -fx-border-color: #d1d5db;");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterAvailableBooks(newValue));

        Label issuedBooksLabel = new Label("Your Issued Books");
        issuedBooksLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        setupAvailableBooksTable();
        setupIssuedBooksTable();

        HBox bottomBar = new HBox(20);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.getChildren().addAll(issueBtn, returnBtn, logoutBtn);

        centerLayout.getChildren().addAll(availableBooksLabel, searchField, availableBooksTable, issuedBooksLabel, issuedBooksTable, bottomBar);

        mainLayout.getChildren().add(centerLayout);
        StackPane.setAlignment(centerLayout, Pos.CENTER);

        stage.setFullScreen(true);
        return new Scene(mainLayout);
    }

}
