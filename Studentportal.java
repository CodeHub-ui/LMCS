import com.fazecast.jSerialComm.SerialPort;  // Import from jSerialComm library
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class StudentPortal extends JFrame {

    // Map for student data (initialize with defaults or add manually if needed)
    private Map<String, StudentData> studentMap = new HashMap<>();
    private StudentData currentStudent;
    private String currentRfidData;  // To track the RFID for saving changes

    // Serial ports for RFID and barcode scanners (optional)
    private SerialPort rfidSerialPort;
    private SerialPort barcodeSerialPort;

    // Inner class for student data
    private static class StudentData {
        String name, id, email, mobile;
        List<String> issuedBooks;

        StudentData(String name, String id, String email, String mobile, List<String> books) {
            this.name = name;
            this.id = id;
            this.email = email;
            this.mobile = mobile;
            this.issuedBooks = new ArrayList<>(books);
        }
    }

    public StudentPortal() {
        // Initialize student data with defaults (customize as needed)
        studentMap.put("CARD1", new StudentData("Harshit Gupta ", "Btechcse", "harshit.gupta@example.com", "9998881112", 
            List.of("Book 1: Java Programming", "Book 2: Data Structures")));
        // Default for unrecognized cards
        studentMap.put("DEFAULT", new StudentData("Bhasker", "B.techECE", "123455@gmail.com", "887567852", 
            List.of("java language", "oops")));

        // Try to initialize scanners (optional - if fails, show warning and continue)
        try {
            initializeScanners();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Serial scanners not available. Using manual mode only.\nError: " + e.getMessage(), "Scanner Warning", JOptionPane.WARNING_MESSAGE);
        }

        // Initial login screen (RFID only, no manual input)
        setTitle("Library Management System - Student Portal");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());

        JLabel instructionLabel = new JLabel("Please tap your RFID card to login", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        loginPanel.add(instructionLabel, BorderLayout.CENTER);

        add(loginPanel);
        setVisible(true);
    }

    private void initializeScanners() {
        // RFID Scanner
        String rfidPortName = "COM3";  // Change this to your actual port
        rfidSerialPort = SerialPort.getCommPort(rfidPortName);
        rfidSerialPort.setBaudRate(9600);
        rfidSerialPort.setNumDataBits(8);
        rfidSerialPort.setNumStopBits(1);
        rfidSerialPort.setParity(SerialPort.NO_PARITY);

        // Barcode Scanner
        String barcodePortName = "COM4";  // Change this to your actual port
        barcodeSerialPort = SerialPort.getCommPort(barcodePortName);
        barcodeSerialPort.setBaudRate(9600);  // Adjust if needed
        barcodeSerialPort.setNumDataBits(8);
        barcodeSerialPort.setNumStopBits(1);
        barcodeSerialPort.setParity(SerialPort.NO_PARITY);

        boolean rfidConnected = rfidSerialPort.openPort();
        boolean barcodeConnected = barcodeSerialPort.openPort();

        // Pop-up messages for connections
        if (rfidConnected && barcodeConnected) {
            JOptionPane.showMessageDialog(this, "RFID scanner connected and barcode scanner connected.", "Connection Status", JOptionPane.INFORMATION_MESSAGE);
        } else if (rfidConnected) {
            JOptionPane.showMessageDialog(this, "RFID scanner connected, but barcode scanner not connected.", "Connection Status", JOptionPane.WARNING_MESSAGE);
        } else if (barcodeConnected) {
            JOptionPane.showMessageDialog(this, "Barcode scanner connected, but RFID scanner not connected.", "Connection Status", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Neither RFID nor barcode scanner connected. Check connections.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }

        // Add listener for RFID
        if (rfidConnected) {
            System.out.println("RFID Reader connected on " + rfidPortName);
            rfidSerialPort.addDataListener(new SerialPortDataListener() {
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
                        SwingUtilities.invokeLater(() -> processLogin(rfidData));
                    }
                }
            });
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
        // Close serial ports when moving to interface (reopen if needed later)
        if (rfidSerialPort != null && rfidSerialPort.isOpen()) {
            rfidSerialPort.closePort();
        }
        if (barcodeSerialPort != null && barcodeSerialPort.isOpen()) {
            barcodeSerialPort.closePort();
        }

        // New window for student details (read-only)
        JFrame studentFrame = new JFrame("Student Interface");
        studentFrame.setSize(600, 500);
        studentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        studentFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Student details panel with read-only fields
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(6, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));

        detailsPanel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField(currentStudent.name);
        nameField.setEditable(false);  // Read-only
        detailsPanel.add(nameField);

        detailsPanel.add(new JLabel("Student ID:"));
        JTextField idField = new JTextField(currentStudent.id);
        idField.setEditable(false);  // Read-only
        detailsPanel.add(idField);

        detailsPanel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField(currentStudent.email);
        emailField.setEditable(false);  // Read-only
        detailsPanel.add(emailField);

        detailsPanel.add(new JLabel("Mobile Number:"));
        JTextField mobileField = new JTextField(currentStudent.mobile);
        mobileField.setEditable(false);  // Read-only
        detailsPanel.add(mobileField);

        detailsPanel.add(new JLabel("Books Issued:"));
        JTextArea booksArea = new JTextArea(String.join("\n", currentStudent.issuedBooks));
        booksArea.setEditable(false);  // Read-only
        JScrollPane scrollPane = new JScrollPane(booksArea);
        detailsPanel.add(scrollPane);

        mainPanel.add(detailsPanel, BorderLayout.CENTER);

        // Action panel with buttons (no save changes)
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout());

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
        issueBookButton.addActionListener(e -> showIssueBookInterface(studentFrame));
        actionPanel.add(issueBookButton);

        JButton returnBookButton = new JButton("Return Book");
        returnBookButton.addActionListener(e -> showReturnBookInterface(studentFrame));
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

    private void showIssueBookInterface(JFrame parentFrame) {
        // Reopen barcode scanner if needed
        if (barcodeSerialPort != null && !barcodeSerialPort.isOpen()) {
            barcodeSerialPort.openPort();
        }

        JFrame issueFrame = new JFrame("Issue Book");
        issueFrame.setSize(400, 300);
        issueFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        issueFrame.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel instructionLabel = new JLabel("Scan the barcode of the book to issue", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(instructionLabel, BorderLayout.CENTER);

        List<String> scannedBooks = new ArrayList<>();
        JButton addAnotherButton = new JButton("Add Another Book");
        JButton continueButton = new JButton("Continue");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addAnotherButton);
        buttonPanel.add(continueButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addAnotherButton.setEnabled(false);
        continueButton.setEnabled(false);

        // Listener for barcode scanner
        if (barcodeSerialPort != null && barcodeSerialPort.isOpen()) {
            barcodeSerialPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    byte[] newData = event.getReceivedData();
                    String barcodeData = new String(newData).trim();
                    if (!barcodeData.isEmpty()) {
                        SwingUtilities.invokeLater(() -> {
                            if (scannedBooks.size() < 6) {
                                scannedBooks.add("Book: " + barcodeData);  // Assuming barcode is book ID
                                JOptionPane.showMessageDialog(issueFrame, "Book scanned: " + barcodeData, "Scan Success", JOptionPane.INFORMATION_MESSAGE);
                                addAnotherButton.setEnabled(true);
                                continueButton.setEnabled(true);
                            } else {
                                JOptionPane.showMessageDialog(issueFrame, "Exceeded the limit to add the books. Proceeding to continue.", "Limit Exceeded", JOptionPane.WARNING_MESSAGE);
                                continueButton.doClick();  // Auto continue
                            }
                        });
                    }
                }
            });
        } else {
            // Manual input if scanner not available
            JButton manualScanButton = new JButton("Manual Scan (for testing)");
            buttonPanel.add(manualScanButton);
            manualScanButton.addActionListener(e -> {
                String barcode = JOptionPane.showInputDialog("Enter barcode:");
                if (barcode != null && !barcode.trim().isEmpty()) {
                    if (scannedBooks.size() < 6) {
                        scannedBooks.add("Book: " + barcode.trim());
                        JOptionPane.showMessageDialog(issueFrame, "Book scanned: " + barcode, "Scan Success", JOptionPane.INFORMATION_MESSAGE);
                        addAnotherButton.setEnabled(true);
                        continueButton.setEnabled(true);
                    } else {
                        JOptionPane.showMessageDialog(issueFrame, "Exceeded the limit to add the books. Proceeding to continue.", "Limit Exceeded", JOptionPane.WARNING_MESSAGE);
                        continueButton.doClick();
                    }
                }
            });
        }

        addAnotherButton.addActionListener(e -> {
            instructionLabel.setText("Scan the barcode of the next book to issue");
            addAnotherButton.setEnabled(false);
            continueButton.setEnabled(false);
        });

        continueButton.addActionListener(e -> {
            // Add scanned books to student's issued list
            currentStudent.issuedBooks.addAll(scannedBooks);
            studentMap.put(currentRfidData, currentStudent);
            // Send confirmation email
            sendEmail(currentStudent.email, "Books Issued Successfully", "You have successfully issued the following books:\n" + String.join("\n", scannedBooks));
            JOptionPane.showMessageDialog(issueFrame, "Books issued successfully! Confirmation email sent.", "Success", JOptionPane.INFORMATION_MESSAGE);
            issueFrame.dispose();
        });

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            issueFrame.dispose();
            parentFrame.dispose();
            new StudentPortal();
        });
        buttonPanel.add(logoutButton);

        issueFrame.add(panel);
        issueFrame.setVisible(true);
    }

    private void showReturnBookInterface(JFrame parentFrame) {
        // Reopen barcode scanner if needed
        if (barcodeSerialPort != null && !barcodeSerialPort.isOpen()) {
            barcodeSerialPort.openPort();
        }

        JFrame returnFrame = new JFrame("Return Book");
        returnFrame.setSize(400, 300);
        returnFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        returnFrame.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // List of issued books
        JList<String> bookList = new JList<>(currentStudent.issuedBooks.toArray(new String[0]));
        JScrollPane listScroll = new JScrollPane(bookList);
        panel.add(listScroll, BorderLayout.CENTER);

        JButton returnSelectedButton = new JButton("Return Selected Book");
        panel.add(returnSelectedButton, BorderLayout.NORTH);

        List<String> returnedBooks = new ArrayList<>();
        JButton returnAnotherButton = new JButton("Return Another Book");
        JButton continueButton = new JButton("Continue");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(returnAnotherButton);
        buttonPanel.add(continueButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        returnAnotherButton.setEnabled(false);
        continueButton.setEnabled(false);

        returnSelectedButton.addActionListener(e -> {
            String selectedBook = bookList.getSelectedValue();
            if (selectedBook != null) {
                JOptionPane.showMessageDialog(returnFrame, "Please scan the barcode of the selected book to confirm return.", "Scan Required", JOptionPane.INFORMATION_MESSAGE);
                // Wait for scan
                if (barcodeSerialPort != null && barcodeSerialPort.isOpen()) {
                    barcodeSerialPort.addDataListener(new SerialPortDataListener() {
                        @Override
                        public int getListeningEvents() {
                            return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
                        }

                        @Override
                        public void serialEvent(SerialPortEvent event) {
                            byte[] newData = event.getReceivedData();
                            String barcodeData = new String(newData).trim();
                            if (!barcodeData.isEmpty() && selectedBook.contains(barcodeData)) {  // Simple check
                                SwingUtilities.invokeLater(() -> {
                                    if (returnedBooks.size() < 6) {
                                        returnedBooks.add(selectedBook);
                                        currentStudent.issuedBooks.remove(selectedBook);
                                        JOptionPane.showMessageDialog(returnFrame, "Book returned: " + selectedBook, "Return Success", JOptionPane.INFORMATION_MESSAGE);
                                        returnAnotherButton.setEnabled(true);
                                        continueButton.setEnabled(true);
                                        bookList.setListData(currentStudent.issuedBooks.toArray(new String[0]));  // Update list
                                    } else {
                                        JOptionPane.showMessageDialog(returnFrame, "Exceeded the limit to return books. Proceeding to continue.", "Limit Exceeded", JOptionPane.WARNING_MESSAGE);
                                        continueButton.doClick();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    // Manual input if scanner not available
                    String barcode = JOptionPane.showInputDialog("Enter barcode for " + selectedBook + ":");
                    if (barcode != null && !barcode.trim().isEmpty() && selectedBook.contains(barcode.trim())) {
                        if (returnedBooks.size() < 6) {
                            returnedBooks.add(selectedBook);
                            currentStudent.issuedBooks.remove(selectedBook);
                            JOptionPane.showMessageDialog(returnFrame, "Book returned: " + selectedBook, "Return Success", JOptionPane.INFORMATION_MESSAGE);
                            returnAnotherButton.setEnabled(true);
                            continueButton.setEnabled(true);
                            bookList.setListData(currentStudent.issuedBooks.toArray(new String[0]));
                        } else {
                            JOptionPane.showMessageDialog(returnFrame, "Exceeded the limit to return books. Proceeding to continue.", "Limit Exceeded", JOptionPane.WARNING_MESSAGE);
                            continueButton.doClick();
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(returnFrame, "Please select a book to return.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            }
        });

        returnAnotherButton.addActionListener(e -> {
            returnAnotherButton.setEnabled(false);
            continueButton.setEnabled(false);
        });

        continueButton.addActionListener(e -> {
            studentMap.put(currentRfidData, currentStudent);
            // Send confirmation email
            sendEmail(currentStudent.email, "Books Returned Successfully", "You have successfully returned the following books:\n" + String.join("\n", returnedBooks));
            JOptionPane.showMessageDialog(returnFrame, "Books returned successfully! Confirmation email sent.", "Success", JOptionPane.INFORMATION_MESSAGE);
            returnFrame.dispose();
        });

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            returnFrame.dispose();
            parentFrame.dispose();
            new StudentPortal();
        });
        buttonPanel.add(logoutButton);

        returnFrame.add(panel);
        returnFrame.setVisible(true);
    }

    private void sendEmail(String to, String subject, String body) {
        // For simulation, just print    private void sendEmail(String to, String subject, String body) {
        // For simulation, just print to console. In real app, configure SMTP.
        System.out.println("Sending email to " + to + ":\nSubject: " + subject + "\nBody: " + body);
        // Uncomment and configure below for actual email
        /*
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.example.com");  // Your SMTP host
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("your-email@example.com", "your-password");
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("your-email@example.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        */
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentPortal());
    }
}
