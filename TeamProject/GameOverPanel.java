import java.awt.*;
import javax.swing.*;

public class GameOverPanel extends JPanel {
    Main main;
    JLabel messageLabel;

    public GameOverPanel(Main main) {
        this.main = main;
        setLayout(null);
        setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("GAME OVER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 80));
        titleLabel.setForeground(Color.RED);
        titleLabel.setBounds(0, 100, 1000, 100);
        add(titleLabel);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 30));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBounds(0, 250, 1000, 50);
        add(messageLabel);

        JButton retryBtn = new JButton("다시 도전");
        retryBtn.setBounds(300, 400, 150, 50);
        retryBtn.addActionListener(e -> main.restartGame());
        add(retryBtn);

        JButton menuBtn = new JButton("메인 메뉴");
        menuBtn.setBounds(550, 400, 150, 50);
        menuBtn.addActionListener(e -> main.showCard("START"));
        add(menuBtn);
    }

    public void setReason(String text) {
        messageLabel.setText(text);
    }
}