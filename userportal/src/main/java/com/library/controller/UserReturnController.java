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
import java.util.List;
import java.util.ArrayList;
import javafx.scene.control.Alert;
import javafx.beans.property.SimpleStringProperty;

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
    private TableView<Book> issuedBooksTable;

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
     * Displays issued books and provides options to return books or navigate.
     *
     * @return the Scene object for the return view
     */
    public Scene getScene() {
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(30));
        contentBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.92); -fx-background-radius: 20;");
        contentBox.setMaxWidth(520);
        contentBox.setAlignment(Pos.CENTER);

        Label heading = new Label("📘 Return Portal");
        heading.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label issuedLabel = new Label("Your issued books:");
        issuedLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #334155;");
        issuedBooksTable = new TableView<>();
        issuedBooksTable.setPrefHeight(160);
        issuedBooksTable.setPrefWidth(480);
        issuedBooksTable.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #d1d5db; -fx-font-size: 15; -fx-text-fill: #1e293b;");
        issuedBooksTable.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.06), 6, 0, 0, 1));
        refreshIssuedBooksTable();

        TextField barcodeField = new TextField();
        barcodeField.setPromptText("Scan Barcode to Return");
        barcodeField.setPrefWidth(480);
        barcodeField.setPadding(new Insets(10));
        barcodeField.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #cbd5e1; -fx-font-size: 15; -fx-text-fill: #334155;");
        barcodeField.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.04), 4, 0, 0, 2));

        Button returnBtn = createStyledButton("Return Selected Book", "#1f7aec", "#0f62fe");
        returnBtn.setPrefWidth(480);
        returnBtn.setPrefHeight(40);
        returnBtn.setOnAction(e -> {
            String barcode = barcodeField.getText();
            if (barcode.trim().isEmpty()) {
                UIUtil.showAlert("Error", "Please enter a barcode to return.", Alert.AlertType.ERROR);
                return;
            }
            User user = UserSession.getLoggedInUser(stage);
            IssuedBookDAO dao = new IssuedBookDAO();
            boolean success;
            if (user.getCourse().isEmpty()) {
                // Faculty
                success = dao.returnBookForFaculty(user.getId(), barcode);
            } else {
                // Student
                success = dao.returnBook(user.getId(), barcode);
            }
            if (success) {
                // Find the returned book details
                for (String[] book : issuedBooks) {
                    if (book[1].equals(barcode)) {
                        returnedBooks.add(book);
                        break;
                    }
                }
                UIUtil.showAlert("Success", "Book returned.", Alert.AlertType.INFORMATION);
                if (user.getCourse().isEmpty()) {
                    // Faculty
                    issuedBooks = dao.getIssuedBooksForFaculty(user.getId());
                } else {
                    // Student
                    issuedBooks = dao.getIssuedBooks(user.getId());
                }
                refreshIssuedBooksTable();
                barcodeField.clear();

                // Send email notification for book return
                sendBookReturnEmail(stage);

            } else {
                UIUtil.showAlert("Error", "Return failed.", Alert.AlertType.ERROR);
            }
        });

        Button continueBtn = createStyledButton("Continue", "#f59e0b", "#f97316");
        continueBtn.setPrefWidth(200);
        continueBtn.setPrefHeight(50);
        continueBtn.setOnAction(e -> UIUtil.switchScene(stage, getConfirmationScene()));

        Button backBtn = createStyledButton("Back", "#6b7280", "#374151");
        backBtn.setPrefWidth(200);
        backBtn.setPrefHeight(50);
        backBtn.setOnAction(e -> UIUtil.switchScene(stage, new UserDashboardController(stage).getScene()));

        Button logoutBtn = createStyledButton("Logout", "#ef4444", "#dc2626");
        logoutBtn.setPrefWidth(200);
        logoutBtn.setPrefHeight(50);
        logoutBtn.setOnAction(e -> {
            UserSession.logout(stage);
            UIUtil.switchScene(stage, new UserLoginController(stage).getScene());
        });

        HBox buttonsRow = new HBox(15);
        buttonsRow.setAlignment(Pos.CENTER);
        buttonsRow.getChildren().addAll(continueBtn, backBtn);

        contentBox.getChildren().addAll(heading, issuedLabel, issuedBooksTable, barcodeField, returnBtn, buttonsRow, logoutBtn);

        StackPane mainLayout = new StackPane(contentBox);
        mainLayout.setStyle("-fx-background-image: url('https://images.unsplash.com/photo-1504384308090-c894fdcc538d?ixlib=rb-4.0.3&auto=format&fit=crop&w=1470&q=80'); -fx-background-size: cover; -fx-background-position: center center;");
        StackPane.setAlignment(contentBox, Pos.CENTER);

        return new Scene(mainLayout, UILayoutConstants.SCENE_WIDTH, UILayoutConstants.SCENE_HEIGHT);
    }

    private void refreshIssuedBooksTable() {
        List<Book> bookObjects = new ArrayList<>();
        for (String[] bookData : issuedBooks) {
            Book book = new Book();
            book.setName(bookData[0]);
            book.setBarcode(bookData[1]);
            book.setAuthor(bookData[2]);
            bookObjects.add(book);
        }
        issuedBooksTable.setItems(FXCollections.observableArrayList(bookObjects));

        // Setup columns if not already set
        if (issuedBooksTable.getColumns().isEmpty()) {
            TableColumn<Book, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
            nameCol.setPrefWidth(150);

            TableColumn<Book, String> barcodeCol = new TableColumn<>("Barcode");
            barcodeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBarcode()));
            barcodeCol.setPrefWidth(100);

            TableColumn<Book, String> authorCol = new TableColumn<>("Author");
            authorCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
            authorCol.setPrefWidth(150);

            issuedBooksTable.getColumns().addAll(nameCol, barcodeCol, authorCol);
        }
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
        mainLayout.setStyle("-fx-background-image: url('https://images.unsplash.com/photo-1504384308090-c894fdcc538d?ixlib=rb-4.0.3&auto=format&fit=crop&w=1470&q=80'); -fx-background-size: cover; -fx-background-position: center center;");
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
