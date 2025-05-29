package com.sw.yutnori.ui.javafx.util;

import javafx.scene.paint.Color;

public class FxColorUtils {
    public static Color parseColor(String colorStr) {
        if (colorStr == null) return Color.LIGHTGRAY;
        switch (colorStr.toUpperCase()) {
            case "RED": return Color.LIGHTPINK;
            case "BLUE": return Color.LIGHTBLUE;
            case "GREEN": return Color.LIGHTGREEN;
            case "YELLOW": return Color.LIGHTYELLOW;
            case "ORANGE": return Color.MOCCASIN;
            case "PURPLE": return Color.LAVENDER;
            case "BLACK": return Color.DARKGRAY;
            case "WHITE": return Color.WHITESMOKE;
            default: return Color.LIGHTGRAY;
        }
    }
} 