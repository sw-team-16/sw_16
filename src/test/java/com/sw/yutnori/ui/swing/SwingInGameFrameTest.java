//package com.sw.yutnori.ui;
//
//import org.junit.jupiter.api.Test;
//import javax.swing.*;
//import java.lang.reflect.InvocationTargetException;
//
//class SwingInGameFrameTest {
//
//    @Test
//    void testUIComponents() throws InvocationTargetException, InterruptedException {
//        // UI 테스트는 EDT에서 실행해야 함
//        SwingUtilities.invokeAndWait(() -> {
//            // 테스트용 프레임 생성
//            SwingInGameFrame frame = new SwingInGameFrame();
//
//            // 테스트를 수동으로 확인할 시간 제공
//            try {
//                Thread.sleep(5000); // 5초 동안 UI 확인
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            // 테스트 종료 후 프레임 닫기
//            frame.dispose();
//        });
//    }
//}