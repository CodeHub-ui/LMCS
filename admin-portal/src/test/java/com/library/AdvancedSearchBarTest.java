package com.library;

import com.library.util.AdvancedSearchBar;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class AdvancedSearchBarTest {

    @BeforeAll
    public static void initJFX() {
        // Initialize JavaFX toolkit
        new JFXPanel();
    }

    @Test
    public void testInstantiation() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                List<String> data = Arrays.asList("Apple", "Banana", "Cherry", "Date");
                AdvancedSearchBar<String> searchBar = new AdvancedSearchBar<>(
                    () -> data,
                    s -> s,
                filtered -> {
                    // Verify initial load shows all items
                    if (filtered.size() == 4) {
                        latch.countDown();
                    }
                },
                    "Search fruits..."
                );

                // Verify UI components
                assertNotNull(searchBar);
                assertEquals("Search fruits...", searchBar.getPromptText());

                // Test setting query
                searchBar.setQuery("App");
                assertEquals("App", searchBar.getQuery());

                latch.countDown();
            } catch (Exception e) {
                fail("Instantiation failed: " + e.getMessage());
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Test timed out");
    }

    @Test
    public void testSearchFunctionality() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final AdvancedSearchBar<String>[] searchBarRef = new AdvancedSearchBar[1];

        Platform.runLater(() -> {
            List<String> data = Arrays.asList("Apple", "Banana", "Cherry", "Date");
            AdvancedSearchBar<String> searchBar = new AdvancedSearchBar<>(
                () -> data,
                s -> s,
                filtered -> {
                    // Check if query is set and results are filtered
                    String query = searchBarRef[0].getQuery();
                    if ("App".equals(query)) {
                        // Should contain only "Apple" for query "App"
                        assertEquals(1, filtered.size());
                        assertTrue(filtered.contains("Apple"));
                        latch.countDown();
                    }
                },
                "Search..."
            );
            searchBarRef[0] = searchBar;

            // Simulate search
            searchBar.setQuery("App");
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS), "Search test timed out");
    }
}
