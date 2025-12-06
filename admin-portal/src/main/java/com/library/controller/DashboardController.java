package com.library.controller;

import com.library.Main;
import com.library.dao.BookDAO;
import com.library.dao.FacultyDAO;
import com.library.dao.IssuedBookDAO;
import com.library.dao.StudentDAO;
import com.library.model.Session;
import com.library.util.UIUtil;
import com.library.util.UILayoutConstants;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;
import java.sql.SQLException;

public class DashboardController {
    private Stage stage;

    public DashboardController(Stage stage) {
        this.stage = stage;
    }

    /**
     * Creates a styled VBox "card" for navigation.
     * @param text The text to display on the card.
     * @param color The background color of the card.
     * @param handler The action to perform on click.
     * @return A styled, clickable VBox.
     */
    private VBox createCard(String text, String color, EventHandler<MouseEvent> handler) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setSpacing(10);
        card.setPrefSize(220, 100);
        String baseStyle = "-fx-background-color: " + color + "; -fx-background-radius: 12; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: " + color.substring(0, color.length()-2) + "CC; -fx-background-radius: 12; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);";
        card.setStyle(baseStyle);
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(baseStyle));
        card.setOnMouseClicked(handler);

        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: white;");
        card.getChildren().add(label);
        return card;
    }

    /**
     * Creates a styled VBox "card" to display a single statistic.
     * @param title The descriptive title for the statistic (e.g., "Total Books").
     * @param value The numerical value of the statistic.
     * @param icon An emoji or icon string to be displayed.
     * @return A styled VBox representing the stat card.
     */
    private VBox createStatCard(String title, String value, String icon) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 3);");
        card.setPrefWidth(220);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: #475569;");

        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }

    /**
     * Creates and returns the dashboard scene.
     * Checks if an admin is logged in; if not, redirects to login.
     */
    public Scene getScene() {
        // Check if an admin is logged in; if not, switch to login scene
        if (Session.getLoggedInAdmin() == null) {
            UIUtil.switchScene(stage, new LoginController(stage).getScene());
            return null; // Return null as we are redirecting
        }

        // Top bar for logout and reload buttons
        Button logoutBtn = UIUtil.createStyledButton("Logout", "#d01414ff", "#dc2626");
        logoutBtn.setOnAction(e -> {
            Session.logout();
            UIUtil.switchScene(stage, new LoginController(stage).getScene());
        });

        Button reloadBtn = UIUtil.createStyledButton("🔄 Reload", "#07ee5cff", "#009035ff");
        reloadBtn.setOnAction(e -> {
            // Reload the dashboard by switching to a new instance
            UIUtil.switchScene(stage, new DashboardController(stage).getScene());
        });

        // Create a spacer to push buttons to opposite ends
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(10, reloadBtn, spacer, logoutBtn);
        topBar.setPadding(UILayoutConstants.PADDING);

        VBox centerLayout = new VBox(15); // Add 15px spacing between elements
        centerLayout.setPadding(UILayoutConstants.PADDING);
        centerLayout.setAlignment(UILayoutConstants.CENTER_ALIGNMENT);

        Label heading = new Label("Admin Dashboard");
        heading.setStyle("-fx-font-size: 28px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        // Overview GridPane: Displays key statistics in a grid layout
        GridPane overview = new GridPane();
        overview.setHgap(20);
        overview.setVgap(20);
        overview.setAlignment(Pos.CENTER);
        overview.setStyle("-fx-padding: 20;");

        // Labels for statistics, fetching with DAOs
        BookDAO bookDAO = new BookDAO();
        IssuedBookDAO issuedBookDAO = new IssuedBookDAO();
        int totalBooks = bookDAO.getTotalBooks();
        int issuedBooks = 0;
        try {
            issuedBooks = issuedBookDAO.getTotalIssuedBooks();
        } catch (SQLException ex) {
            UIUtil.showAlert("Error", "Failed to fetch issued book count.", Alert.AlertType.ERROR);
        }
        int availableBooks = totalBooks - issuedBooks;
        int pendingReturns = issuedBooks; // Pending returns are the issued books not yet returned

        // Create and add stat cards to the overview grid
        overview.add(createStatCard("Total Books", String.valueOf(totalBooks), "📚"), 0, 0);
        overview.add(createStatCard("Books Issued", String.valueOf(issuedBooks), "📖"), 1, 0);
        overview.add(createStatCard("Available Books", String.valueOf(availableBooks), "✅"), 2, 0);

        overview.add(createStatCard("Pending Returns", String.valueOf(pendingReturns), "⏳"), 0, 1);
        overview.add(createStatCard("Students Registered", String.valueOf(new StudentDAO().getTotalStudents()), "👨‍🎓"), 1, 1);
        overview.add(createStatCard("Faculty Registered", String.valueOf(new FacultyDAO().getTotalFaculty()), "👨‍🏫"), 2, 1);


        // --- Navigation Section ---
        // Use a TilePane for a responsive grid of navigation cards.
        TilePane navGrid = new TilePane();
        navGrid.setPadding(new Insets(20, 0, 0, 0));
        navGrid.setHgap(20);
        navGrid.setVgap(20);
        navGrid.setAlignment(Pos.CENTER);

        // Create navigation cards using the existing createCard helper method
        VBox studentCard = createCard("👨‍🎓 Student Management", "#0f48a3ff", e -> UIUtil.switchScene(stage, new StudentManagementController(stage).getScene()));
        VBox facultyCard = createCard("👨‍🏫 Faculty Management", "#054e5bff", e -> UIUtil.switchScene(stage, new FacultyManagementController(stage).getScene()));
        VBox bookCard = createCard("📚 Book Management", "#b4520bff", e -> UIUtil.switchScene(stage, new BookManagementController(stage).getScene()));
        VBox trackCard = createCard("🔍 Track Activity", "#057b53ff", e -> UIUtil.switchScene(stage, new TrackController(stage).getScene()));
        VBox searchCard = createCard("🔍 Centralized Search", "#af0d5eff", e -> UIUtil.switchScene(stage, new SearchController(stage).getScene()));
        VBox profileCard = createCard("👤 Admin Profile", "#1014ddff", e -> UIUtil.switchScene(stage, new AdminProfileController(stage).getScene()));
        VBox cleanupCard = createCard("🧹 Cleanup Records", "#e00808ff", e -> {
            IssuedBookDAO cleanupDAO = new IssuedBookDAO();
            int removed = cleanupDAO.removeOrphanedIssuedBooks();
            if (removed > 0) {
                UIUtil.showAlert("Success", "Removed " + removed + " orphaned issued book records.", Alert.AlertType.INFORMATION);
                UIUtil.switchScene(stage, new DashboardController(stage).getScene()); // Reload to update stats
            } else {
                UIUtil.showAlert("Info", "No orphaned records found.", Alert.AlertType.INFORMATION);
            }
        });
        VBox entryExitCard = createCard("📊 Entry/Exit Data", "#8b5cf6", e -> Main.getAppHostServices().showDocument("https://docs.google.com/spreadsheets/d/1d4AgSSYDlWorcXEeB355DNpgxIYN_PMLbZ7Th2l0xi4/edit?gid=0#gid=0"));

        navGrid.getChildren().addAll(studentCard, facultyCard, bookCard, trackCard, searchCard, profileCard, cleanupCard, entryExitCard);

        // Add all elements to the center layout (including the new buttons)
        centerLayout.getChildren().addAll(heading, overview, navGrid);

        return UIUtil.createScene(topBar, centerLayout);
    }

    /**
     * Creates the sidebar navigation component.
     * @param currentView The name of the currently active view to highlight it.
     * @return A VBox containing the sidebar.
     */
    public VBox createSidebar(String currentView) {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(250);
        sidebar.setStyle(UILayoutConstants.getSidebarStyle());
        sidebar.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Admin Portal");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 700; -fx-text-fill: " + (UILayoutConstants.isDarkTheme() ? "#f1f5f9;" : "#1e293b;") + " -fx-padding: 0 0 20 0;");

        // Navigation links
        Map<String, Runnable> navLinks = Map.of(
            "Dashboard", () -> UIUtil.switchScene(stage, new DashboardController(stage).getScene()),
            "Books", () -> UIUtil.switchScene(stage, new BookManagementController(stage).getScene()),
            "Students", () -> UIUtil.switchScene(stage, new StudentManagementController(stage).getScene()),
            "Faculty", () -> UIUtil.switchScene(stage, new FacultyManagementController(stage).getScene()),
            "Search", () -> UIUtil.switchScene(stage, new SearchController(stage).getScene()),
            "Profile", () -> UIUtil.switchScene(stage, new AdminProfileController(stage).getScene())
        );

        sidebar.getChildren().add(title);

        navLinks.forEach((text, action) -> {
            Hyperlink link = new Hyperlink(text);
            link.setOnAction(e -> action.run());
            String defaultStyle = "-fx-font-size: 16px; -fx-text-fill: " + (UILayoutConstants.isDarkTheme() ? "#94a3b8;" : "#475569;") + " -fx-padding: 8 12; -fx-border-width: 0; -fx-background-color: transparent;";
            String activeStyle = "-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: " + (UILayoutConstants.isDarkTheme() ? "#ffffff;" : "#0f172a;") + " -fx-background-color: " + (UILayoutConstants.isDarkTheme() ? "#334155;" : "#f1f5f9;") + " -fx-background-radius: 8; -fx-padding: 8 12;";
            
            if (text.equals(currentView)) {
                link.setStyle(activeStyle);
            } else {
                link.setStyle(defaultStyle);
                link.setOnMouseEntered(e -> link.setStyle(activeStyle));
                link.setOnMouseExited(e -> link.setStyle(defaultStyle));
            }
            sidebar.getChildren().add(link);
        });

        return sidebar;
    }
}
