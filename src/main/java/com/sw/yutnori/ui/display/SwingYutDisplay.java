package com.sw.yutnori.ui.display;

import javax.swing.*;
import java.util.Random;

public class SwingYutDisplay implements YutDisplay {
    private final JLabel[] yutSticks;
    private final ImageIcon upIcon;
    private final ImageIcon downIcon;
    private final ImageIcon backDoDownIcon;

    public SwingYutDisplay(JLabel[] yutSticks, ImageIcon upIcon, ImageIcon downIcon, ImageIcon backDoDownIcon) {
        this.yutSticks = yutSticks;
        this.upIcon = upIcon;
        this.downIcon = downIcon;
        this.backDoDownIcon = backDoDownIcon;
    }

    @Override
    public void displayYutResult(String yutType) {
        reset();

        Random random = new Random();

        switch (yutType) {
            case "DO": // 도 (1개 뒤집힘)
                flipRandomSticks(1, random);
                break;
            case "GAE": // 개 (2개 뒤집힘)
                flipRandomSticks(2, random);
                break;
            case "GEOL": // 걸 (3개 뒤집힘)
                flipRandomSticks(3, random);
                break;
            case "YUT": // 윷 (4개 모두 뒷면)
                flipAllSticks();
                break;
            case "MO": // 모 (4개 모두 앞면)
                // 초기화된 상태와 동일
                break;
            case "BACK_DO": // 빽도 (1개 뒤집힘, 특별 처리 필요시)
                displayBackDo(random);
                break;
        }
    }

    @Override
    public void reset() {
        for (JLabel stick : yutSticks) {
            stick.setIcon(upIcon);
        }
    }

    private void flipRandomSticks(int count, Random random) {
        boolean[] flipped = new boolean[4];
        int flippedCount = 0;

        while (flippedCount < count) {
            int idx = random.nextInt(4);
            if (!flipped[idx]) {
                yutSticks[idx].setIcon(downIcon);
                flipped[idx] = true;
                flippedCount++;
            }
        }
    }

    private void flipAllSticks() {
        for (JLabel stick : yutSticks) {
            stick.setIcon(downIcon);
        }
    }

    private void displayBackDo(Random random) {
        int backDoIndex = random.nextInt(4);
        yutSticks[backDoIndex].setIcon(backDoDownIcon);
    }
}
