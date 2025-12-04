package com.library.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

/**
 * A utility class to hold constants for UI layout and styling.
 * This ensures a consistent look and feel across the application.
 * Enhanced with theme support and modern Material Design styles.
 */
public final class UILayoutConstants {

    // Private constructor to prevent instantiation
    private UILayoutConstants() {}

    // --- Scene Dimensions ---
    public static final double SCENE_WIDTH = 1280;
    public static final double SCENE_HEIGHT = 800;

    // --- Padding and Alignment ---
    public static final Insets PADDING = new Insets(20);
    public static final Pos CENTER_ALIGNMENT = Pos.CENTER;

    // --- Theme Colors ---
    public static final String LIGHT_BACKGROUND = "#f8fafc";
    public static final String DARK_BACKGROUND = "#0f172a";
    public static final String LIGHT_SURFACE = "#ffffff";
    public static final String DARK_SURFACE = "#1e293b";
    public static final String LIGHT_TEXT = "#1e293b";
    public static final String DARK_TEXT = "#f1f5f9";
    public static final String ACCENT_COLOR = "#3b82f6";
    public static final String ACCENT_HOVER = "#2563eb";

    // --- Styling Strings (JavaFX CSS) ---

    /**
     * Style for the root container with gradient background.
     */
    public static final String FULL_BACKGROUND_STYLE_LIGHT =
        "-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 100%);";

    public static final String FULL_BACKGROUND_STYLE_DARK =
        "-fx-background-color: linear-gradient(to bottom right, #2d3748 0%, #1a202c 100%);";

    /**
     * Style for the main content pane with glassmorphism effect.
     */
    public static final String CONTENT_PANE_STYLE_LIGHT =
        "-fx-background-color: rgba(255, 255, 255, 0.95);" +
        "-fx-background-radius: 20px;" +
        "-fx-border-radius: 20px;" +
        "-fx-border-color: rgba(255, 255, 255, 0.2);" +
        "-fx-border-width: 1px;" +
        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 20, 0, 0, 10);";

    public static final String CONTENT_PANE_STYLE_DARK =
        "-fx-background-color: rgba(30, 41, 59, 0.95);" +
        "-fx-background-radius: 20px;" +
        "-fx-border-radius: 20px;" +
        "-fx-border-color: rgba(255, 255, 255, 0.1);" +
        "-fx-border-width: 1px;" +
        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 20, 0, 0, 10);";

    // --- Component Styles ---
    public static final String HEADING_STYLE_LIGHT = "-fx-font-size: 28px; -fx-font-weight: 700; -fx-text-fill: #1e293b;";
    public static final String HEADING_STYLE_DARK = "-fx-font-size: 28px; -fx-font-weight: 700; -fx-text-fill: #f1f5f9;";
    public static final String SUBHEADING_STYLE_LIGHT = "-fx-font-size: 14px; -fx-text-fill: #475569;";
    public static final String SUBHEADING_STYLE_DARK = "-fx-font-size: 14px; -fx-text-fill: #94a3b8;";

    public static final String TEXT_INPUT_STYLE_LIGHT =
        "-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 12; -fx-border-color: #d1d5db; -fx-border-radius: 12; -fx-background-color: #ffffff;";
    public static final String TEXT_INPUT_STYLE_DARK =
        "-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 12; -fx-border-color: #475569; -fx-border-radius: 12; -fx-background-color: #334155; -fx-text-fill: #f1f5f9;";

    // --- Button Styles ---
    public static final String PRIMARY_BUTTON_STYLE_LIGHT =
        "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 12; -fx-padding: 12 24; -fx-cursor: hand;" +
        "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 8, 0, 0, 2);";
    public static final String PRIMARY_BUTTON_STYLE_DARK =
        "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 12; -fx-padding: 12 24; -fx-cursor: hand;" +
        "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.5), 8, 0, 0, 2);";

    public static final String SECONDARY_BUTTON_STYLE_LIGHT =
        "-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: 600; -fx-background-radius: 12; -fx-padding: 12 24; -fx-cursor: hand;" +
        "-fx-border-color: #d1d5db; -fx-border-radius: 12; -fx-border-width: 1;";
    public static final String SECONDARY_BUTTON_STYLE_DARK =
        "-fx-background-color: #334155; -fx-text-fill: #cbd5e1; -fx-font-weight: 600; -fx-background-radius: 12; -fx-padding: 12 24; -fx-cursor: hand;" +
        "-fx-border-color: #475569; -fx-border-radius: 12; -fx-border-width: 1;";

    // --- Card Styles ---
    public static final String CARD_STYLE_LIGHT =
        "-fx-background-color: #ffffff; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);";
    public static final String CARD_STYLE_DARK =
        "-fx-background-color: #1e293b; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 4);";

    // --- Sidebar Styles ---
    public static final String SIDEBAR_STYLE_LIGHT =
        "-fx-background-color: #ffffff; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);";
    public static final String SIDEBAR_STYLE_DARK =
        "-fx-background-color: #0f172a; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);";

    // --- Legacy Constants for backward compatibility ---
    public static final String FULL_BACKGROUND_STYLE = FULL_BACKGROUND_STYLE_LIGHT;
    public static final String CONTENT_PANE_STYLE = CONTENT_PANE_STYLE_LIGHT;
    public static final String HEADING_STYLE = HEADING_STYLE_LIGHT;
    public static final String SUBHEADING_STYLE = SUBHEADING_STYLE_LIGHT;
    public static final String TEXT_INPUT_STYLE = TEXT_INPUT_STYLE_LIGHT;

    // --- Theme Management ---
    private static boolean isDarkTheme = false;

    public static boolean isDarkTheme() {
        return isDarkTheme;
    }

    public static void setDarkTheme(boolean dark) {
        isDarkTheme = dark;
    }

    public static void toggleTheme() {
        isDarkTheme = !isDarkTheme;
    }

    public static String getBackgroundStyle() {
        return isDarkTheme ? FULL_BACKGROUND_STYLE_DARK : FULL_BACKGROUND_STYLE_LIGHT;
    }

    public static String getContentPaneStyle() {
        return isDarkTheme ? CONTENT_PANE_STYLE_DARK : CONTENT_PANE_STYLE_LIGHT;
    }

    public static String getHeadingStyle() {
        return isDarkTheme ? HEADING_STYLE_DARK : HEADING_STYLE_LIGHT;
    }

    public static String getSubheadingStyle() {
        return isDarkTheme ? SUBHEADING_STYLE_DARK : SUBHEADING_STYLE_LIGHT;
    }

    public static String getTextInputStyle() {
        return isDarkTheme ? TEXT_INPUT_STYLE_DARK : TEXT_INPUT_STYLE_LIGHT;
    }

    public static String getPrimaryButtonStyle() {
        return isDarkTheme ? PRIMARY_BUTTON_STYLE_DARK : PRIMARY_BUTTON_STYLE_LIGHT;
    }

    public static String getSecondaryButtonStyle() {
        return isDarkTheme ? SECONDARY_BUTTON_STYLE_DARK : SECONDARY_BUTTON_STYLE_LIGHT;
    }

    public static String getCardStyle() {
        return isDarkTheme ? CARD_STYLE_DARK : CARD_STYLE_LIGHT;
    }

    public static String getSidebarStyle() {
        return isDarkTheme ? SIDEBAR_STYLE_DARK : SIDEBAR_STYLE_LIGHT;
    }
}
