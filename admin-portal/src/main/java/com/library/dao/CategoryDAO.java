// Placeholder for CategoryDAO.java
package com.library.dao;

import com.library.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
public List<Category> getAllCategories() {
        LogDAO.log("Entering getAllCategories method.");
        String sql = "SELECT * FROM categories";
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                categories.add(c);
            }
            LogDAO.log("Retrieved " + categories.size() + " categories.");
        } catch (SQLException e) {
            LogDAO.log("SQLException in getAllCategories: " + e.getMessage());
            e.printStackTrace();
        }
        LogDAO.log("Exiting getAllCategories method.");
        return categories;
    }

    public Category getCategoryById(int id) {
        String sql = "SELECT id, name FROM categories WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                return category;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

public boolean addCategory(String name) {
        LogDAO.log("Entering addCategory method with name: " + name);
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LogDAO.log("Successfully added category: " + name);
            } else {
                LogDAO.log("Failed to add category: " + name);
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            LogDAO.log("SQLException in addCategory: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            LogDAO.log("Exiting addCategory method.");
        }
    }

public boolean updateCategory(Category category) {
        LogDAO.log("Entering updateCategory method for category ID: " + category.getId());
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setInt(2, category.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LogDAO.log("Successfully updated category: " + category.getName());
            } else {
                LogDAO.log("Failed to update category: " + category.getName());
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            LogDAO.log("SQLException in updateCategory: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            LogDAO.log("Exiting updateCategory method for category ID: " + category.getId());
        }
    }

    public String getCategoryNameById(int id) {
        String sql = "SELECT name FROM categories WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

public boolean deleteCategory(int id) {
        LogDAO.log("Entering deleteCategory method for category ID: " + id);
        // Check if category has books
        String checkSql = "SELECT COUNT(*) FROM books WHERE category_id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                LogDAO.log("Cannot delete category ID " + id + ": category contains books.");
                return false; // Cannot delete if books exist
            }
        } catch (SQLException e) {
            LogDAO.log("SQLException in deleteCategory check: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        String categoryName = getCategoryNameById(id); // For logging
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LogDAO.log("Successfully deleted category: " + (categoryName != null ? categoryName : "ID " + id));
            } else {
                LogDAO.log("Failed to delete category: " + (categoryName != null ? categoryName : "ID " + id));
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            LogDAO.log("SQLException in deleteCategory: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            LogDAO.log("Exiting deleteCategory method for category ID: " + id);
        }
    }


    /**
     * Searches for categories by name.
     *
     * @param query the search query (partial name)
     * @return a list of Category objects matching the query
     */
    public List<Category> searchCategories(String query) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE name LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("name"));
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    /**
     * Deletes all categories from the database.
     * Note: This should be called after all books are deleted to avoid foreign key constraints.
     *
     * @return true if successful, false otherwise
     */
    public boolean deleteAllCategories() {
        String sql = "DELETE FROM categories";
        try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement()) {
            int deletedRows = stmt.executeUpdate(sql);
            LogDAO.log("All categories deleted. Rows affected: " + deletedRows);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
