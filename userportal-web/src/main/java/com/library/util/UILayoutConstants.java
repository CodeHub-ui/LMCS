package com.library.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Screen;

public class UILayoutConstants {
    // Use primary screen dimensions for a full-page layout
    public static final double SCENE_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
    public static final double SCENE_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
    public static final Insets PADDING = new Insets(20);
    public static final Pos CENTER_ALIGNMENT = Pos.CENTER;
}
