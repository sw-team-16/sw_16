package com.sw.yutnori.ui.javafx.display;

import com.sw.yutnori.ui.display.YutDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;

public class FxYutDisplay implements YutDisplay {

    private final ImageView[] yutSticks;
    private final Image upImage;
    private final Image downImage;
    private final Image backDoDownImage;

    public FxYutDisplay(ImageView[] yutSticks, Image upImage, Image downImage, Image backDoDownImage) {
        this.yutSticks = yutSticks;
        this.upImage = upImage;
        this.downImage = downImage;
        this.backDoDownImage = backDoDownImage;
    }

    @Override
    public void displayYutSticks(String yutType) {
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
            case "BACK_DO": // 빽도 (별도 관리)
                displayBackDo(random);
                break;
        }
    }

    @Override
    public void reset() {
        for (ImageView stick : yutSticks) {
            stick.setImage(upImage);
        }
    }

    // 랜덤 적용해 윷이 매번 다르게 뒤집히도록 함
    private void flipRandomSticks(int count, Random random) {
        boolean[] flipped = new boolean[4];
        int flippedCount = 0;

        while (flippedCount < count) {
            int idx = random.nextInt(4);
            if (!flipped[idx]) {
                yutSticks[idx].setImage(downImage);
                flipped[idx] = true;
                flippedCount++;
            }
        }

    }

    private void flipAllSticks() {
        for (ImageView stick : yutSticks) {
            stick.setImage(downImage);
        }
    }

    private void displayBackDo(Random random) {
        int backDoIdx = random.nextInt(4);
        yutSticks[backDoIdx].setImage(backDoDownImage);
    }
}
