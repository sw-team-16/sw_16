package com.sw.yutnori.logic.util;
import java.awt.*;
//  플레이어의 색상 코드 매칭 -> 말 위치 표시에 필요
public class ColorUtils {
    public static Color parseColor(String colorStr) {
        return switch (colorStr.toUpperCase()) {
            case "RED" -> new Color(255, 182, 193);
            case "BLUE" -> new Color(173, 216, 230);
            case "GREEN" -> new Color(144, 238, 144);
            case "YELLOW" -> new Color(255, 255, 224);
            case "ORANGE" -> new Color(255, 228, 181);
            case "PURPLE" -> new Color(216, 191, 216);
            case "BLACK" -> new Color(169, 169, 169);
            case "WHITE" -> new Color(245, 245, 245);
            default -> new Color(211, 211, 211); // fallback
        };
    }
}
