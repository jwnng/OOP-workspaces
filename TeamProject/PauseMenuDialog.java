import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class PauseMenuDialog extends JDialog {

    private final Main main;

    public PauseMenuDialog(Main owner) {
        super(owner, "일시정지", true);
        this.main = owner;

        setSize(350, 330);
        setResizable(false);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // ===== 버튼 패널 =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 10));

        JButton btnResume  = new JButton("계속하기");
        JButton btnRestart = new JButton("다시 시작");
        JButton btnHome    = new JButton("메인 메뉴로");
        JButton btnQuit    = new JButton("게임 종료");

        buttonPanel.add(btnResume);
        buttonPanel.add(btnRestart);
        buttonPanel.add(btnHome);
        buttonPanel.add(btnQuit);

        // ===== 소리 설정 패널 =====
        JPanel soundPanel = new JPanel(new BorderLayout());
        JLabel soundLabel = new JLabel("🔊 소리 크기", SwingConstants.CENTER);
        soundLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JSlider volumeSlider = new JSlider(0, 100, 80);
        volumeSlider.setMajorTickSpacing(20);
        volumeSlider.setMinorTickSpacing(10);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);

        soundPanel.add(soundLabel, BorderLayout.NORTH);
        soundPanel.add(volumeSlider, BorderLayout.CENTER);

        add(buttonPanel, BorderLayout.CENTER);
        add(soundPanel, BorderLayout.SOUTH);

        // -------------------
        //  슬라이더 → actual volume 적용
        // -------------------
        volumeSlider.addChangeListener(e -> {
            float v = volumeSlider.getValue() / 100f;
            main.setBgmVolume(v);   // Main → MainMap/StartPanel → BgmLoop.setVolume()
        });

        // ===== 버튼 동작 =====
        btnResume.addActionListener(e -> {
            dispose();
            main.resumeFromPause();
        });

        btnRestart.addActionListener(e -> {
            dispose();
            main.restartFromPause();
        });

        btnHome.addActionListener(e -> {
            dispose();
            main.goHomeFromPause();
        });

        btnQuit.addActionListener(e -> {
            dispose();
            main.quitGameFromPause();
        });

        // ESC 눌르면 "계속하기"
        getRootPane().registerKeyboardAction(e -> {
            dispose();
            main.resumeFromPause();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
}

