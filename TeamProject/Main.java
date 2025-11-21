import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

//프로그램을 시작하는 파일, 페이지를 넘겨주는 역할
public class Main extends JFrame{
	private CardLayout cardLayout;
    private JPanel mainContainer;
    private GamePanel gamePanel;
    private GameOverPanel gameoverpanel;
	public Main() {
		setTitle("대저택의 미로");
		setSize(1000,700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); //창을 모니터 가운데 띄우기
		
		 cardLayout =new CardLayout();
		mainContainer = new JPanel(cardLayout);//페이지를 담을 통
		//패널들 순서대로 작성
		gamePanel= new GamePanel(this);
		StartPanel startPanel = new StartPanel(cardLayout, mainContainer, gamePanel, 1000, 700);
		gameoverpanel= new GameOverPanel(this);
		
		mainContainer.add(startPanel,"START"); //START라는 이름을 붙여줌
		mainContainer.add(gamePanel,"GAME"); //GAME이라는 이름을 붙여줌
		mainContainer.add(gameoverpanel,"GAMEOVER");
		
		add(mainContainer); //스케치북 붙이기
		cardLayout.show(mainContainer, "START");//첫 페이지 보여주기
		setVisible(true);
	} 
	public void showCard(String name) {
		cardLayout.show(mainContainer, name);
    }
	public void triggerGameOver(String reason) {
        gameoverpanel.setReason(reason); // 이유 적어주고
        cardLayout.show(mainContainer, "GAMEOVER"); // 화면 넘기기!
        gamePanel.stopGame(); // 게임 멈춰!
    }
	public void restartGame() {
        gamePanel.resetGame(); // 게임 초기화 (위치 원상복구)
        cardLayout.show(mainContainer, "GAME");
        gamePanel.requestFocus(); // 키보드 입력 받게 포커스
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Main();
	}

}
