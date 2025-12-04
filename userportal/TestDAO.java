import java.sql.SQLException;
import java.util.List;
import com.library.dao.DatabaseUtil;
import com.library.dao.BookDAO;
import com.library.dao.IssuedBookDAO;
import com.library.model.Book;

public class TestDAO {
    public static void main(String[] args) {
        // Initialize database
        DatabaseUtil.initializeDatabase();

        BookDAO bookDAO = new BookDAO();
        IssuedBookDAO issuedBookDAO = new IssuedBookDAO();

        try {
            // Test getAllBooks
            System.out.println("Testing getAllBooks:");
            List<Book> allBooks = bookDAO.getAllBooks();
            for (Book b : allBooks) {
                System.out.println("Book: " + b.getName() + ", Available: " + b.isAvailable());
            }

            // Test searchBooks
            System.out.println("\nTesting searchBooks for 'Java':");
            List<Book> searchResults = bookDAO.searchBooks("Java");
            for (Book b : searchResults) {
                System.out.println("Found: " + b.getName() + ", Available: " + b.isAvailable());
            }

            // Test issueBook (assuming student_id 1 and barcode B001 exist)
            System.out.println("\nTesting issueBook:");
            boolean issued = issuedBookDAO.issueBook(1, "B001");
            System.out.println("Issue successful: " + issued);

            // Check if book is now unavailable
            Book book = bookDAO.getBookById(1);
            if (book != null) {
                System.out.println("Book available after issue: " + book.isAvailable());
            }

            // Test returnBook (assuming barcode exists)
            System.out.println("\nTesting returnBook:");
            boolean returned = issuedBookDAO.returnBook(1, "B001"); // Assuming barcode B001
            System.out.println("Return successful: " + returned);

            // Check if book is now available
            book = bookDAO.getBookById(1);
            if (book != null) {
                System.out.println("Book available after return: " + book.isAvailable());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
