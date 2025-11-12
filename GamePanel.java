// GamePanel.java (수정된 코드)

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*; // KeyListener, KeyEvent, ActionListener, Timer를 위해
import java.io.File;
import java.io.IOException;
import java.util.HashSet; // Set 사용을 위해
import java.util.Set;     // Set 사용을 위해
 
// ActionListener를 추가하여 Timer 이벤트를 처리합니다.
public class GamePanel extends JPanel implements KeyListener, ActionListener { 
    
    // --- 필드(멤버 변수) ---
    private final int TILE_SIZE = 32;
    private final int SPEED = 4; // 이동 속도 설정
    private final int FPS = 60; // 초당 프레임 수
    private final int DELAY = 1000 / FPS; // 게임 루프 지연 시간 (약 16ms)
    private Timer gameTimer; // 게임 루프를 위한 Timer

    private final int[][] MAP = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 1, 1, 1, 0, 1, 0, 1},
        {1, 0, 1, 0, 0, 1, 0, 1, 0, 1},
        {1, 0, 1, 0, 1, 1, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    
    private Player player1; // Player 1 (WASD)
    private Player player2; // Player 2 (화살표)
    
    // 현재 눌린 모든 키 코드를 저장하는 Set
    private final Set<Integer> pressedKeys = new HashSet<>(); 
    
    // --- 생성자 ---
    public GamePanel() {
        this.setFocusable(true); 
        this.addKeyListener(this); 
        loadAssets(); 
        
        // Timer 설정: DELAY마다 actionPerformed()를 호출합니다. (게임 루프 시작)
        gameTimer = new Timer(DELAY, this);
        gameTimer.start();
        
        // 게임 시작 시 포커스 요청 (키 입력을 받기 위해)
        this.requestFocusInWindow(); 
    }
    
    // ... (Getter 메서드는 동일하게 유지) ...
    
    // --- 이미지 로드 메서드 ---
    private void loadAssets() {
        try {
            Image img1 = ImageIO.read(new File("C:\\Users\\user\\eclipse-workspace\\teample_java2\\images\\여자 주인공.png"));
            // player1, player2 구분을 위해 같은 이미지를 사용하되, 다른 시작 위치를 지정합니다.
            Image img2 = ImageIO.read(new File("C:\\Users\\user\\eclipse-workspace\\teample_java2\\images\\여자 주인공.png"));

            // Player 1 (WASD) 초기 위치 설정 (1행 1열)
            player1 = new Player(TILE_SIZE * 1, TILE_SIZE * 1, img1);
            // Player 2 (화살표) 초기 위치 설정 (1행 8열)
            player2 = new Player(TILE_SIZE * 8, TILE_SIZE * 1, img2);
			
        } catch (IOException e) {
            System.err.println("캐릭터 이미지를 로드할 수 없습니다. 경로를 다시 확인하세요");
            e.printStackTrace();
        }
    }
    
    // --- 드로잉 메서드 (화면을 그리는 핵심 메서드) ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMaze(g); 
        drawPlayers(g); // 두 플레이어를 그립니다.
        Toolkit.getDefaultToolkit().sync(); 
    }
    
    // 미로 그리기 (drawMaze는 동일하게 유지)
    private void drawMaze(Graphics g) {
        // ... (기존 drawMaze 로직) ...
        for (int row = 0; row < MAP.length; row++) {
            for (int col = 0; col < MAP[0].length; col++) {
                if (MAP[row][col] == 1) { 
                    g.setColor(Color.DARK_GRAY); 
                    g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE); 
                } else { 
                    g.setColor(Color.LIGHT_GRAY); 
                    g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE); 
                }
            }
        }
    }
    
    // 두 플레이어 그리기 메서드
    private void drawPlayers(Graphics g) {
        // Player 1 그리기 (파란색 테두리로 구분)
        if (player1 != null && player1.getImage() != null) { 
            g.drawImage(player1.getImage(), player1.getX(), player1.getY(), player1.getSize(), player1.getSize(), this);
            g.setColor(Color.BLUE);
            g.drawRect(player1.getX(), player1.getY(), player1.getSize(), player1.getSize()); // 구분용 테두리
        }
        
        // Player 2 그리기 (빨간색 테두리로 구분)
        if (player2 != null && player2.getImage() != null) { 
            g.drawImage(player2.getImage(), player2.getX(), player2.getY(), player2.getSize(), player2.getSize(), this);
            g.setColor(Color.RED);
            g.drawRect(player2.getX(), player2.getY(), player2.getSize(), player2.getSize()); // 구분용 테두리
        }
    }
    
    // -------------------------------------------------------------
    // --- 게임 루프 로직 (Timer에 의해 주기적으로 호출됨) ---
    // -------------------------------------------------------------
    
    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame(); // 게임 로직 업데이트 (이동 및 충돌)
        repaint();    // 화면 다시 그리기
    }
    
    private void updateGame() {
        // 1. 임시 위치 저장 (충돌 시 되돌리기 위해)
        int oldX1 = player1.getX();
        int oldY1 = player1.getY();
        int oldX2 = player2.getX();
        int oldY2 = player2.getY();
        
        // 2. 키 상태를 확인하여 플레이어 이동 (WASD)
        if (pressedKeys.contains(KeyEvent.VK_W)) player1.setY(player1.getY() - SPEED);
        if (pressedKeys.contains(KeyEvent.VK_S)) player1.setY(player1.getY() + SPEED);
        if (pressedKeys.contains(KeyEvent.VK_A)) player1.setX(player1.getX() - SPEED);
        if (pressedKeys.contains(KeyEvent.VK_D)) player1.setX(player1.getX() + SPEED);

        // 3. 키 상태를 확인하여 플레이어 이동 (화살표)
        if (pressedKeys.contains(KeyEvent.VK_UP)) player2.setY(player2.getY() - SPEED);
        if (pressedKeys.contains(KeyEvent.VK_DOWN)) player2.setY(player2.getY() + SPEED);
        if (pressedKeys.contains(KeyEvent.VK_LEFT)) player2.setX(player2.getX() - SPEED);
        if (pressedKeys.contains(KeyEvent.VK_RIGHT)) player2.setX(player2.getX() + SPEED);

        // 4. 충돌 검사 및 되돌리기
        if (checkCollision(player1)) {
            player1.setX(oldX1);
            player1.setY(oldY1);
        }
        if (checkCollision(player2)) {
            player2.setX(oldX2);
            player2.setY(oldY2);
        }
    }
    
    // 특정 Player 객체를 받아 충돌을 검사하는 메서드로 변경
    private boolean checkCollision(Player targetPlayer) {
        // 주인공이 현재 위치한 맵 타일의 인덱스 계산
        int playerTileCol = (targetPlayer.getX() + targetPlayer.getSize() / 2) / TILE_SIZE;
        int playerTileRow = (targetPlayer.getY() + targetPlayer.getSize() / 2) / TILE_SIZE;
        
        // 맵 범위 확인
        if (playerTileRow < 0 || playerTileRow >= MAP.length ||
                playerTileCol < 0 || playerTileCol >= MAP[0].length) {
            return true; // 맵 밖은 충돌로 간주
        }
        
        // 현재 주인공 위치의 맵 값이 1(벽)인지 확인
        return MAP[playerTileRow][playerTileCol] == 1;
        
        // 참고: 플레이어 간 충돌을 원하면 이 곳에 로직을 추가해야 합니다.
    }
    
    // -------------------------------------------------------------
    // --- 키 입력 리스너 (키 상태만 업데이트) ---
    // -------------------------------------------------------------
    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode()); // 키가 눌리면 Set에 추가
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode()); // 키가 떼어지면 Set에서 제거
    }
    
    @Override
    public void keyTyped(KeyEvent e) {} // 사용하지 않음

	// GamePanel.java (추가할 코드)

// ... (생성자 public GamePanel() 닫는 } 바로 아래에 추가) ...
    
// --- Getter 메서드: Main 클래스가 창 크기 계산을 위해 사용 ---
	public int getMapHeight() {
		return MAP.length; // 맵의 세로 길이 (행의 개수) 반환
	}

	public int getMapWidth() {
		return MAP[0].length; // 맵의 가로 길이 (열의 개수) 반환
	}

	public int getTileSize() {
		return TILE_SIZE; // 타일의 크기 반환
	}
	// ... (loadAssets() 메서드로 이어집니다.) ...
}
