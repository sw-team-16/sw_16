package com.sw.yutnori.ui.display;

import com.sw.yutnori.common.enums.YutResult;
import javax.swing.*;

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
        // 윷이나 모인 경우 특별 처리
        if (result.equals("윷") || result.equals("모")) {
            handleSpecialResult(result);
        } else {
            handleNormalResult(result);
        }

        // 현재 윷 결과 업데이트
        updateCurrentYut(convertYutTypeToEnglish(result));
    }

    private void handleSpecialResult(String result) {
        String currentText = resultLabels[currentResultIndex].getText();

        if (currentText.equals("-")) {
            resultLabels[currentResultIndex].setText(result);
            return;
        }

        if (currentText.equals(result) || (currentText.startsWith("<html>") && currentText.contains(result))) {
            resultLabels[currentResultIndex].setText(formatYutResultWithSuperscript(result, currentText));
            return;
        }

        moveToNextLabel();
        resultLabels[currentResultIndex].setText(result);
    }

    private void handleNormalResult(String result) {
        if (resultLabels[currentResultIndex].getText().equals("-")) {
            resultLabels[currentResultIndex].setText(result);
        } else {
            moveToNextLabel();
            resultLabels[currentResultIndex].setText(result);
        }
    }

    private void moveToNextLabel() {
        currentResultIndex++;
        if (currentResultIndex >= resultLabels.length) {
            resetResults();
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
        return result.equals("윷") || result.equals("모");
    }

    private String formatYutResultWithSuperscript(String result, String currentText) {
        // HTML 태그가 있는지 확인
        if (currentText.startsWith("<html>")) {
            // 기존 텍스트에서 결과와 횟수 추출
            String content = currentText.substring(6, currentText.length() - 7);
            int startIndex = content.indexOf("<sup>");

            if (startIndex > 0) {
                String baseText = content.substring(0, startIndex);
                String countText = content.substring(startIndex + 5, content.indexOf("</sup>"));

                // 같은 결과인 경우에만 횟수 증가
                if (baseText.equals(result)) {
                    try {
                        int count = Integer.parseInt(countText);
                        return "<html>" + result + "<sup>" + (count + 1) + "</sup></html>";
                    } catch (NumberFormatException e) {
                        return "<html>" + result + "<sup>2</sup></html>";
                    }
                }
            }
        }

        // 처음 중복되는 경우 - 횟수 2부터 시작
        return "<html>" + result + "<sup>2</sup></html>";
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