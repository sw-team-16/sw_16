package com.sw.yutnori.ui.javafx.display;

import com.sw.yutnori.model.enums.YutResult;
import com.sw.yutnori.ui.display.ResultDisplay;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

public class FxResultDisplay implements ResultDisplay {

    private final Label[] resultLabels;
    private final Label currentYutLabel;
    private int currentResultIndex = 0;

    public FxResultDisplay(Label[] resultLabels, Label currentYutLabel) {
        this.resultLabels = resultLabels;
        this.currentYutLabel = currentYutLabel;
    }

    @Override
    public void displayYutResult(String result) {
        String koreanResult = convertYutTypeToKorean(result);

        // 윷이나 모인 경우 특별 처리
        if (koreanResult.equals("윷") || koreanResult.equals("모")) {
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
                // HTML 대신 HBox와 Text 노드 사용
                Text baseText = new Text(result);
                baseText.setFont(Font.font("System", FontWeight.BOLD, 25));

                Text superText = new Text("2");
                superText.setFont(Font.font("System", FontWeight.BOLD, 16));
                superText.setTranslateY(-10); // 윗첨자 위치로 이동

                HBox container = new HBox(0, baseText, superText);
                container.setAlignment(Pos.CENTER);

                resultLabels[i].setText("");
                resultLabels[i].setGraphic(container);
                return;
            }

            // 동일한 윷, 모가 3번 이상 나오는 경우 (Label의 graphic이 이미 설정된 경우)
            if (resultLabels[i].getGraphic() instanceof HBox container && labelText.isEmpty()) {
                if (container.getChildren().size() == 2 &&
                        container.getChildren().get(0) instanceof Text baseText &&
                        container.getChildren().get(1) instanceof Text superText) {

                    if (baseText.getText().equals(result)) {
                        try {
                            int count = Integer.parseInt(superText.getText());
                            superText.setText(String.valueOf(count + 1));
                            return;
                        } catch (NumberFormatException e) {
                            System.err.println("윗첨자 파싱 오류: " + e);
                        }
                    }
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
    public void updateCurrentYut(String yutType) {
        currentYutLabel.setText(convertYutTypeToKorean(yutType));
    }

    @Override
    public void resetResults() {
        for (Label label : resultLabels) {
            label.setText("-");
        }
        currentResultIndex = 0;
    }

}
