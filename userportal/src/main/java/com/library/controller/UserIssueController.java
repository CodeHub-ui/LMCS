package com.library.controller;

import com.library.dao.IssuedBookDAO;
import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.model.User;
import com.library.model.UserSession;
import com.library.util.EmailService;
import com.library.util.UIUtil;
import javafx.geometry.Pos;
import com.library.util.UILayoutConstants;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.BlurType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import javafx.beans.property.SimpleStringProperty;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.application.Platform;

import jakarta.mail.*;
import jakarta.mail.internet.*;

/**
 * Controller for handling book issuing functionality.
 * Allows users to search, select, and issue books, without email confirmation.
 * Refactored to use a search bar and TableView similar to UserManagementController.
 */
public class UserIssueController {
    private Stage stage;
    private ObservableList<Book> searchResults = FXCollections.observableArrayList();
    private ObservableList<Book> selectedBooks = FXCollections.observableArrayList();

    private List<Book> issuedBooksList = null;
    private List<Book> allBooks = new java.util.ArrayList<>();
    private ScheduledExecutorService debounceExecutor = Executors.newSingleThreadScheduledExecutor();

    /**
     * Constructor for UserIssueController.
     * @param stage the primary stage of the application
     */
    public UserIssueController(Stage stage) {
        this.stage = stage;
        // Load all available books into memory for fast searching
        try {
            allBooks = new BookDAO().getAllBooks().stream()
                    .filter(Book::isAvailable)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error loading books: " + e.getMessage());
            allBooks = new java.util.ArrayList<>();
        }
    }

    /**
     * Creates and returns the issue scene.
     * Includes search functionality, book selection, and navigation buttons.
     *
     * @return the Scene object for the issue view
     */
    public Scene getScene() {
        StackPane mainLayout = new StackPane();
        // Padding is now on the contentBox, so it's removed from the main layout.
        mainLayout.setStyle(UILayoutConstants.FULL_BACKGROUND_STYLE);

        VBox contentBox = new VBox(20);
        contentBox.setPadding(UILayoutConstants.PADDING);
        contentBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.92); -fx-background-radius: 15;");
        contentBox.setMaxWidth(700);
        contentBox.setAlignment(Pos.CENTER);

        DropShadow shadow = new DropShadow();
        shadow.setBlurType(BlurType.GAUSSIAN);
        shadow.setColor(Color.rgb(0, 0, 0, 0.12));
        shadow.setRadius(16);
        contentBox.setEffect(shadow);

