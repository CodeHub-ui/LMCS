import com.fazecast.jSerialComm.SerialPort;  // Import from jSerialComm library
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class StudentPortal extends JFrame {

    // Map RFID data to student profiles (add more as needed)
    private Map<String, StudentData> studentMap = new HashMap<>();
    private StudentData currentStudent;
    private String currentRfidData;  // To track the RFID for saving changes

    // Inner class for student data
    private static class StudentData {
        String name, id, email, mobile;
        String[] issuedBooks;

        StudentData(String name, String id, String email, String mobile, String[] books) {
            this.name = name;
            this.id = id;
            this.email = email;
            this.mobile = mobile;
            this.issuedBooks = books;
        }
    }

    private SerialPort serialPort;  // Serial port for RFID reader

    public StudentPortal() {
        // Initialize student data for different cards (customize as needed)
        studentMap.put("CARD1", new StudentData("Harshit Gupta ", "Btechcse", "harshit.gupta@example.com", "9998881112", 
            new String[]{"Book 1: Java Programming", "Book 2: Data Structures"}));
        // Default for unrecognized cards (updated with your details)
        studentMap.put("DEFAULT", new StudentData("Bhasker", "B.techECE", "123455@gmail.com", "887567852", 
            new String[]{"java language", "oops"}));

        // Initial login screen
        setTitle("Library Management System - Student Portal");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());

        JLabel instructionLabel = new JLabel("Please tap your RFID card to login", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        loginPanel.add(instructionLabel, BorderLayout.CENTER);

        // Button to manually trigger if RFID fails (for testing)
        JButton manualTapButton = new JButton("Manual Tap (for testing)");
        manualTapButton.setFont(new Font("Arial", Font.PLAIN, 14));
        manualTapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Simulate with default or prompt for card ID
                String testCard = JOptionPane.showInputDialog("Enter test card ID (e.g., CARD1, CARD2, CARD3):");
                if (testCard != null) {
                    processLogin(testCard.trim());
                }
            }
        });
        loginPanel.add(manualTapButton, BorderLayout.SOUTH);

        add(loginPanel);
        setVisible(true);

        // Initialize RFID reader
        initializeRFIDReader();
    }

    private void initializeRFIDReader() {
        // Specify your serial port (e.g., "COM3" on Windows, "/dev/ttyUSB0" on Linux/Mac)
        String portName = "COM3";  // Change this to your actual port
        serialPort = SerialPort.getCommPort(portName);

        // Set port parameters (adjust based on your RFID reader specs)
        serialPort.setBaudRate(9600);  // Common baud rate
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(1);
        serialPort.setParity(SerialPort.NO_PARITY);

        if (serialPort.openPort()) {
            System.out.println("RFID Reader connected on " + portName);
            // Add listener for incoming data
            serialPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    byte[] newData = event.getReceivedData();
                    String rfidData = new String(newData).trim();
                    if (!rfidData.isEmpty()) {
                        System.out.println("RFID Data Received: " + rfidData);
                        // Process login immediately on tap
                        SwingUtilities.invokeLater(() -> processLogin(rfidData));
                    }
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, "Failed to connect to RFID reader on " + portName + ". Check connection and port name.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processLogin(String rfidData) {
        // Fetch student data based on RFID
        currentStudent = studentMap.getOrDefault(rfidData, studentMap.get("DEFAULT"));
        currentRfidData = rfidData;  // Store for saving changes
        JOptionPane.showMessageDialog(this, "You were successfully logged in!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        showStudentInterface();
    }

    private void showStudentInterface() {
        // Close serial port when moving to interface
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }

        // New window for student details
        JFrame studentFrame = new JFrame("Student Interface");
        studentFrame.setSize(600, 500);
        studentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        studentFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Student details panel with editable fields
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(6, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));

        detailsPanel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField(currentStudent.name);
        detailsPanel.add(nameField);

        detailsPanel.add(new JLabel("Student ID:"));
        JTextField idField = new JTextField(currentStudent.id);
        detailsPanel.add(idField);

        detailsPanel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField(currentStudent.email);
        detailsPanel.add(emailField);

        detailsPanel.add(new JLabel("Mobile Number:"));
        JTextField mobileField = new JTextField(currentStudent.mobile);
        detailsPanel.add(mobileField);

        detailsPanel.add(new JLabel("Books Issued:"));
        JTextArea booksArea = new JTextArea(String.join("\n", currentStudent.issuedBooks));
        JScrollPane scrollPane = new JScrollPane(booksArea);
        detailsPanel.add(scrollPane);

        mainPanel.add(detailsPanel, BorderLayout.CENTER);

        // Action panel with save and other buttons
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout());

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            // Update current student data
            currentStudent.name = nameField.getText();
            currentStudent.id = idField.getText();
            currentStudent.email = emailField.getText();
            currentStudent.mobile = mobileField.getText();
            currentStudent.issuedBooks = booksArea.getText().split("\n");
            // Save back to map for persistence
            studentMap.put(currentRfidData, currentStudent);
            JOptionPane.showMessageDialog(studentFrame, "Changes saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        actionPanel.add(saveButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            // Reload fields with current data
            nameField.setText(currentStudent.name);
            idField.setText(currentStudent.id);
            emailField.setText(currentStudent.email);
            mobileField.setText(currentStudent.mobile);
            booksArea.setText(String.join("\n", currentStudent.issuedBooks));
        });
        actionPanel.add(refreshButton);

        JButton issueBookButton = new JButton("Issue Book");
        issueBookButton.addActionListener(e -> JOptionPane.showMessageDialog(studentFrame, "Feature to issue a book (not implemented yet)", "Info", JOptionPane.INFORMATION_MESSAGE));
        actionPanel.add(issueBookButton);

        JButton returnBookButton = new JButton("Return Book");
        returnBookButton.addActionListener(e -> JOptionPane.showMessageDialog(studentFrame, "Feature to return a book (not implemented yet)", "Info", JOptionPane.INFORMATION_MESSAGE));
        actionPanel.add(returnBookButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            studentFrame.dispose();
            new StudentPortal(); // Restart to login screen
        });
        actionPanel.add(logoutButton);

        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        studentFrame.add(mainPanel);
        studentFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentPortal());
    }

}
