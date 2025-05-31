package com.sw.yutnori.ui.swing.display;

import com.sw.yutnori.model.Player;
import com.sw.yutnori.ui.display.DialogDisplay;
import com.sw.yutnori.ui.swing.SwingGameSetupFrame;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

@Setter
public class SwingDialogDisplay implements DialogDisplay {

    private Component parentComponent;

    @Override
    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
                parentComponent,
                message,
                "오류",
                JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void showWinnerDialog(String winnerName) {
        Window window = SwingUtilities.getWindowAncestor(parentComponent);
        JFrame frame = (window instanceof JFrame) ? (JFrame) window : null;
        if (frame == null) {
            JOptionPane.showMessageDialog(parentComponent,
                    winnerName + "님이 승리했습니다!",
                    "게임 종료",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 오버레이(GlassPane) 생성
        JPanel overlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 120)); // 반투명 overlay
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        overlay.setLayout(new GridBagLayout());

        // 중앙 게임 종료 Panel 생성
        JPanel dialogPanel = new JPanel();
        dialogPanel.setBackground(Color.WHITE);
        dialogPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        dialogPanel.setPreferredSize(new Dimension(350, 200));

        JLabel label = new JLabel(winnerName + "님이 승리했습니다!");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 22f));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(36, 0, 30, 0));
        dialogPanel.add(label);

        dialogPanel.add(Box.createVerticalGlue());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        JButton restartBtn = new JButton("재시작");
        JButton exitBtn = new JButton("종료");
        restartBtn.setPreferredSize(new Dimension(100, 38));
        exitBtn.setPreferredSize(new Dimension(100, 38));
        buttonPanel.add(restartBtn);
        buttonPanel.add(exitBtn);
        dialogPanel.add(buttonPanel);

        // 버튼 동작
        restartBtn.addActionListener(e -> {
            frame.getGlassPane().setVisible(false);
            closeWindowAndOpenSetup();
        });
        exitBtn.addActionListener(e -> System.exit(0));

        // 오버레이에 다이얼로그 중앙 배치
        overlay.add(dialogPanel, new GridBagConstraints());
        frame.setGlassPane(overlay);
        overlay.setVisible(true);
        overlay.requestFocusInWindow();
    }

    private void closeWindowAndOpenSetup() {
        Window window = SwingUtilities.getWindowAncestor(parentComponent);
        if (window != null) {
            window.dispose();
        }
        SwingUtilities.invokeLater(() -> {
            SwingGameSetupFrame frame = new SwingGameSetupFrame();
            frame.setVisible(true);
        });
    }


    @Override
    public void showNoOnboardPieceDialog() {
        JOptionPane.showMessageDialog(
                null,
                "OnBoard 상태의 말이 없어 턴을 넘깁니다.",
                "빽도",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void showGoalDialog(String playerName, int pieceNumber) {
        JOptionPane.showMessageDialog(
                null,
                playerName + "님의 " + pieceNumber + "번 말이 도착지에 도달했습니다!",
                "완주",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void showCaptureDialog() {
        JOptionPane.showMessageDialog(null,
                "상대 말을 잡았습니다!",
                "잡기",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void showCarryDialog(String groupedPieces) {
        JOptionPane.showMessageDialog(
                null,
                "같은 위치의 아군 말을 업었습니다: " + groupedPieces,
                "업기",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void showOneMoreTurnDialog() {
        JOptionPane.showMessageDialog(null,
                "한 번 더 이동할 수 있습니다. 윷을 던지세요.",
                "추가 턴",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public Object pieceSelctionDialog(Player player, String[] displayOptions) {
        return JOptionPane.showInputDialog(
                null,
                "[" + player.getName() + "] 사용할 말을 선택하세요",
                "말 선택",
                JOptionPane.PLAIN_MESSAGE,
                null,
                displayOptions,
                displayOptions[0]
        );
    }

    @Override
    public Object yutSelectionDialog(String[] displayOptions) {
            return JOptionPane.showInputDialog(
                null,
                "사용할 윷 결과를 선택하세요",
                "윷 결과 선택",
                JOptionPane.PLAIN_MESSAGE,
                null,
                displayOptions,
                displayOptions[0]
        );
    }
}
