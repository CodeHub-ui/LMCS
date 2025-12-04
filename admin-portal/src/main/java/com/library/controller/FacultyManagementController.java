package com.library.controller;

import com.library.dao.FacultyDAO;
import com.library.dao.StudentDAO;
import com.library.model.Faculty;
import com.library.model.Student;
import com.library.service.RegistrationService;
import com.library.util.EmailService;
import com.library.util.UIUtil;
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

/**
 * FacultyManagementController handles faculty management operations.
 */
// import javafx.concurrent.Task; import will be added
import javafx.concurrent.Task;

public class FacultyManagementController {
    private Stage stage;
    private FacultyDAO facultyDAO = new FacultyDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private EmailService emailService = new EmailService();
    private ObservableList<Faculty> faculty = FXCollections.observableArrayList();
    private ObservableList<Faculty> filteredFaculty = FXCollections.observableArrayList();
    private TableView<Faculty> facultyTable = new TableView<>();
    private TextField searchField = new TextField();
    private TextField nameField = new TextField();
    private TextField idField = new TextField();
    private TextField emailField = new TextField();
    private TextField mobileField = new TextField();
    private TextField rfidField = new TextField();

    // Add Clear All Faculty button and handler
    private Button clearAllFacultyBtn = new Button("Clear All Faculty");

public FacultyManagementController(Stage stage) {
    this.stage = stage;
    // Show alert or console log to confirm scene construction
    System.out.println("FacultyManagementController initialized");
    // You can alternatively use UIUtil.showAlert here if desired, but console log is less intrusive
    // UIUtil.showAlert("Info", "Faculty Management Scene Loaded", Alert.AlertType.INFORMATION);

    // Initially load faculty data asynchronously
    loadFaculty();

    // Set prompt text for fields
    this.nameField.setPromptText("Name");
    this.idField.setPromptText("Faculty ID");
    this.emailField.setPromptText("Email");
    this.mobileField.setPromptText("Mobile");
    this.rfidField.setPromptText("RFID");
    this.searchField.setPromptText("Search faculty...");
    this.searchField.textProperty().addListener((observable, oldValue, newValue) -> filterFaculty(newValue));

    clearAllFacultyBtn.setStyle("-fx-background-color: linear-gradient(#ef4444, #dc2626); -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 16 10 16; -fx-cursor: hand;");
    clearAllFacultyBtn.setOnAction(e -> clearAllFaculty());
}

    public Scene getScene() {
        return getManageScene();
    }



    // ============================
    // Scene 5: Manage Faculty
    // ============================
private Scene getManageScene() {
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

    topBar.getChildren().add(backBtn);

    // Center layout
    HBox centerLayout = new HBox(20);
    centerLayout.setPadding(UILayoutConstants.PADDING);
    centerLayout.setAlignment(UILayoutConstants.CENTER_ALIGNMENT);

    // Left side: Faculty form
    VBox facultyForm = new VBox(15);
    facultyForm.setPadding(UILayoutConstants.PADDING);
    facultyForm.setAlignment(Pos.TOP_LEFT);
    facultyForm.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 4); -fx-pref-width: 400;");

    Label formTitle = new Label("Faculty Details");
    formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

    // Add the Clear All Faculty button to the form
    clearAllFacultyBtn.setMaxWidth(Double.MAX_VALUE);
    // facultyForm.getChildren().addAll(formTitle, this.nameField, this.idField, this.emailField, this.mobileField, this.rfidField, clearAllFacultyBtn);


        Button updateFacultyBtn = new Button("Update Faculty");
        updateFacultyBtn.setOnAction(e -> updateFaculty());

        Button addFacultyBtn = new Button("Add Faculty");
        addFacultyBtn.setOnAction(e -> addFaculty());

        Button blockFacultyBtn = new Button("Block Faculty");
        blockFacultyBtn.setOnAction(e -> blockFaculty());

