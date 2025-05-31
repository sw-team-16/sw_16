package com.sw.yutnori.ui.display;

import com.sw.yutnori.model.enums.YutResult;

import java.util.List;

public interface ResultDisplay {

    void displayYutResult(String result);

    void syncWithYutResults(List<YutResult> yutResults);

    void updateCurrentYut(String yutType);

    void resetResults();

    default String convertYutTypeToKorean(String englishYutType) {
        return switch (englishYutType) {
            case "DO" -> "도";
            case "GAE" -> "개";
            case "GEOL" -> "걸";
            case "YUT" -> "윷";
            case "MO" -> "모";
            case "BACKDO", "BACK_DO" -> "빽도";
            default -> throw new IllegalArgumentException("알 수 없는 윷 타입: " + englishYutType);
        };
    }

    default String convertYutTypeToEnglish(String koreanYutType) {
        return switch (koreanYutType) {
            case "도" -> "DO";
            case "개" -> "GAE";
            case "걸" -> "GEOL";
            case "윷" -> "YUT";
            case "모" -> "MO";
            case "빽도" -> "BACK_DO";
            default -> throw new IllegalArgumentException("알 수 없는 윷 타입: " + koreanYutType);
        };
    }

}
