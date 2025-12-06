package com.library.model;

/**
 * Unified search result model for advanced search functionality.
 * Represents a single result from any entity type with relevance scoring.
 */
public class SearchResult implements Comparable<SearchResult> {
    public enum EntityType {
        BOOK("📚", "Book"),
        STUDENT("👨‍🎓", "Student"),
        FACULTY("👨‍🏫", "Faculty"),
        CATEGORY("📂", "Category"),
        ISSUED_BOOK("📖", "Issued Book"),
        RETURNED_BOOK("📚", "Returned Book");

        private final String icon;
        private final String displayName;

        EntityType(String icon, String displayName) {
            this.icon = icon;
            this.displayName = displayName;
        }

        public String getIcon() { return icon; }
        public String getDisplayName() { return displayName; }
    }

    private EntityType entityType;
    private String title;
    private String subtitle;
    private String details;
    private double relevanceScore;
    private Object originalObject; // Reference to the original entity object
    private String highlightedTitle; // Title with search terms highlighted
    private String highlightedSubtitle; // Subtitle with search terms highlighted

    public SearchResult(EntityType entityType, String title, String subtitle, String details, double relevanceScore, Object originalObject) {
        this.entityType = entityType;
        this.title = title;
        this.subtitle = subtitle;
        this.details = details;
        this.relevanceScore = relevanceScore;
        this.originalObject = originalObject;
    }

    // Getters and setters
    public EntityType getEntityType() { return entityType; }
    public void setEntityType(EntityType entityType) { this.entityType = entityType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(double relevanceScore) { this.relevanceScore = relevanceScore; }

    public Object getOriginalObject() { return originalObject; }
    public void setOriginalObject(Object originalObject) { this.originalObject = originalObject; }

    public String getHighlightedTitle() { return highlightedTitle != null ? highlightedTitle : title; }
    public void setHighlightedTitle(String highlightedTitle) { this.highlightedTitle = highlightedTitle; }

    public String getHighlightedSubtitle() { return highlightedSubtitle != null ? highlightedSubtitle : subtitle; }
    public void setHighlightedSubtitle(String highlightedSubtitle) { this.highlightedSubtitle = highlightedSubtitle; }

    @Override
    public int compareTo(SearchResult other) {
        // Sort by relevance score descending (higher scores first)
        return Double.compare(other.relevanceScore, this.relevanceScore);
    }

    @Override
    public String toString() {
        return String.format("%s %s - %s (Score: %.2f)",
            entityType.getIcon(), title, subtitle, relevanceScore);
    }
}
