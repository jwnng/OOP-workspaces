import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

public class StartPanel extends JPanel {
    Image backgroundImage;
    Font myCustomFont;
    BgmLoop startBgm; // 시작 화면 bgm

    public StartPanel(CardLayout cardLayout, JPanel mainContainer, MainMap mainMap) {
        setLayout(null);
        
        // 배경 이미지
        backgroundImage = new ImageIcon("Images/Background/FirstBackground.png").getImage(); 
        // 타이틀 폰트
        myCustomFont = new Font("Arial", Font.BOLD, 80);

        // 시작화면 BGM
        startBgm = new BgmLoop("sound/start_bgm.wav");
        startBgm.start();

        // 타이틀
        JLabel titleLabel = new JLabel("Mansion Maze", JLabel.CENTER);
        titleLabel.setFont(myCustomFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 100, 1000, 150);
        add(titleLabel);

        // 시작 버튼
        ImageIcon btnIcon = new ImageIcon("Images/Buttons/Play.png");
        Image scaledImage = btnIcon.getImage().getScaledInstance(300, 120, Image.SCALE_SMOOTH);
        JButton startBtn = new JButton(new ImageIcon(scaledImage));
        
        if (btnIcon.getIconWidth() == -1) {
            startBtn.setText("GAME START");
        }

        startBtn.setBorderPainted(false);
        startBtn.setContentAreaFilled(false);
        startBtn.setFocusPainted(false);
        startBtn.setBounds(700, 580, 200, 80);
        startBtn.setFocusable(false);

        startBtn.addActionListener(e -> {
            if (startBgm != null) startBgm.stopMusic(); // 시작화면 음악 끄기
            cardLayout.show(mainContainer, "GAME");     // 게임 화면으로 전환
            mainMap.requestFocusInWindow();      
            mainMap.startBgm();                         // 게임 브금 켜기
        });
        add(startBtn);

        // 🔊 시작화면 전용 볼륨 슬라이더 (상단 오른쪽)
        // 프레임 가로 1000 기준으로 오른쪽 위에 붙임
        int sliderWidth = 220;
        int sliderHeight = 40;
        int marginRight = 30;
        int marginTop = 40;
        int sliderX = 1000 - sliderWidth - marginRight; // 오른쪽에서 20px 떨어짐
        int sliderY = marginTop;

        JSlider volumeSlider = new JSlider(0, 100, 80); // 0~100, 기본값 80
        volumeSlider.setBounds(sliderX, sliderY, sliderWidth, sliderHeight);
        volumeSlider.setOpaque(false); // 배경 안 칠해서 배경 이미지랑 어울리게

        volumeSlider.addChangeListener(e -> {
            float v = volumeSlider.getValue() / 100f; // 0.0 ~ 1.0
            if (startBgm != null) {
                startBgm.setVolume(v);
            }
        });

        add(volumeSlider);
    }

    // 일시정지 메뉴에서 전체 볼륨 조절할 때도 시작 브금에 반영하고 싶으면 이 메서드 사용
    public void setBgmVolume(float v) {
        if (startBgm != null) {
            startBgm.setVolume(v);   // 0.0f ~ 1.0f
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
