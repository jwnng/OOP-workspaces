import java.awt.CardLayout;	
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StartPanel extends JPanel {
	Image backgroundImage; //배경
	Font myCustomFont; //가져온 폰트
	
public StartPanel(CardLayout cardlayout, JPanel mainContainer, GamePanel gamePanel) {
	setLayout(null);// 내 맘대로 배치
	backgroundImage=new ImageIcon("C:\\Users\\user\\eclipse-workspace\\OOP-workspaces\\TeamProject\\Images\\Background\\FirstBackground.png").getImage(); //배경 이미지 불러오기
	try {
		myCustomFont=Font.createFont(Font.TRUETYPE_FONT, new File("Galmuri11-Bold.ttf")).deriveFont(80f);//폰트 크기
	}catch(Exception e){
		System.out.println("폰트 파일을 못 찾았어요. 기본 폰트로 합니다.");
        myCustomFont = new Font("Arial", Font.BOLD, 80);
	}
	//게임 제목 넣기
	JLabel titleLabel = new JLabel("대저택의 미로",JLabel.CENTER);
	titleLabel.setFont(myCustomFont); //폰드 적용
	titleLabel.setForeground(Color.WHITE); //글자색
	titleLabel.setBounds(0,100,1000,150); //위치
	add(titleLabel);
	
	
	//시작 버튼 넣기
	ImageIcon btnIcon=new ImageIcon("C:\\Users\\user\\eclipse-workspace\\OOP-workspaces\\TeamProject\\Images\\Buttons\\Play.png");//이미지
	JButton startbtn =new JButton(btnIcon);
	
	startbtn.setBorderPainted(false);//테두리 선 없애기
	startbtn.setContentAreaFilled(false); //버튼 배경색 없애기
	startbtn.setFocusPainted(false); //클릭했을 때 점선 테두리 없애기
	
	startbtn.setBounds(400,400,200,80); //버튼 위치
	//페이지 넘기기
	startbtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // "GAME"화면으로 넘김
            cardlayout.show(mainContainer, "GAME");

            // 화면이 바뀌면 게임 화면(GamePanel)이 키보드 입력을 받을 수 있게 '포커스'
            gamePanel.requestFocus(); 
        }
    });
	add(startbtn);
}	
@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    // 화면 가득 채우기
    if (backgroundImage != null) {
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
}
