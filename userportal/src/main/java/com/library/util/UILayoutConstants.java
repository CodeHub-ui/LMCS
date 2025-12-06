package com.library.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Screen;

public class UILayoutConstants {
    // Use fixed dimensions for a consistent layout
    public static final double SCENE_WIDTH = 1200;
    public static final double SCENE_HEIGHT = 900;
    public static final Insets PADDING = new Insets(20);
    public static final Pos CENTER_ALIGNMENT = Pos.CENTER;

    // Background style with image
    public static final String FULL_BACKGROUND_STYLE =
        "-fx-background-image: url('https://images.unsplash.com/photo-1481627834876-b7833e8f5570?ixlib=rb-4.0.3&auto=format&fit=crop&w=1470&q=80');" +
        "-fx-background-size: cover;" +
        "-fx-background-position: center center;" +
        "-fx-background-repeat: no-repeat;";
}
