package com.sw.yutnori.ui.swing.display;

import com.sw.yutnori.model.enums.YutResult;
import com.sw.yutnori.ui.display.ResultDisplay;

import javax.swing.*;
import java.util.List;

public class SwingResultDisplay implements ResultDisplay {
    private final JLabel[] resultLabels;
    private final JLabel currentYutLabel;
    private int currentResultIndex = 0;

    public SwingResultDisplay(JLabel[] resultLabels, JLabel currentYutLabel) {
        this.resultLabels = resultLabels;
        this.currentYutLabel = currentYutLabel;
    }

    @Override
    public void displayYutResult(String result) {
        String koreanResult = convertYutTypeToKorean(result);

        // 윷이나 모인 경우 특별 처리
        if (isSpecialResult(koreanResult)) {
            handleSpecialResult(koreanResult);
        } else {
            handleNormalResult(koreanResult);
        }
    }

    // 윷 혹은 모가 나온 경우 결과 처리
    private void handleSpecialResult(String result) {
        // 현재 표시된 모든 라벨을 확인하여 동일한 결과가 있는지 확인
        for (int i = 0; i <= currentResultIndex; i++) {
            String labelText = resultLabels[i].getText();

            // 기존에 윷 혹은 모가 있을 때 동일한 결과가 나온 경우
            if (labelText.equals(result)) {
                resultLabels[i].setText("<html>" + result + "<sup>2</sup></html>");
                return;
            }

            // 동일한 윷, 모가 3번 이상 나오는 경우
            if (labelText.startsWith("<html>") && labelText.contains(result + "<sup>")) {
                try {
                    int startIdx = labelText.indexOf("<sup>") + 5;
                    int endIdx = labelText.indexOf("</sup>");
                    String countStr = labelText.substring(startIdx, endIdx);
                    int count = Integer.parseInt(countStr);

                    resultLabels[i].setText("<html>" + result + "<sup>" + (count + 1) + "</sup></html>");
                    return;
                } catch (Exception e) {
                    System.err.println("윗첨자 파싱 오류: " + e);
                }
            }
        }

        // 윷 혹은 모가 처음 나온 경우
        if (resultLabels[currentResultIndex].getText().equals("-")) {
            resultLabels[currentResultIndex].setText(result);
        } else {
            moveToNextLabel();
            resultLabels[currentResultIndex].setText(result);
        }
    }

    // 윷 혹은 모 이외의 결과 처리
    private void handleNormalResult(String result) {
        if (resultLabels[currentResultIndex].getText().equals("-")) {
            resultLabels[currentResultIndex].setText(convertYutTypeToKorean(result));
        } else {
            moveToNextLabel();
            resultLabels[currentResultIndex].setText(convertYutTypeToKorean(result));
        }
    }

    private void moveToNextLabel() {
        currentResultIndex++;
        if (currentResultIndex >= resultLabels.length) {
            resetResults();
        }
    }

    @Override
    public void syncWithYutResults(List<YutResult> yutResults) {
        // 모든 라벨 초기화
        resetResults();

        // 현재 실제 윷 결과들로 화면 업데이트
        if (yutResults != null && !yutResults.isEmpty()) {
            currentResultIndex = 0;
            for (YutResult result : yutResults) {
                displayYutResult(result.name());
                if (currentResultIndex < resultLabels.length - 1) {
                    currentResultIndex++;
                }
            }
        }
    }

    @Override
    public void resetResults() {
        for (JLabel label : resultLabels) {
            label.setText("-");
        }
        currentResultIndex = 0;
    }

    @Override
    public void updateCurrentYut(String yutType) {
        currentYutLabel.setText(convertYutTypeToKorean(yutType));
    }

    @Override
    public boolean isSpecialResult(String result) {
        return result.equals("윷") || result.equals("모") ||
                result.equals("YUT") || result.equals("MO");
    }

    @Override
    public String convertYutTypeToKorean(String englishYutType) {
        return convertYutType(englishYutType, true);
    }

    @Override
    public String convertYutTypeToEnglish(String koreanYutType) {
        return convertYutType(koreanYutType, false);
    }

    private String convertYutType(String yutType, boolean toKorean) {
        return switch (yutType) {
            case "도", "DO" -> toKorean ? "도" : "DO";
            case "개", "GAE" -> toKorean ? "개" : "GAE";
            case "걸", "GEOL" -> toKorean ? "걸" : "GEOL";
            case "윷", "YUT" -> toKorean ? "윷" : "YUT";
            case "모", "MO" -> toKorean ? "모" : "MO";
            case "빽도", "BACKDO", "BACK_DO" -> toKorean ? "빽도" : "BACKDO";
            default -> throw new IllegalArgumentException("알 수 없는 윷 타입: " + yutType);
        };
    }

}