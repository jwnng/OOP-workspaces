import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameUI extends JFrame {

	public GameUI(){
		//윈도우 창 설정
		GamePanel gamePanel=new GamePanel(); //인스턴스 생성
		setTitle("대저택의 미로");
		int width = gamePanel.getMapWidth() * gamePanel.getTileSize(); 
		int height = gamePanel.getMapHeight() * gamePanel.getTileSize() + 25;
		setSize(width,height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //화면 종료
		setLocationRelativeTo(null);//화면 중앙에 배치
		setResizable(false); //창 크기 조절 불가
		
		add(gamePanel);// 창 보이게 설정
		setVisible(true);
	}
	public static void main(String[] args) {
		
		new GameUI();
	}

}
