// =====================================
// StudentManagementController.java
// Handles all student management tasks:
//  - View and manage students with table view
//  - Add, edit, delete students
// =====================================

package com.library.controller;

import com.library.dao.StudentDAO;
import com.library.model.Student;
import com.library.model.Session;
import com.library.service.RegistrationService;
import com.library.util.UIUtil;
import com.library.util.EmailService;
import com.library.util.EmailValidator;
import com.library.util.UILayoutConstants;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class StudentManagementController {
    private Stage stage;
    private StudentDAO studentDAO = new StudentDAO();
    private EmailService emailService = new EmailService();
    private ObservableList<Student> students = FXCollections.observableArrayList();
    private ObservableList<Student> filteredStudents = FXCollections.observableArrayList();
    private TableView<Student> studentTable = new TableView<>();
    private TextField searchField = new TextField();
    private TextField nameField = new TextField();
    private TextField idField = new TextField();
    private TextField emailField = new TextField();
    private TextField mobileField = new TextField();
    private TextField courseField = new TextField();
    private TextField rfidField = new TextField();

    public StudentManagementController(Stage stage) {
        this.stage = stage;
        loadStudents();
    }

    public Scene getScene() {
        if (Session.getLoggedInAdmin() == null) {
            UIUtil.switchScene(stage, new LoginController(stage).getScene());
            return null;
        }

        // Content container with semi-transparent background
        BorderPane mainLayout = new BorderPane();

        // Top bar with back button
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(0, 0, 10, 0));

        Button backBtn = new Button("⬅ Back to Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #6b7280; -fx-text-fill: #6b7280; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #374151; -fx-text-fill: #374151; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #6b7280; -fx-text-fill: #6b7280; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;"));
        backBtn.setOnAction(e -> UIUtil.switchScene(stage, new DashboardController(stage).getScene()));
        backBtn.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) backBtn.fire(); });

        topBar.getChildren().addAll(backBtn);

        // Center layout
        HBox centerLayout = new HBox(20);
        centerLayout.setPadding(UILayoutConstants.PADDING);
        centerLayout.setAlignment(UILayoutConstants.CENTER_ALIGNMENT);

        // Left side: Student form
        VBox studentForm = new VBox(15);
        studentForm.setPadding(new Insets(20));
        studentForm.setAlignment(Pos.TOP_LEFT);
        studentForm.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 4); -fx-pref-width: 400;");

        Label formTitle = new Label("Student Details");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        nameField.setPromptText("Name");
        idField.setPromptText("Student ID");
        emailField.setPromptText("Email");
        mobileField.setPromptText("Mobile");
        courseField.setPromptText("Course");
        rfidField.setPromptText("RFID");

        Button addStudentBtn = new Button("Add Student");
        addStudentBtn.setOnAction(e -> addStudent());
        addStudentBtn.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) addStudentBtn.fire(); });

        Button blockStudentBtn = new Button("Block Student");
        blockStudentBtn.setOnAction(e -> blockStudent());
        blockStudentBtn.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) blockStudentBtn.fire(); });

        Button blockedStudentsBtn = new Button("Blocked Students");
        blockedStudentsBtn.setOnAction(e -> UIUtil.switchScene(stage, getBlockedStudentsScene()));
        blockedStudentsBtn.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) blockedStudentsBtn.fire(); });

        Button updateStudentBtn = new Button("Update Student");
        updateStudentBtn.setOnAction(e -> updateStudent());
        updateStudentBtn.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) updateStudentBtn.fire(); });

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> clearForm());
        clearBtn.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) clearBtn.fire(); });


        // Arrange buttons in a grid for a cleaner layout
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);
        buttonGrid.add(addStudentBtn, 0, 0);
        buttonGrid.add(updateStudentBtn, 1, 0);
        buttonGrid.add(blockStudentBtn, 0, 1);
        buttonGrid.add(clearBtn, 1, 1);

        // Create a separate VBox to hold the button grid and the full-width button
        // This ensures the "Blocked Students" button aligns with the grid width.
        VBox buttonContainer = new VBox(10); // 10px spacing
        buttonContainer.getChildren().addAll(buttonGrid, blockedStudentsBtn);

        // Use UIUtil for consistent styling
        UIUtil.setButtonStyle(addStudentBtn, "#10b981", "#059669");
        UIUtil.setButtonStyle(updateStudentBtn, "#f59e0b", "#f97316");
        UIUtil.setButtonStyle(blockStudentBtn, "#ef4444", "#dc2626");
        UIUtil.setButtonStyle(blockedStudentsBtn, "#7c3aed", "#5b21b6");
        UIUtil.setButtonStyle(clearBtn, "#6b7280", "#4b5563");

        // Make buttons in the grid grow to the same width
        addStudentBtn.setMaxWidth(Double.MAX_VALUE);
        updateStudentBtn.setMaxWidth(Double.MAX_VALUE);
        blockStudentBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        blockedStudentsBtn.setMaxWidth(Double.MAX_VALUE); // Also apply to the standalone button

        studentForm.getChildren().addAll(formTitle, nameField, idField, emailField, mobileField, courseField, rfidField, buttonContainer);

        // Right side: Student table
        VBox rightSide = new VBox(15);
        rightSide.setPadding(new Insets(20));
        rightSide.setAlignment(Pos.TOP_LEFT);
        rightSide.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 4); -fx-pref-width: 600;");

        searchField.setPromptText("Search students...");
        searchField.setStyle("-fx-background-radius: 8; -fx-padding: 8 12 8 12; -fx-pref-width: 300;");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterStudents(newValue));

        Label tableTitle = new Label("Students");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        setupStudentTable();
        setupStudentTableContextMenu();
        studentTable.setItems(students);
        studentTable.setOnMouseClicked(e -> selectStudent());

        rightSide.getChildren().addAll(searchField, tableTitle, studentTable);

        centerLayout.getChildren().addAll(studentForm, rightSide);

        return UIUtil.createScene(topBar, centerLayout);
    }

    private void setupStudentTable() {
        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(120);

        TableColumn<Student, String> idCol = new TableColumn<>("Student ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        idCol.setPrefWidth(100);

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(150);

        TableColumn<Student, String> mobileCol = new TableColumn<>("Mobile");
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        mobileCol.setPrefWidth(100);

        TableColumn<Student, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("course"));
        courseCol.setPrefWidth(100);

        TableColumn<Student, String> rfidCol = new TableColumn<>("RFID");
        rfidCol.setCellValueFactory(new PropertyValueFactory<>("rfid"));
        rfidCol.setPrefWidth(100);

        studentTable.getColumns().setAll(nameCol, idCol, emailCol, mobileCol, courseCol, rfidCol);
        studentTable.setPrefHeight(400);
    }

    private void loadStudents() {
        students.clear();
        students.addAll(studentDAO.getAllStudents(true)); // Load active students
    }

    private void addStudent() {
        String name = nameField.getText();
        String id = idField.getText();
        String email = emailField.getText();
        String mobile = mobileField.getText();
        String course = courseField.getText();
        String rfid = rfidField.getText();

        if (name.isEmpty() || id.isEmpty() || email.isEmpty() || mobile.isEmpty() || course.isEmpty() || rfid.isEmpty()) {
            UIUtil.showAlert("Error", "Please fill all fields", Alert.AlertType.ERROR);
            return;
        }

        // Validate email format
        String emailValidationError = EmailValidator.validateEmail(email);
        if (!emailValidationError.isEmpty()) {
            UIUtil.showAlert("Error", emailValidationError, Alert.AlertType.ERROR);
            return;
        }

        // Check for duplicate registration
        RegistrationService.RegistrationResult validationResult = RegistrationService.validateRegistration(email, mobile);
        if (!validationResult.isSuccess()) {
            // Show popup with existing account details
            String message = "Registration failed: " + validationResult.getErrorMessage() + "\n\n" +
                           "Existing Account Details:\n" +
                           validationResult.getExistingAccount().toString();
            UIUtil.showAlert("Duplicate Account Found", message, Alert.AlertType.WARNING);
            return;
        }

        Student student = new Student();
        student.setName(name);
        student.setStudentId(id);
        student.setEmail(email);
        student.setMobile(mobile);
        student.setCourse(course);
        student.setRfid(rfid);
        student.setActive(true);

        if (studentDAO.register(student)) {
            loadStudents();
            clearForm();
            UIUtil.showAlert("Success", "Student added successfully", Alert.AlertType.INFORMATION);

            // Send registration confirmation email
            sendStudentRegistrationEmail(student);
        } else {
            UIUtil.showAlert("Error", "Failed to add student", Alert.AlertType.ERROR);
        }
    }

    private void updateStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtil.showAlert("Error", "Please select a student to update", Alert.AlertType.ERROR);
            return;
        }

        String name = nameField.getText();
        String id = idField.getText();
        String email = emailField.getText();
        String mobile = mobileField.getText();
        String course = courseField.getText();
        String rfid = rfidField.getText();

        if (name.isEmpty() || id.isEmpty() || email.isEmpty() || mobile.isEmpty() || course.isEmpty() || rfid.isEmpty()) {
            UIUtil.showAlert("Error", "Please fill all fields", Alert.AlertType.ERROR);
            return;
        }

        selected.setName(name);
        selected.setStudentId(id);
        selected.setEmail(email);
        selected.setMobile(mobile);
        selected.setCourse(course);
        selected.setRfid(rfid);

        if (studentDAO.updateStudent(selected)) {
            loadStudents();
            clearForm();
            UIUtil.showAlert("Success", "Student updated successfully", Alert.AlertType.INFORMATION);
        } else {
            UIUtil.showAlert("Error", "Failed to update student", Alert.AlertType.ERROR);
        }
    }

    private void selectStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nameField.setText(selected.getName());
            idField.setText(selected.getStudentId());
            emailField.setText(selected.getEmail());
            mobileField.setText(selected.getMobile());
            courseField.setText(selected.getCourse());
            rfidField.setText(selected.getRfid());
        }
    }

    private void setupStudentTableContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> deleteStudent());
        contextMenu.getItems().add(deleteItem);
        studentTable.setContextMenu(contextMenu);
    }

    private void deleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtil.showAlert("Error", "Please select a student to delete", Alert.AlertType.ERROR);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Student");
        alert.setHeaderText("Are you sure you want to delete this student?");
        alert.setContentText("This action cannot be undone.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (studentDAO.deleteStudent(selected.getId())) {
                    loadStudents();
                    clearForm();
                    UIUtil.showAlert("Success", "Student deleted successfully", Alert.AlertType.INFORMATION);

                    // Send deletion notification email
                    emailService.sendStudentDeletionNotification(selected.getEmail(), selected.getName());
                } else {
                    UIUtil.showAlert("Error", "Cannot delete student with issued books", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void blockStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtil.showAlert("Error", "Please select a student to block", Alert.AlertType.ERROR);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Block Student");
        alert.setHeaderText("Are you sure you want to block this student?");
        alert.setContentText("The student will lose access to the system.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (studentDAO.blockStudent(selected.getId())) {
                    loadStudents();
                    clearForm();
                    UIUtil.showAlert("Success", "Student blocked successfully", Alert.AlertType.INFORMATION);
                } else {
                    UIUtil.showAlert("Error", "Failed to block student", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void clearForm() {
        nameField.clear();
        idField.clear();
        emailField.clear();
        mobileField.clear();
        courseField.clear();
        rfidField.clear();
        studentTable.getSelectionModel().clearSelection();
    }

    private void filterStudents(String query) {
        if (query == null || query.isEmpty()) {
            studentTable.setItems(students);
        } else {
            filteredStudents.clear();
            String lowerQuery = query.toLowerCase();
            for (Student student : students) {
                if (student.getName().toLowerCase().contains(lowerQuery) ||
                    student.getStudentId().toLowerCase().contains(lowerQuery) ||
                    student.getEmail().toLowerCase().contains(lowerQuery) ||
                    student.getMobile().toLowerCase().contains(lowerQuery) ||
                    student.getCourse().toLowerCase().contains(lowerQuery) ||
                    student.getRfid().toLowerCase().contains(lowerQuery)) {
                    filteredStudents.add(student);
                }
            }
            studentTable.setItems(filteredStudents);
        }
    }

    private Scene getBlockedStudentsScene() {
        // Content container with semi-transparent background
        BorderPane mainLayout = new BorderPane();

        // Top bar with back button
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(0, 0, 10, 0));

        Button backBtn = new Button("⬅ Back to Students");
        backBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #6b7280; -fx-text-fill: #6b7280; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #374151; -fx-text-fill: #374151; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #6b7280; -fx-text-fill: #6b7280; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;"));
        backBtn.setOnAction(e -> UIUtil.switchScene(stage, getScene()));

        topBar.getChildren().add(backBtn);

        // Center layout
        VBox centerLayout = new VBox(15);
        centerLayout.setPadding(UILayoutConstants.PADDING);
        centerLayout.setAlignment(UILayoutConstants.CENTER_ALIGNMENT);

        Label tableTitle = new Label("Blocked Students");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        TableView<Student> blockedStudentTable = new TableView<>();
        setupStudentTableForBlocked(blockedStudentTable);
        setupBlockedStudentTableContextMenu(blockedStudentTable);

        ObservableList<Student> blockedStudents = FXCollections.observableArrayList();
        blockedStudents.addAll(studentDAO.getAllStudents(false)); // Load inactive students
        blockedStudentTable.setItems(blockedStudents);
        blockedStudentTable.setPrefHeight(400);

        centerLayout.getChildren().addAll(tableTitle, blockedStudentTable);

        return UIUtil.createScene(topBar, centerLayout);
    }

    private void setupStudentTableForBlocked(TableView<Student> table) {
        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(120);

        TableColumn<Student, String> idCol = new TableColumn<>("Student ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        idCol.setPrefWidth(100);

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(150);

        TableColumn<Student, String> mobileCol = new TableColumn<>("Mobile");
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        mobileCol.setPrefWidth(100);

        TableColumn<Student, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("course"));
        courseCol.setPrefWidth(100);

        TableColumn<Student, String> rfidCol = new TableColumn<>("RFID");
        rfidCol.setCellValueFactory(new PropertyValueFactory<>("rfid"));
        rfidCol.setPrefWidth(100);

        table.getColumns().setAll(nameCol, idCol, emailCol, mobileCol, courseCol, rfidCol);
    }

    private void setupBlockedStudentTableContextMenu(TableView<Student> table) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem unblockItem = new MenuItem("Unblock");
        unblockItem.setOnAction(e -> unblockStudent(table));
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> deleteBlockedStudent(table));
        contextMenu.getItems().addAll(unblockItem, deleteItem);
        table.setContextMenu(contextMenu);
    }

    private void unblockStudent(TableView<Student> table) {
        Student selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtil.showAlert("Error", "Please select a student to unblock", Alert.AlertType.ERROR);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unblock Student");
        alert.setHeaderText("Are you sure you want to unblock this student?");
        alert.setContentText("The student will regain access to the system.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (studentDAO.unblockStudent(selected.getId())) {
                    table.getItems().remove(selected);
                    UIUtil.showAlert("Success", "Student unblocked successfully", Alert.AlertType.INFORMATION);
                } else {
                    UIUtil.showAlert("Error", "Failed to unblock student", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void deleteBlockedStudent(TableView<Student> table) {
        Student selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtil.showAlert("Error", "Please select a student to delete", Alert.AlertType.ERROR);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Student");
        alert.setHeaderText("Are you sure you want to delete this student?");
        alert.setContentText("This action cannot be undone.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (studentDAO.deleteStudent(selected.getId())) {
                    table.getItems().remove(selected);
                    UIUtil.showAlert("Success", "Student deleted successfully", Alert.AlertType.INFORMATION);

                    // Send deletion notification email
                    emailService.sendStudentDeletionNotification(selected.getEmail(), selected.getName());
                } else {
                    UIUtil.showAlert("Error", "Cannot delete student with issued books", Alert.AlertType.ERROR);
                }
            }
        });
    }

    /**
     * Sends a registration confirmation email to the newly registered student.
     *
     * @param student the student who was registered
     */
    private void sendStudentRegistrationEmail(Student student) {
        String subject = "Welcome to Library Management System";
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Dear ").append(student.getName()).append(",\n\n");
        emailBody.append("Welcome to the Library Management System!\n\n");
        emailBody.append("Your account has been successfully created with the following details:\n");
        emailBody.append("• Student ID: ").append(student.getStudentId()).append("\n");
        emailBody.append("• Name: ").append(student.getName()).append("\n");
        emailBody.append("• Course: ").append(student.getCourse()).append("\n");
        emailBody.append("• Email: ").append(student.getEmail()).append("\n\n");
        emailBody.append("You can now log in to the student portal using your student ID.\n\n");
        emailBody.append("If you have any questions, please contact the library administration.\n\n");
        emailBody.append("Best regards,\n");
        emailBody.append("Library Management Team\n\n");
        emailBody.append("(This is an automated message. Please do not reply.)");

        emailService.sendEmail(student.getEmail(), subject, emailBody.toString());
    }
}
