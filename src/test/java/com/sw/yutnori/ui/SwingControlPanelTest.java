//package com.sw.yutnori.ui;
//
//import com.sw.yutnori.client.TestYutnoriApiClient;
//import org.junit.jupiter.api.Test;
//import javax.swing.*;
//import java.awt.*;
//import java.lang.reflect.InvocationTargetException;
//
//class SwingControlPanelTest {
//
//    @Test
//    void testControlPanel() throws InvocationTargetException, InterruptedException {
//        SwingUtilities.invokeAndWait(() -> {
//            // 테스트 프레임 생성
//            JFrame testFrame = new JFrame("컨트롤 패널 테스트");
//            testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//            testFrame.setSize(400, 600);
//
//            // 테스트용 API 클라이언트 생성
//            TestYutnoriApiClient apiClient = new TestYutnoriApiClient();
//
//            // 컨트롤 패널 생성 및 테스트
//            SwingYutControlPanel controlPanel = new SwingYutControlPanel(apiClient);
//            controlPanel.setGameContext(1L, 1L);
//
//            testFrame.add(controlPanel, BorderLayout.CENTER);
//            testFrame.setVisible(true);
//
//            // UI 확인 시간
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            testFrame.dispose();
//        });
//    }
//}