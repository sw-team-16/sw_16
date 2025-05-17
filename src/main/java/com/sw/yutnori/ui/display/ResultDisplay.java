package com.sw.yutnori.ui.display;

import com.sw.yutnori.model.enums.YutResult;

import java.util.List;

public interface ResultDisplay {

    void displayYutResult(String result);

    void syncWithYutResults(List<YutResult> yutResults);

    void updateCurrentYut(String yutType);

    void resetResults();

    boolean isSpecialResult(String result);

    String convertYutTypeToKorean(String englishYutType);

    String convertYutTypeToEnglish(String koreanYutType);

}
