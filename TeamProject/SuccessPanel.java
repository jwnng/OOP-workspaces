import java.awt.*;
import javax.swing.*;

public class SuccessPanel extends JPanel {
    Main main;

    public SuccessPanel(Main main) {
        this.main = main;
        setLayout(null);
        setBackground(Color.BLACK);

        
        // "SUCCESS"
        JLabel titleLabel = new JLabel("SUCCESS!!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 80));
        titleLabel.setForeground(Color.GREEN);
        titleLabel.setBounds(0, 150, 1000, 100); 
        add(titleLabel);

        // "CONGRATULATIONS"
        JLabel subTitleLabel = new JLabel("CONGRATULATIONS", SwingConstants.CENTER);
        subTitleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        subTitleLabel.setForeground(Color.WHITE);
        subTitleLabel.setBounds(0, 230, 1000, 100); 
        add(subTitleLabel);
        
        // [다시 도전] 버튼
        JButton retryBtn = new JButton("다시 도전");
        retryBtn.setBounds(330, 500, 150, 50); 
        retryBtn.setFocusable(false);
        retryBtn.addActionListener(e -> main.restartGame());
        add(retryBtn);

        // [메인 메뉴] 버튼
        JButton menuBtn = new JButton("메인 메뉴");
        menuBtn.setBounds(520, 500, 150, 50);
        menuBtn.setFocusable(false);
        menuBtn.addActionListener(e -> main.showCard("START"));
        add(menuBtn);
    }
}