        Button blockedFacultyBtn = new Button("Blocked Faculty");
        blockedFacultyBtn.setOnAction(e -> UIUtil.switchScene(stage, getBlockedFacultyScene()));

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> clearForm());

        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);
        buttonGrid.add(addFacultyBtn, 0, 0);
        buttonGrid.add(updateFacultyBtn, 1, 0);
        buttonGrid.add(blockFacultyBtn, 0, 1);
        buttonGrid.add(clearBtn, 1, 1);

        VBox buttonContainer = new VBox(10);
        buttonContainer.getChildren().addAll(buttonGrid, blockedFacultyBtn);

        UIUtil.setButtonStyle(addFacultyBtn, "#10b981", "#059669");
        UIUtil.setButtonStyle(updateFacultyBtn, "#f59e0b", "#f97316");
        UIUtil.setButtonStyle(blockFacultyBtn, "#ef4444", "#dc2626");
        UIUtil.setButtonStyle(blockedFacultyBtn, "#7c3aed", "#5b21b6");
        UIUtil.setButtonStyle(clearBtn, "#6b7280", "#4b5563");

        addFacultyBtn.setMaxWidth(Double.MAX_VALUE);
        updateFacultyBtn.setMaxWidth(Double.MAX_VALUE);
        blockFacultyBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        blockedFacultyBtn.setMaxWidth(Double.MAX_VALUE);

        facultyForm.getChildren().addAll(formTitle, this.nameField, this.idField, this.emailField, this.mobileField, this.rfidField, buttonContainer);

        // Right side: Faculty table
        VBox rightSide = new VBox(15);
        rightSide.setPadding(UILayoutConstants.PADDING);
        rightSide.setAlignment(Pos.TOP_LEFT);
        rightSide.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 4); -fx-pref-width: 600;");

        Label tableTitle = new Label("Faculty");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        setupFacultyTable(facultyTable);
        setupFacultyTableContextMenu();
        facultyTable.setItems(faculty);
        facultyTable.setOnMouseClicked(e -> selectFaculty());

        rightSide.getChildren().addAll(tableTitle, searchField, facultyTable);
        centerLayout.getChildren().addAll(facultyForm, rightSide);

        return UIUtil.createScene(topBar, centerLayout);
    }

    private void setupFacultyTable(TableView<Faculty> facultyTable) {
        TableColumn<Faculty, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(120);

        TableColumn<Faculty, String> idCol = new TableColumn<>("Faculty ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("facultyId"));
        idCol.setPrefWidth(100);

        TableColumn<Faculty, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(150);

        TableColumn<Faculty, String> mobileCol = new TableColumn<>("Mobile");
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        mobileCol.setPrefWidth(100);

        TableColumn<Faculty, String> rfidCol = new TableColumn<>("RFID");
        rfidCol.setCellValueFactory(new PropertyValueFactory<>("rfid"));
        rfidCol.setPrefWidth(100);

        facultyTable.getColumns().setAll(nameCol, idCol, emailCol, mobileCol, rfidCol);
        facultyTable.setPrefHeight(400);
    }

    private void loadFaculty() {
        Task<List<Faculty>> loadFacultyTask = new Task<List<Faculty>>() {
            @Override
            protected List<Faculty> call() throws Exception {
                return facultyDAO.getAllFaculty(true);
            }
        };

        loadFacultyTask.setOnSucceeded(event -> {
            List<Faculty> result = loadFacultyTask.getValue();
            faculty.clear();
            faculty.addAll(result);
        });

        loadFacultyTask.setOnFailed(event -> {
            System.err.println("Failed to load faculty: " + loadFacultyTask.getException());
            UIUtil.showAlert("Error", "Failed to load faculty data.", javafx.scene.control.Alert.AlertType.ERROR);
        });

        Thread thread = new Thread(loadFacultyTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateFaculty() {
        Faculty selected = facultyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtil.showAlert("Error", "Please select a faculty to update", Alert.AlertType.ERROR);
            return;
        }

        String name = this.nameField.getText();
        String id = this.idField.getText();
        String email = this.emailField.getText();
        String mobile = this.mobileField.getText();
        String rfid = this.rfidField.getText();

        if (name.isEmpty() || id.isEmpty() || email.isEmpty() || mobile.isEmpty() || rfid.isEmpty()) {
            UIUtil.showAlert("Error", "Please fill all fields", Alert.AlertType.ERROR);
            return;
        }

        selected.setName(name);
        selected.setFacultyId(id);
        selected.setEmail(email);
        selected.setMobile(mobile);
        selected.setRfid(rfid);

        if (facultyDAO.updateFaculty(selected)) {
            loadFaculty();
            clearForm();
            UIUtil.showAlert("Success", "Faculty updated successfully", Alert.AlertType.INFORMATION);
        } else {
            UIUtil.showAlert("Error", "Failed to update faculty", Alert.AlertType.ERROR);
        }
    }

    private void selectFaculty() {
        Faculty selected = facultyTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nameField.setText(selected.getName());
            idField.setText(selected.getFacultyId());
            emailField.setText(selected.getEmail());
            mobileField.setText(selected.getMobile());
            rfidField.setText(selected.getRfid());
        }
    }

    private void blockFaculty() {
        Faculty selected = facultyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtil.showAlert("Error", "Please select a faculty to block", Alert.AlertType.ERROR);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Block Faculty");
        alert.setHeaderText("Are you sure you want to block this faculty?");
        alert.setContentText("The faculty will lose access to the system.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (facultyDAO.blockFaculty(selected.getId())) {
                    loadFaculty();
                    clearForm();
                    UIUtil.showAlert("Success", "Faculty blocked successfully", Alert.AlertType.INFORMATION);
                } else {
                    UIUtil.showAlert("Error", "Failed to block faculty", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void clearForm() {
        this.nameField.clear();
        this.idField.clear();
        this.emailField.clear();
        this.mobileField.clear();
        this.rfidField.clear();
        facultyTable.getSelectionModel().clearSelection();
    }

    private void addFaculty() {
        String name = this.nameField.getText();
        String id = this.idField.getText();
        String email = this.emailField.getText();
        String mobile = this.mobileField.getText();
        String rfid = this.rfidField.getText();

        if (name.isEmpty() || id.isEmpty() || email.isEmpty() || mobile.isEmpty() || rfid.isEmpty()) {
            UIUtil.showAlert("Error", "Please fill all fields", Alert.AlertType.ERROR);
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

        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setFacultyId(id);
        faculty.setEmail(email);
        faculty.setMobile(mobile);
        faculty.setRfid(rfid);
        faculty.setActive(true);

        if (facultyDAO.register(faculty)) {
            loadFaculty();
            clearForm();
            UIUtil.showAlert("Success", "Faculty added successfully", Alert.AlertType.INFORMATION);
        } else {
            UIUtil.showAlert("Error", "Failed to add faculty", Alert.AlertType.ERROR);
        }
    }

    private void setupFacultyTableContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem blockItem = new MenuItem("Block");
        blockItem.setOnAction(e -> blockFaculty());
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> deleteFaculty());
        contextMenu.getItems().addAll(blockItem, deleteItem);
        facultyTable.setContextMenu(contextMenu);
    }



    private void deleteFaculty() {
        Faculty selected = facultyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtil.showAlert("Error", "Please select a faculty to delete", Alert.AlertType.ERROR);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Faculty");
        alert.setHeaderText("Are you sure you want to delete this faculty?");
        alert.setContentText("This action cannot be undone.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (facultyDAO.deleteFaculty(selected.getId())) {
                    loadFaculty();
                    clearForm();
                    UIUtil.showAlert("Success", "Faculty deleted successfully", Alert.AlertType.INFORMATION);

                    // Send deletion notification email
                    emailService.sendFacultyDeletionNotification(selected.getEmail(), selected.getName());
                } else {
                    UIUtil.showAlert("Error", "Failed to delete faculty", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private Scene getBlockedFacultyScene() {
        // Content container with semi-transparent background
        BorderPane mainLayout = new BorderPane();

        // Top bar with back button
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(0, 0, 10, 0));

        Button backBtn = new Button("⬅ Back to Faculty");
        backBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #6b7280; -fx-text-fill: #6b7280; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #374151; -fx-text-fill: #374151; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #6b7280; -fx-text-fill: #6b7280; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;"));
        backBtn.setOnAction(e -> UIUtil.switchScene(stage, getScene()));

        topBar.getChildren().add(backBtn);

        // Center layout
        VBox centerLayout = new VBox(15);
        centerLayout.setPadding(new Insets(20));
        centerLayout.setAlignment(Pos.TOP_CENTER);

        Label tableTitle = new Label("Blocked Faculty");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        TableView<Faculty> blockedFacultyTable = new TableView<>();
        setupFacultyTableForBlocked(blockedFacultyTable);
        setupBlockedFacultyTableContextMenu(blockedFacultyTable);

        ObservableList<Faculty> blockedFaculty = FXCollections.observableArrayList();
        blockedFaculty.addAll(facultyDAO.getAllFaculty(false)); // Load inactive faculty
        blockedFacultyTable.setItems(blockedFaculty);
        blockedFacultyTable.setPrefHeight(400);

        centerLayout.getChildren().addAll(tableTitle, blockedFacultyTable);

        return UIUtil.createScene(topBar, centerLayout);
    }

    private void setupFacultyTableForBlocked(TableView<Faculty> table) {
        TableColumn<Faculty, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(120);

        TableColumn<Faculty, String> idCol = new TableColumn<>("Faculty ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("facultyId"));
        idCol.setPrefWidth(100);

        TableColumn<Faculty, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(150);

        TableColumn<Faculty, String> mobileCol = new TableColumn<>("Mobile");
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        mobileCol.setPrefWidth(100);

        TableColumn<Faculty, String> rfidCol = new TableColumn<>("RFID");
        rfidCol.setCellValueFactory(new PropertyValueFactory<>("rfid"));
        rfidCol.setPrefWidth(100);

        table.getColumns().setAll(nameCol, idCol, emailCol, mobileCol, rfidCol);
    }

    private void setupBlockedFacultyTableContextMenu(TableView<Faculty> table) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem unblockItem = new MenuItem("Unblock");
        unblockItem.setOnAction(e -> unblockFaculty(table));
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> deleteBlockedFaculty(table));
        contextMenu.getItems().addAll(unblockItem, deleteItem);
        table.setContextMenu(contextMenu);
    }

    private void unblockFaculty(TableView<Faculty> table) {
        Faculty selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtil.showAlert("Error", "Please select a faculty to unblock", Alert.AlertType.ERROR);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unblock Faculty");
        alert.setHeaderText("Are you sure you want to unblock this faculty?");
        alert.setContentText("The faculty will regain access to the system.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (facultyDAO.unblockFaculty(selected.getId())) {
                    table.getItems().remove(selected);
                    UIUtil.showAlert("Success", "Faculty unblocked successfully", Alert.AlertType.INFORMATION);
                } else {
                    UIUtil.showAlert("Error", "Failed to unblock faculty", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void deleteBlockedFaculty(TableView<Faculty> table) {
        Faculty selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtil.showAlert("Error", "Please select a faculty to delete", Alert.AlertType.ERROR);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Faculty");
        alert.setHeaderText("Are you sure you want to delete this faculty?");
        alert.setContentText("This action cannot be undone.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (facultyDAO.deleteFaculty(selected.getId())) {
                    table.getItems().remove(selected);
                    UIUtil.showAlert("Success", "Faculty deleted successfully", Alert.AlertType.INFORMATION);

                    // Send deletion notification email
                    emailService.sendFacultyDeletionNotification(selected.getEmail(), selected.getName());
                } else {
                    UIUtil.showAlert("Error", "Cannot delete faculty with issued books", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void filterFaculty(String query) {
        if (query == null || query.isEmpty()) {
            facultyTable.setItems(faculty);
        } else {
            filteredFaculty.clear();
            String lowerQuery = query.toLowerCase();
            for (Faculty fac : faculty) {
                if (fac.getName().toLowerCase().contains(lowerQuery) ||
                    fac.getFacultyId().toLowerCase().contains(lowerQuery) ||
                    fac.getEmail().toLowerCase().contains(lowerQuery) ||
                    fac.getMobile().toLowerCase().contains(lowerQuery) ||
                    fac.getRfid().toLowerCase().contains(lowerQuery)) {
                    filteredFaculty.add(fac);
                }
            }
            facultyTable.setItems(filteredFaculty);
        }
    }

    private void clearAllFaculty() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear All Faculty");
        alert.setHeaderText("Are you sure you want to clear all faculty entries?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = facultyDAO.deleteAllFaculty();
                if (success) {
                    loadFaculty();
                    UIUtil.showAlert("Success", "All faculty entries cleared successfully.", Alert.AlertType.INFORMATION);
                } else {
                    UIUtil.showAlert("Error", "Failed to clear faculty entries.", Alert.AlertType.ERROR);
                }
            }
        });
    }
}
