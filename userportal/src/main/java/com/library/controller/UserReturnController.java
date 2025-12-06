package com.library.controller;

import com.library.dao.IssuedBookDAO;
import com.library.model.Book;
import com.library.model.User;
import com.library.model.UserSession;
import com.library.util.UIUtil;
import com.library.util.UILayoutConstants;
import com.library.util.EmailService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.BlurType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import javafx.beans.property.SimpleStringProperty;
import javafx.application.Platform;

// For email functionality
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Controller for handling book return functionality.
 * Allows users to view issued books and return them by barcode, with email confirmation.
 */
public class UserReturnController {
    private Stage stage;
    private List<String[]> issuedBooks;
    private List<String[]> returnedBooks = new ArrayList<>();
    private ObservableList<Book> searchResults = FXCollections.observableArrayList();
    private ObservableList<Book> selectedBooks = FXCollections.observableArrayList();
    private ScheduledExecutorService debounceExecutor = Executors.newSingleThreadScheduledExecutor();

    /**
     * Constructor for UserReturnController.
     * Initializes the list of issued books for the logged-in user.
     *
     * @param stage the primary stage of the application
     */
    public UserReturnController(Stage stage) {
        this.stage = stage;
        User user = UserSession.getLoggedInUser(stage);
        if (user.getCourse().isEmpty()) {
            // Faculty
            issuedBooks = new IssuedBookDAO().getIssuedBooksForFaculty(user.getId());
        } else {
            // Student
            issuedBooks = new IssuedBookDAO().getIssuedBooks(user.getId());
        }
    }