        Label heading = new Label("📚 Book Issue Portal");
        heading.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search by book name or barcode...");
        searchField.setPrefWidth(660);
        searchField.setPadding(new Insets(12));
        searchField.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        searchField.setFocusTraversable(false);
        searchField.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.05), 6, 0, 0, 2));

        TableView<Book> bookTable = new TableView<>();
        bookTable.setItems(searchResults);
        bookTable.setPrefHeight(200);


        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        titleCol.setPrefWidth(200);

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        authorCol.setPrefWidth(150);

        TableColumn<Book, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBarcode()));
        barcodeCol.setPrefWidth(120);

        TableColumn<Book, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getCategoryId())));
        categoryCol.setPrefWidth(100);

        TableColumn<Book, String> availabilityCol = new TableColumn<>("Available");
        availabilityCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isAvailable() ? "Yes" : "No"));
        availabilityCol.setPrefWidth(80);

        bookTable.getColumns().addAll(titleCol, authorCol, barcodeCol, categoryCol, availabilityCol);

        TableView<Book> selectedBookTable = new TableView<>();
        selectedBookTable.setItems(selectedBooks);
        selectedBookTable.setPrefHeight(150);

        TableColumn<Book, String> selTitleCol = new TableColumn<>("Title");
        selTitleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        selTitleCol.setPrefWidth(200);

        TableColumn<Book, String> selBarcodeCol = new TableColumn<>("Barcode");
        selBarcodeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBarcode()));
        selBarcodeCol.setPrefWidth(120);

        selectedBookTable.getColumns().addAll(selTitleCol, selBarcodeCol);

        Button addBookBtn = UIUtil.createStyledButton("Add Book", "#3b82f6", "#2563eb");
        addBookBtn.setPrefWidth(200);
        addBookBtn.setPrefHeight(50);
        addBookBtn.setOnAction(e -> {
            User user = UserSession.getLoggedInUser(stage);
            Book selected = bookTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UIUtil.showAlert("Error", "Please select a book from the search results to add.", Alert.AlertType.ERROR);
                return;
            }
            if (!selected.isAvailable()) {
                UIUtil.showAlert("Error", "This book is not available.", Alert.AlertType.ERROR);
                return;
            }
            if (selectedBooks.stream().anyMatch(b -> b.getBarcode().equals(selected.getBarcode()))) {
                UIUtil.showAlert("Error", "This book is already added for issuing.", Alert.AlertType.ERROR);
                return;
            }
            // Check if book is already issued to this user
            boolean alreadyIssued = false;
            if (user.getCourse().isEmpty()) {
                // Faculty
                alreadyIssued = new IssuedBookDAO().getIssuedBooksForFaculty(user.getId())
                        .stream()
                        .anyMatch(issued -> issued[1].equals(selected.getBarcode()));
            } else {
                // Student
                alreadyIssued = new IssuedBookDAO().getIssuedBooks(user.getId())
                        .stream()
                        .anyMatch(issued -> issued[1].equals(selected.getBarcode()));
            }
            if (alreadyIssued) {
                UIUtil.showAlert("Error", "This book is already issued to you.", Alert.AlertType.ERROR);
                return;
            }
            // Check if adding this book would exceed the 5-book limit
            int currentIssued = 0;
            if (user.getCourse().isEmpty()) {
                // Faculty
                currentIssued = new IssuedBookDAO().getIssuedCountForFaculty(user.getId());
            } else {
                // Student
                currentIssued = new IssuedBookDAO().getIssuedCountForStudent(user.getId());
            }
            if (currentIssued + selectedBooks.size() + 1 > 5) {
                UIUtil.showAlert("Error", "You can issue a maximum of 5 books. You currently have " + currentIssued + " issued, and have " + selectedBooks.size() + " selected.", Alert.AlertType.ERROR);
                return;
            }
            selectedBooks.add(selected);
        });

        Button removeBookBtn = UIUtil.createStyledButton("Remove Selected Book", "#ef4444", "#dc2626");
        removeBookBtn.setPrefWidth(200);
        removeBookBtn.setPrefHeight(50);
        removeBookBtn.setOnAction(e -> {
            Book selected = selectedBookTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UIUtil.showAlert("Error", "Please select a book to remove.", Alert.AlertType.ERROR);
                return;
            }
            selectedBooks.remove(selected);
        });

        Button continueBtn = UIUtil.createStyledButton("Continue", "#22c55e", "#16a34a");
        continueBtn.setPrefWidth(200);
        continueBtn.setPrefHeight(50);
        continueBtn.setOnAction(e -> {
            if (selectedBooks.isEmpty()) {
                UIUtil.showAlert("Error", "No books selected to issue.", Alert.AlertType.ERROR);
                return;
            }
            User user = UserSession.getLoggedInUser(stage);
            try {
                // Issue the books and build issuedBooksList for confirmation scene
                issuedBooksList = new java.util.ArrayList<>();
                IssuedBookDAO issuedBookDAO = new IssuedBookDAO();
                for (Book book : selectedBooks) {
                    boolean success = false;
                    if (user.getCourse().isEmpty()) {
                        // Faculty
                        success = issuedBookDAO.issueBookForFaculty(user.getId(), book.getBarcode());
                    } else {
                        // Student
                        success = issuedBookDAO.issueBook(user.getId(), book.getBarcode());
                    }
                    if (!success) {
                        UIUtil.showAlert("Error", "Failed to issue book: " + book.getName() + " (" + book.getBarcode() + ").", Alert.AlertType.ERROR);
                        return;
                    }
                    issuedBooksList.add(book);
                }

                // Send email notification for book issue
                sendBookIssueEmail(stage);

                // Clear inputs after storing issued list
                selectedBooks.clear();
                searchResults.clear();
                searchField.clear();

                // Logout after issuing to prevent session confusion for next user
                UserSession.logout(stage);

                UIUtil.switchScene(stage, new UserLoginController(stage).getScene());
            } catch (Exception ex) {
                UIUtil.showAlert("Error", "An unexpected error occurred while issuing books.", Alert.AlertType.ERROR);
                ex.printStackTrace();
            }
        });


        Button backBtn = UIUtil.createStyledButton("Back", "#6b7280", "#4b5563");
        backBtn.setPrefWidth(200);
        backBtn.setPrefHeight(50);
        backBtn.setOnAction(e -> UIUtil.switchScene(stage, new UserDashboardController(stage).getScene()));

        Button logoutBtn = UIUtil.createStyledButton("Logout", "#ef4444", "#dc2626");
        logoutBtn.setPrefWidth(200);
        logoutBtn.setPrefHeight(50);
        logoutBtn.setOnAction(e -> {
            UserSession.logout(stage);
            UIUtil.switchScene(stage, new UserLoginController(stage).getScene());
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String trimmed = newValue.trim();
            // Cancel previous debounce task
            debounceExecutor.shutdownNow();
            debounceExecutor = Executors.newSingleThreadScheduledExecutor();

            if (!trimmed.isEmpty()) {
                // Check for exact barcode match for auto-add
                Book exactMatch = allBooks.stream()
                        .filter(book -> book.getBarcode().equals(trimmed))
                        .findFirst()
                        .orElse(null);
                if (exactMatch != null) {
                    // Auto-add to selected books if not already added
                    if (selectedBooks.stream().noneMatch(b -> b.getBarcode().equals(trimmed))) {
                        selectedBooks.add(exactMatch);
                    }
                    // Use Platform.runLater to avoid triggering another change event during current event processing
                    Platform.runLater(() -> searchField.clear());
                    return;
                }

                // Debounce search
                debounceExecutor.schedule(() -> {
                    List<Book> results = allBooks.stream()
                            .filter(book -> book.getName().toLowerCase().contains(trimmed.toLowerCase()) ||
                                           book.getBarcode().contains(trimmed))
                            .limit(10)
                            .collect(Collectors.toList());
                    searchResults.setAll(results);
                }, 300, TimeUnit.MILLISECONDS);
            } else {
                searchResults.clear();
            }
        });

        HBox buttonsBox = new HBox(10, addBookBtn, removeBookBtn);
        buttonsBox.setPadding(UILayoutConstants.PADDING);
        buttonsBox.setAlignment(Pos.CENTER);

        HBox navButtonsBox = new HBox(10, continueBtn, backBtn, logoutBtn);
        navButtonsBox.setPadding(UILayoutConstants.PADDING);
        navButtonsBox.setAlignment(Pos.CENTER);

        contentBox.getChildren().addAll(heading, searchField, bookTable, buttonsBox, new Label("Selected Books to Issue:"), selectedBookTable, navButtonsBox);
        mainLayout.getChildren().add(contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);

        return new Scene(mainLayout, UILayoutConstants.SCENE_WIDTH, UILayoutConstants.SCENE_HEIGHT);
    }

    private Scene getConfirmationScene() {
        VBox contentBox = new VBox(20);
        contentBox.setPadding(UILayoutConstants.PADDING);
        contentBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 14;");
        contentBox.setMaxWidth(600);
        contentBox.setAlignment(Pos.CENTER);

        Label heading = new Label("Issue Portal");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label message = new Label("Successfully issued book(s).");
        message.setStyle("-fx-font-size: 16px; -fx-text-fill: #334155; -fx-font-weight: 600;");

        TableView<Book> issuedTable = new TableView<>();
        issuedTable.setItems(FXCollections.observableArrayList(issuedBooksList != null ? issuedBooksList : new java.util.ArrayList<>()));
        issuedTable.setPrefHeight(200);
        issuedTable.setPrefWidth(560);
        issuedTable.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-font-size: 14;");
        issuedTable.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.04), 4, 0, 0, 1));

        TableColumn<Book, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(200);

        TableColumn<Book, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBarcode()));
        barcodeCol.setPrefWidth(150);

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        authorCol.setPrefWidth(200);

        issuedTable.getColumns().addAll(nameCol, barcodeCol, authorCol);

        List<Book> tempIssuedBooks = issuedBooksList;
        issuedBooksList = null; // Clear after use

        // Email sending removed as per request

        Button logoutBtn = UIUtil.createStyledButton("🚪 Logout", "#ef4444", "#dc2626");
        logoutBtn.setPrefWidth(180);
        logoutBtn.setOnAction(e -> {
            UserSession.logout(stage);
            UIUtil.switchScene(stage, new UserLoginController(stage).getScene());
        });

        Button backBtn = UIUtil.createStyledButton("← Back", "#6b7280", "#4b5363");
        backBtn.setPrefWidth(180);
        backBtn.setOnAction(e -> UIUtil.switchScene(stage, new UserDashboardController(stage).getScene()));

        HBox navBox = new HBox(20, backBtn, logoutBtn);
        navBox.setAlignment(Pos.CENTER);

        contentBox.getChildren().addAll(heading, message, issuedTable, navBox);

        StackPane mainLayout = new StackPane();
        mainLayout.setStyle(UILayoutConstants.FULL_BACKGROUND_STYLE);
        mainLayout.getChildren().add(contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);

        return new Scene(mainLayout, UILayoutConstants.SCENE_WIDTH, UILayoutConstants.SCENE_HEIGHT);
    }

    private void sendBookIssueEmail(Stage stage) {
        try {
            EmailService emailService = new EmailService();
            User user = UserSession.getLoggedInUser(stage);

            // Get current issued books list
            StringBuilder issuedBooksStr = new StringBuilder();
            List<String[]> issuedBooks = null;
            if (user.getCourse().isEmpty()) {
                // Faculty
                issuedBooks = new IssuedBookDAO().getIssuedBooksForFaculty(user.getId());
            } else {
                // Student
                issuedBooks = new IssuedBookDAO().getIssuedBooks(user.getId());
            }
            if (issuedBooks.size() > 0) {
                for (String[] book : issuedBooks) {
                    issuedBooksStr.append("- ").append(book[0]).append(" (Barcode: ").append(book[1]).append(")\n");
                }
            } else {
                issuedBooksStr.append("No books currently issued.");
            }

            // Calculate due date (assuming 14 days from now)
            java.time.LocalDate dueDate = java.time.LocalDate.now().plusDays(14);
            String dueDateStr = dueDate.toString();

            // Send email for each issued book
            for (Book book : issuedBooksList) {
                boolean success = emailService.sendBookIssueNotification(
                    user.getEmail(),
                    user.getName(),
                    user.getRfid(),
                    book.getName(),
                    java.time.LocalDate.now().toString(),
                    dueDateStr,
                    issuedBooksStr.toString()
                );

                if (!success) {
                    System.err.println("Failed to send issue notification email for book: " + book.getName());
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending book issue email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendConfirmationEmail(String toEmail, String subject, String body) {
        Properties config = new Properties();
        String fromEmail;
        String password;

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("❌ Sorry, unable to find config.properties");
                return;
            }
            config.load(input);
            fromEmail = config.getProperty("email.username");
            password = config.getProperty("email.password");
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        final String finalFromEmail = fromEmail;
        final String finalPassword = password;
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(finalFromEmail, finalPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(finalFromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("✅ Confirmation email sent to " + toEmail);
        } catch (MessagingException e) {

            e.printStackTrace();
            System.out.println("❌ Failed to send confirmation email.");
        }
    }
}
