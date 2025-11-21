import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GameOverPanel extends JPanel{
	Main main;
	JLabel messageLabel; //시간 초과 또는 함정에 빠졌을때 이유를 알려줄 라벨
	public GameOverPanel(Main main) {
		this.main =main;
		setLayout(null);
		setBackground(Color.BLACK); //배경은 무서운 검은색
		
		//GAMEOVER
		JLabel titleLabel =new JLabel("GAME OVER",SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial",Font.BOLD, 80));
		titleLabel.setForeground(Color.RED);
		titleLabel.setBounds(0,100,1000,100);
		add(titleLabel);
		
		//죽은 이유
		messageLabel =new JLabel("", SwingConstants.CENTER);
		messageLabel.setFont(new Font("Arial", Font.BOLD,30));
		messageLabel.setForeground(Color.WHITE); 
		messageLabel.setBounds(0,250,1000,50);
		add(messageLabel);
		
		JButton retrybtn =new JButton("다시 도전");
		retrybtn.setBounds(300,400,150,50);
		retrybtn.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent e) {
                main.restartGame(); // 메인한테 "게임 다시 시작해줘!" 부탁하기
            }
		});
		add(retrybtn);
		
		ImageIcon btnIcon=new ImageIcon("");//이미지
		JButton menubtn =new JButton(btnIcon);
		menubtn.setBounds(550, 400, 150, 50);
        menubtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.showCard("START"); // 시작 화면으로 가기
            }
        });
        add(menubtn);
	}
	public void setReason(String text) {
        messageLabel.setText(text);
    }
}