    /**
     * Creates and returns the return scene.
     * Includes search functionality, book selection, and navigation buttons.
     *
     * @return the Scene object for the return view
     */
    public Scene getScene() {
        StackPane mainLayout = new StackPane();
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

        Label heading = new Label("📘 Return Portal");
        heading.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search by book name or barcode...");
        searchField.setPrefWidth(660);
        searchField.setPadding(new Insets(12));
        searchField.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        searchField.setFocusTraversable(false);
        searchField.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.05), 6, 0, 0, 2));

        // Initialize with all issued books
        List<Book> issuedBookObjects = issuedBooks.stream()
                .map(bookData -> {
                    Book book = new Book();
                    book.setName(bookData[0]);
                    book.setBarcode(bookData[1]);
                    book.setAuthor(bookData[2]);
                    return book;
                })
                .collect(Collectors.toList());
        searchResults.setAll(issuedBookObjects);

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

        bookTable.getColumns().addAll(titleCol, authorCol, barcodeCol);

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
            Book selected = bookTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UIUtil.showAlert("Error", "Please select a book from the search results to add.", Alert.AlertType.ERROR);
                return;
            }
            if (selectedBooks.stream().anyMatch(b -> b.getBarcode().equals(selected.getBarcode()))) {
                UIUtil.showAlert("Error", "This book is already added for returning.", Alert.AlertType.ERROR);
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
                UIUtil.showAlert("Error", "No books selected to return.", Alert.AlertType.ERROR);
                return;
            }
            User user = UserSession.getLoggedInUser(stage);
            try {
                // Return the books and build returnedBooksList for confirmation scene
                returnedBooks = new ArrayList<>();
                IssuedBookDAO issuedBookDAO = new IssuedBookDAO();
                for (Book book : selectedBooks) {
                    boolean success = false;
                    if (user.getCourse().isEmpty()) {
                        // Faculty
                        success = issuedBookDAO.returnBookForFaculty(user.getId(), book.getBarcode());
                    } else {
                        // Student
                        success = issuedBookDAO.returnBook(user.getId(), book.getBarcode());
                    }
                    if (!success) {
                        UIUtil.showAlert("Error", "Failed to return book: " + book.getName() + " (" + book.getBarcode() + ").", Alert.AlertType.ERROR);
                        return;
                    }
                    // Add to returnedBooks for email
                    returnedBooks.add(new String[]{book.getName(), book.getBarcode(), book.getAuthor()});
                }

                // Send email notification for book return
                sendBookReturnEmail(stage);

                // Clear inputs after storing returned list
                selectedBooks.clear();
                searchResults.clear();
                searchField.clear();

                // Logout after returning to prevent session confusion for next user
                UserSession.logout(stage);

                UIUtil.switchScene(stage, new UserLoginController(stage).getScene());
            } catch (Exception ex) {
                UIUtil.showAlert("Error", "An unexpected error occurred while returning books.", Alert.AlertType.ERROR);
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
                for (String[] issuedBook : issuedBooks) {
                    if (issuedBook[1].equals(trimmed)) {
                        Book book = new Book();
                        book.setName(issuedBook[0]);
                        book.setBarcode(issuedBook[1]);
                        book.setAuthor(issuedBook[2]);
                        if (selectedBooks.stream().noneMatch(b -> b.getBarcode().equals(trimmed))) {
                            selectedBooks.add(book);
                        }
                        // Use Platform.runLater to avoid triggering another change event during current event processing
                        Platform.runLater(() -> searchField.clear());
                        return;
                    }
                }

                // Debounce search
                debounceExecutor.schedule(() -> {
                    List<Book> results = issuedBooks.stream()
                            .filter(bookData -> bookData[0].toLowerCase().contains(trimmed.toLowerCase()) ||
                                               bookData[1].contains(trimmed))
                            .map(bookData -> {
                                Book book = new Book();
                                book.setName(bookData[0]);
                                book.setBarcode(bookData[1]);
                                book.setAuthor(bookData[2]);
                                return book;
                            })
                            .limit(10)
                            .collect(Collectors.toList());
                    searchResults.setAll(results);
                }, 300, TimeUnit.MILLISECONDS);
            } else {
                // Show all issued books when search is empty
                List<Book> allBooks = issuedBooks.stream()
                        .map(bookData -> {
                            Book book = new Book();
                            book.setName(bookData[0]);
                            book.setBarcode(bookData[1]);
                            book.setAuthor(bookData[2]);
                            return book;
                        })
                        .collect(Collectors.toList());
                searchResults.setAll(allBooks);
            }
        });

        HBox buttonsBox = new HBox(10, addBookBtn, removeBookBtn);
        buttonsBox.setPadding(UILayoutConstants.PADDING);
        buttonsBox.setAlignment(Pos.CENTER);

        HBox navButtonsBox = new HBox(10, continueBtn, backBtn, logoutBtn);
        navButtonsBox.setPadding(UILayoutConstants.PADDING);
        navButtonsBox.setAlignment(Pos.CENTER);

        contentBox.getChildren().addAll(heading, searchField, bookTable, buttonsBox, new Label("Selected Books to Return:"), selectedBookTable, navButtonsBox);
        mainLayout.getChildren().add(contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);

        return new Scene(mainLayout, UILayoutConstants.SCENE_WIDTH, UILayoutConstants.SCENE_HEIGHT);
    }



    /**
     * Creates the confirmation scene after returning books.
     * Displays the list of returned books and sends email confirmation.
     *
     * @return the Scene object for the confirmation view
     */
    private Scene getConfirmationScene() {
        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(20));
        contentBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 14;");
        contentBox.setMaxWidth(600);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.12), 6, 0, 0, 1));

        Label message = new Label("Successfully returned the books.");
        message.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");

        TableView<Book> returnedTable = new TableView<>();
        List<Book> returnedBookObjects = new ArrayList<>();
        for (String[] bookData : returnedBooks) {
            Book book = new Book();
            book.setName(bookData[0]);
            book.setBarcode(bookData[1]);
            book.setAuthor(bookData[2]);
            returnedBookObjects.add(book);
        }
        returnedTable.setItems(FXCollections.observableArrayList(returnedBookObjects));
        returnedTable.setPrefHeight(200);
        returnedTable.setPrefWidth(560);
        returnedTable.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-font-size: 14;");
        returnedTable.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.04), 4, 0, 0, 1));

        TableColumn<Book, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(200);

        TableColumn<Book, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBarcode()));
        barcodeCol.setPrefWidth(150);

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        authorCol.setPrefWidth(200);

        returnedTable.getColumns().addAll(nameCol, barcodeCol, authorCol);

        Button logoutBtn = createStyledButton("Logout", "#ef4444", "#dc2626");
        logoutBtn.setPrefWidth(180);
        logoutBtn.setOnAction(e -> {
            UserSession.logout(stage);
            UIUtil.switchScene(stage, new UserLoginController(stage).getScene());
        });

        Button backBtn = createStyledButton("Back", "#6b7280", "#374151");
        backBtn.setPrefWidth(180);
        backBtn.setOnAction(e -> UIUtil.switchScene(stage, new UserDashboardController(stage).getScene()));

        HBox navBox = new HBox(20, backBtn, logoutBtn);
        navBox.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));
        navBox.setAlignment(Pos.CENTER);

        contentBox.getChildren().addAll(message, returnedTable, navBox);

        StackPane mainLayout = new StackPane();
        mainLayout.setStyle(UILayoutConstants.FULL_BACKGROUND_STYLE);
        mainLayout.getChildren().add(contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);

        return new Scene(mainLayout, UILayoutConstants.SCENE_WIDTH, UILayoutConstants.SCENE_HEIGHT);
    }

    /**
     * Sends a confirmation email to the user after returning books.
     *
     * @param toEmail the recipient's email address
     * @param subject the email subject
     * @param body the email body content
     */
    private void sendConfirmationEmail(String toEmail, String subject, String body) {
        Properties config = new Properties();
        String fromEmail = "";
        String password = "";

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
            return; // Stop if config fails to load
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Gmail SMTP Host
        props.put("mail.smtp.port", "587"); // Gmail SMTP Port
        props.put("mail.smtp.auth", "true"); // Enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS

        // Create a session with an authenticator
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
            message.setFrom(new InternetAddress(fromEmail));
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

    private void sendBookReturnEmail(Stage stage) {
        try {
            EmailService emailService = new EmailService();
            User user = UserSession.getLoggedInUser(stage);

            // Get current issued books list
            StringBuilder issuedBooksStr = new StringBuilder();
            List<String[]> issuedBooks = new IssuedBookDAO().getIssuedBooks(user.getId());
            if (issuedBooks.size() > 0) {
                for (String[] book : issuedBooks) {
                    issuedBooksStr.append("- ").append(book[0]).append(" (Barcode: ").append(book[1]).append(")\n");
                }
            } else {
                issuedBooksStr.append("No books currently issued.");
            }

            // Send email for each returned book
            for (String[] book : returnedBooks) {
                boolean success = emailService.sendBookReturnNotification(
                    user.getEmail(),
                    user.getName(),
                    user.getRfid(),
                    book[0], // book name
                    java.time.LocalDate.now().toString(),
                    issuedBooksStr.toString()
                );

                if (!success) {
                    System.err.println("Failed to send return notification email for book: " + book[0]);
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending book return email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Button createStyledButton(String text, String startColor, String endColor) {
        Button btn = new Button(text);
        String baseStyle = String.format("-fx-background-color: linear-gradient(%s, %s); -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand;", startColor, endColor);
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(String.format("-fx-background-color: linear-gradient(%s, %s); -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand;", endColor, startColor)));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        return btn;
    }
}
