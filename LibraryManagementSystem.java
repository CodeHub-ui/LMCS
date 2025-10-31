import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class LibraryManagementSystem {

    // Store registered admins: ID -> Password
    private static Map<String, String> registeredAdmins = new HashMap<>();
    private static int loginAttempts = 0;
    private static boolean isLocked = false;
    private static String currentAdmin = "";

    // Initialize with a default admin for demo (you can remove this later)
    static {
        registeredAdmins.put("admin", "password");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }

    // Login Page Class
    static class LoginPage extends JFrame {
        private JTextField adminIdField;
        private JPasswordField passwordField;
        private JCheckBox showPasswordCheckBox;
        private JButton loginButton;
        private JButton registerButton;
        private JButton backToHomeButton;
        private JLabel forgotPasswordLabel;
        private JLabel messageLabel;

        public LoginPage() {
            setTitle("Admin Login - Library Management System");
            setSize(400, 360); // Increased height for buttons
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Admin ID
            gbc.gridx = 0; gbc.gridy = 0;
            add(new JLabel("Admin ID:"), gbc);
            gbc.gridx = 1;
            adminIdField = new JTextField(15);
            add(adminIdField, gbc);

            // Password
            gbc.gridx = 0; gbc.gridy = 1;
            add(new JLabel("Password:"), gbc);
            gbc.gridx = 1;
            passwordField = new JPasswordField(15);
            add(passwordField, gbc);

            // Show/Hide Password
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            showPasswordCheckBox = new JCheckBox("Show Password");
            showPasswordCheckBox.addActionListener(e -> {
                if (showPasswordCheckBox.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('*');
                }
            });
            add(showPasswordCheckBox, gbc);

            // Forgot Password Link
            gbc.gridy = 3;
            forgotPasswordLabel = new JLabel("<html><u>Forgot Password?</u></html>");
            forgotPasswordLabel.setForeground(Color.BLUE);
            forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            forgotPasswordLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JOptionPane.showMessageDialog(LoginPage.this, "Contact support to reset password.", "Forgot Password", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            add(forgotPasswordLabel, gbc);

            // Login Button
            gbc.gridy = 4; gbc.gridwidth = 1; gbc.gridx = 0;
            loginButton = new JButton("Login");
            loginButton.addActionListener(new LoginAction());
            add(loginButton, gbc);

            // Register Button - Open the Register Dialog here
            gbc.gridx = 1;
            registerButton = new JButton("Register New Admin");
            registerButton.addActionListener(e -> new RegisterDialog(null).setVisible(true));
            add(registerButton, gbc);

            // Back to Home/User Login Button
            gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
            backToHomeButton = new JButton("Back to User Login");
            backToHomeButton.addActionListener(e -> {
                dispose();
                // You can add code here to show user login window
            });
            add(backToHomeButton, gbc);

            // Message Label
            gbc.gridy = 6;
            messageLabel = new JLabel("");
            messageLabel.setForeground(Color.RED);
            add(messageLabel, gbc);
        }

        private class LoginAction implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isLocked) {
                    messageLabel.setText("Screen is locked. Please wait.");
                    return;
                }

                String id = adminIdField.getText().trim();
                String password = new String(passwordField.getPassword());

                if (id.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginPage.this, "Please fill all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (registeredAdmins.containsKey(id) && registeredAdmins.get(id).equals(password)) {
                    currentAdmin = id;
                    JOptionPane.showMessageDialog(LoginPage.this, "Successfully logged in!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new AdminDashboard().setVisible(true);
                } else {
                    loginAttempts++;
                    if (loginAttempts >= 3) {
                        isLocked = true;
                        messageLabel.setText("Invalid login details. Screen locked for 15 seconds.");
                        loginButton.setEnabled(false);
                        java.util.Timer timer = new java.util.Timer();
                        timer.schedule(new java.util.TimerTask() {
                            @Override
                            public void run() {
                                isLocked = false;
                                loginAttempts = 0;
                                SwingUtilities.invokeLater(() -> {
                                    loginButton.setEnabled(true);
                                    messageLabel.setText("");
                                });
                            }
                        }, 15000); // 15 seconds
                    } else {
                        JOptionPane.showMessageDialog(LoginPage.this, "Invalid login details.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    // Updated RegisterDialog class merged as requested:
    static class RegisterDialog extends JDialog {
        private JTextField newIdField;
        private JPasswordField newPasswordField;
        private JPasswordField confirmPasswordField;
        private Runnable onSuccess;

        public RegisterDialog(Runnable onSuccess) {
            super();
            this.onSuccess = onSuccess;
            setTitle("Register New Admin");
            setModal(true);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // New Admin ID Label and TextField
            gbc.gridx = 0; gbc.gridy = 0;
            add(new JLabel("New Admin ID:"), gbc);
            gbc.gridx = 1;
            newIdField = new JTextField(20);
            add(newIdField, gbc);

            // Password Label and PasswordField
            gbc.gridx = 0; gbc.gridy = 1;
            add(new JLabel("Password:"), gbc);
            gbc.gridx = 1;
            newPasswordField = new JPasswordField(20);
            add(newPasswordField, gbc);

            // Confirm Password Label and PasswordField
            gbc.gridx = 0; gbc.gridy = 2;
            add(new JLabel("Confirm Password:"), gbc);
            gbc.gridx = 1;
            confirmPasswordField = new JPasswordField(20);
            add(confirmPasswordField, gbc);

            // Buttons Panel with Register and Cancel
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            JButton registerBtn = new JButton("Register");
            JButton cancelBtn = new JButton("Cancel");
            buttonPanel.add(registerBtn);
            buttonPanel.add(cancelBtn);
            add(buttonPanel, gbc);

            // Register button action
            registerBtn.addActionListener(e -> {
                String id = newIdField.getText().trim();
                String password = new String(newPasswordField.getPassword());
                String confirm = new String(confirmPasswordField.getPassword());

                if (id.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterDialog.this,
                            "Please fill all fields.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!password.equals(confirm)) {
                    JOptionPane.showMessageDialog(RegisterDialog.this,
                            "Passwords do not match.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (registeredAdmins.containsKey(id)) {
                    JOptionPane.showMessageDialog(RegisterDialog.this,
                            "Admin ID already exists.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                registeredAdmins.put(id, password);
                JOptionPane.showMessageDialog(RegisterDialog.this,
                        "Registration successful!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                if (onSuccess != null) {
                    onSuccess.run();
                }
                dispose();
            });

            // Cancel button action simply closes dialog
            cancelBtn.addActionListener(e -> dispose());

            pack(); // Auto-size dialog based on content
            setLocationRelativeTo(null); // Center on screen
        }
    }

    // Rest of the classes remain unchanged (AdminDashboard, ManageAdminsDialog etc.)
    static class AdminDashboard extends JFrame {
        public AdminDashboard() {
            setTitle("Admin Dashboard - Library Management System");
            setSize(1000, 700);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            // Header / Navigation Bar
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(Color.LIGHT_GRAY);
            headerPanel.setPreferredSize(new Dimension(1000, 50));
            JLabel libraryName = new JLabel("Library Management System");
            libraryName.setFont(new Font("Arial", Font.BOLD, 20));
            headerPanel.add(libraryName, BorderLayout.WEST);
            JPanel adminPanel = new JPanel(new FlowLayout());
            adminPanel.add(new JLabel("Admin: " + currentAdmin));
            JButton logoutButton = new JButton("Logout");
            logoutButton.addActionListener(e -> {
                dispose();
                new LoginPage().setVisible(true);
            });
            adminPanel.add(logoutButton);
            headerPanel.add(adminPanel, BorderLayout.EAST);
            add(headerPanel, BorderLayout.NORTH);

            // Main Content Panel (Summary and buttons)
            JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Overview Cards (Summary Stats)
            JPanel overviewPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            overviewPanel.setBorder(BorderFactory.createTitledBorder("Overview"));
            overviewPanel.add(new JLabel("Total Books: 1000"));
            overviewPanel.add(new JLabel("Books Issued: 200"));
            overviewPanel.add(new JLabel("Books Available: 800"));
            overviewPanel.add(new JLabel("Total Members: 500"));
            overviewPanel.add(new JLabel("Pending Returns: 50"));
            overviewPanel.add(new JLabel("Fines Collected: ₹5000"));
            mainPanel.add(overviewPanel);

            // Book Management
            JPanel bookPanel = new JPanel(new GridLayout(3, 1, 5, 5));
            bookPanel.setBorder(BorderFactory.createTitledBorder("Book Management"));
            JButton addBookButton = new JButton("Add/Edit/Delete Book");
            JButton categoriesButton = new JButton("Book Categories");
            JButton recentBooksButton = new JButton("Recently Added Books");
            bookPanel.add(addBookButton);
            bookPanel.add(categoriesButton);
            bookPanel.add(recentBooksButton);
            mainPanel.add(bookPanel);

            // Member Management
            JPanel memberPanel = new JPanel(new GridLayout(4, 1, 5, 5));
            memberPanel.setBorder(BorderFactory.createTitledBorder("Member Management"));
            memberPanel.add(new JLabel("Total Members: 500"));
            memberPanel.add(new JLabel("New Registrations: 20"));
            memberPanel.add(new JLabel("Blocked/Inactive: 10"));
            JButton manageMembersButton = new JButton("Manage Memberships");
            memberPanel.add(manageMembersButton);
            mainPanel.add(memberPanel);

            // Issue/Return Records
            JPanel issuePanel = new JPanel(new GridLayout(4, 1, 5, 5));
            issuePanel.setBorder(BorderFactory.createTitledBorder("Issue/Return Records"));
            issuePanel.add(new JLabel("Currently Issued: 200"));
            issuePanel.add(new JLabel("Due Dates: Check List"));
            issuePanel.add(new JLabel("Pending Returns: 50"));
            JButton fineCalcButton = new JButton("Fine Calculation");
            issuePanel.add(fineCalcButton);
            mainPanel.add(issuePanel);

            // Bottom Panel for other sections
            JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 10, 10));
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Notifications/Alerts
            JPanel notificationPanel = new JPanel(new GridLayout(3, 1, 5, 5));
            notificationPanel.setBorder(BorderFactory.createTitledBorder("Notifications/Alerts"));
            notificationPanel.add(new JLabel("Overdue Alerts: 10"));
            notificationPanel.add(new JLabel("Low Stock Alerts: 5"));
            notificationPanel.add(new JLabel("New Registrations: 20"));
            bottomPanel.add(notificationPanel);

            // Reports & Analytics
            JPanel reportsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
            reportsPanel.setBorder(BorderFactory.createTitledBorder("Reports & Analytics"));
            reportsPanel.add(new JLabel("Most Issued Books"));
            reportsPanel.add(new JLabel("Top Readers"));
            reportsPanel.add(new JLabel("Monthly Stats (Chart Placeholder)"));
            reportsPanel.add(new JLabel("Fine Collection Graph"));
            bottomPanel.add(reportsPanel);

            // System Settings
            JPanel settingsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
            settingsPanel.setBorder(BorderFactory.createTitledBorder("System Settings"));
            JButton backupButton = new JButton("Backup Database");
            JButton manageStaffButton = new JButton("Manage Staff/Admin Accounts");
            manageStaffButton.addActionListener(e -> {
                new ManageAdminsDialog(AdminDashboard.this).setVisible(true);
            });
            JButton changePassButton = new JButton("Change Password");
            settingsPanel.add(backupButton);
            settingsPanel.add(manageStaffButton);
            settingsPanel.add(changePassButton);
            bottomPanel.add(settingsPanel);

            // Search Bar
            JPanel searchPanel = new JPanel(new FlowLayout());
            searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
            JTextField searchField = new JTextField(20);
            JButton searchButton = new JButton("Search");
            searchPanel.add(new JLabel("Search by Book/Author/ISBN/Member:"));
            searchPanel.add(searchField);
            searchPanel.add(searchButton);

            // Footer
            JPanel footerPanel = new JPanel(new FlowLayout());
            footerPanel.setBackground(Color.LIGHT_GRAY);
            footerPanel.add(new JLabel("© 2023 Library Management System | Contact Support"));
            footerPanel.setPreferredSize(new Dimension(1000, 30));

            // Add to frame
            add(mainPanel, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);
            add(searchPanel, BorderLayout.EAST);
            add(footerPanel, BorderLayout.SOUTH);

            // Add action listeners for buttons as placeholders
            addBookButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Add/Edit/Delete Book functionality here."));
        }
    }

    static class ManageAdminsDialog extends JDialog {
        private DefaultListModel<String> listModel;

        public ManageAdminsDialog(Frame parent) {
            super(parent, "Manage Admin Accounts", true);
            setSize(400, 300);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            // List of admins
            listModel = new DefaultListModel<>();
            refreshList();
            JList<String> adminList = new JList<>(listModel);
            add(new JScrollPane(adminList), BorderLayout.CENTER);

            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton addButton = new JButton("Add New Admin");
            addButton.addActionListener(e -> {
                Runnable onSuccess = this::refreshList;
                new RegisterDialog(onSuccess).setVisible(true);
            });
            JButton removeButton = new JButton("Remove Selected");
            removeButton.addActionListener(e -> {
                String selected = adminList.getSelectedValue();
                if (selected != null && !selected.equals(currentAdmin)) {
                    registeredAdmins.remove(selected);
                    refreshList();
                    JOptionPane.showMessageDialog(this, "Admin removed.");
                } else {
                    JOptionPane.showMessageDialog(this, "Cannot remove yourself or select an admin.");
                }
            });
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dispose());
            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);
            buttonPanel.add(closeButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void refreshList() {
            listModel.clear();
            for (String id : registeredAdmins.keySet()) {
                listModel.addElement(id);
            }
        }
    }
}


