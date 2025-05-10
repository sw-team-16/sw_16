package com.sw.yutnori.ui.display;

public interface ResultDisplay {

    void displayYutResult(String result);

    void updateCurrentYut(String yutType);

    void resetResults();

    boolean isSpecialResult(String result);

    String convertYutTypeToKorean(String englishYutType);

    String convertYutTypeToEnglish(String koreanYutType);

}
