import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    
    // 우리가 만든 게임 화면 클래스 이름은 'MainMap'입니다.
    private MainMap mainMap; 
    private GameOverPanel gameOverPanel;
    private StartPanel startPanel;

    public Main() {
        setTitle("대저택의 미로");
        setSize(1000, 800); // 맵 크기에 맞춰 조금 넉넉하게
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 모니터 중앙 배치
        setResizable(false); // 창 크기 고정

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // 1. 게임 화면 생성
        mainMap = new MainMap(this); // Main을 알려줘서 게임 오버 때 호출하게 함

        // 2. 시작 화면 생성
        startPanel = new StartPanel(cardLayout, mainContainer, mainMap);

        // 3. 게임 오버 화면 생성
        gameOverPanel = new GameOverPanel(this);

        // 카드(화면) 담기
        mainContainer.add(startPanel, "START");
        mainContainer.add(mainMap, "GAME");
        mainContainer.add(gameOverPanel, "GAMEOVER");

        add(mainContainer);

        // 첫 화면은 START로 설정
        cardLayout.show(mainContainer, "START");
        setVisible(true);
    }

    // 게임 오버가 되면 호출되는 함수
    public void triggerGameOver(String reason) {
        gameOverPanel.setReason(reason); // 죽은 이유 설정
        cardLayout.show(mainContainer, "GAMEOVER"); // 게임 오버 화면 보여주기
        mainMap.stopGame(); // 게임 정지
    }

    // 다시 시작 버튼을 누르면 호출되는 함수
    public void restartGame() {
        mainMap.resetGame(); // 게임 초기화
        cardLayout.show(mainContainer, "GAME"); // 게임 화면 보여주기
        mainMap.requestFocus(); // 키보드 입력 받기 위해 포커스 설정
    }

    // 메인 메뉴로 돌아가기
    public void showCard(String name) {
        cardLayout.show(mainContainer, name);
    }

    public static void main(String[] args) {
        new Main();
    }
}