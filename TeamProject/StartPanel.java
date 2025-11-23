import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StartPanel extends JPanel {
    Image backgroundImage;
    Font myCustomFont;

    // 생성자 파라미터 수정: GamePanel -> MainMap
    public StartPanel(CardLayout cardLayout, JPanel mainContainer, MainMap mainMap) {
        setLayout(null);
        
        // 이미지 경로 수정 (상대 경로)
        backgroundImage = new ImageIcon("Images/Background/FirstBackground.png").getImage(); 
        
        // 폰트 설정 (없으면 기본 폰트)
        myCustomFont = new Font("Arial", Font.BOLD, 80);

        JLabel titleLabel = new JLabel("Mansion Maze", JLabel.CENTER);
        titleLabel.setFont(myCustomFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 100, 1000, 150);
        add(titleLabel);

        // 시작 버튼
        ImageIcon btnIcon = new ImageIcon("Images/Buttons/Play.png"); // 경로 수정
        Image scaledImage = btnIcon.getImage().getScaledInstance(300, 120, Image.SCALE_SMOOTH);
        JButton startBtn = new JButton(new ImageIcon(scaledImage));
        
        // 이미지가 없으면 글씨라도 나오게 처리
        if (btnIcon.getIconWidth() == -1) startBtn.setText("GAME START");

        startBtn.setBorderPainted(false);
        startBtn.setContentAreaFilled(false);
        startBtn.setFocusPainted(false);
        startBtn.setBounds(700, 580, 200, 80);

        startBtn.addActionListener(e -> {
            cardLayout.show(mainContainer, "GAME"); // 게임 화면으로 전환
            mainMap.requestFocus(); // 키보드 입력 받기 시작
        });
        add(startBtn);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}