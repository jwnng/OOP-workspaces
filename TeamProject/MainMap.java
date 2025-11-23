import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Rectangle; //충돌 검사
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MainMap extends JPanel {
    Main main; // 메인 프레임 참조
    Player p1, p2;
    Image wallImage, backgroundImage;
    Timer gameLoop; //게임 상태를 계속 체크하는 타이머

    // 생성자 수정: Main을 받아옴
    public MainMap(Main main) {
        this.main = main;
        initMap();
    }
    public MainMap() {
        initMap(); // Main 없이 맵만 초기화
    }

    // 맵 초기화 함수
    private void initMap() {
        int mapHeight = Collision.tileMap.length * Collision.TILE_SIZE;
        int mapWidth = Collision.tileMap[0].length * Collision.TILE_SIZE;
        setPreferredSize(new Dimension(mapWidth, mapHeight));
        setLayout(null);
        setBackground(Color.BLACK);

        wallImage = new ImageIcon("Images/Tile/WoodTile1.png").getImage();
        backgroundImage = new ImageIcon("Images/Background/Background.jpg").getImage();

        // 플레이어 생성
        createPlayers();

        setFocusable(true);
        setupKeyListener();
        startGameLoop();//둘이 만나는지 검사
    }
 // 게임 루프 시작 함수
    private void startGameLoop() {
        if (gameLoop != null) gameLoop.stop(); // 기존 타이머가 있으면 정지

        gameLoop = new Timer(30, e -> {
        	if (p1 != null) p1.update();
            if (p2 != null) p2.update();
            checkMeeting(); // 1. 둘이 만났는지 확인
            repaint();      // 2. 화면 다시 그리기 (플레이어 이동 반영)
        });
        gameLoop.start();
    }

    //핵심 기능: 두 플레이어가 만났는지 확인하는 함수
    private void checkMeeting() {
        if (p1 == null || p2 == null || p1.isDead || p2.isDead) return;

        // p1과 p2의 위치와 크기(사각형)를 가져옴
        // Player 클래스 안에 있는 character(JLabel)의 위치 정보를 이용
        Rectangle r1 = p1.character.getBounds();
        Rectangle r2 = p2.character.getBounds();

        // 두 사각형이 겹치면(intersects) 만난 것!
        if (r1.intersects(r2)) {
            success(); // 성공 처리
        }
    }
    
    private void createPlayers() {
        // 기존 플레이어가 있다면 제거 (재시작 시)
        if (p1 != null) remove(p1.character);
        if (p2 != null) remove(p2.character);

        p1 = new Player(this, 100, 100);//플레이어 위치
        setImage(p1, "Images/Girls/Girl_Idle.png");

        p2 = new Player(this, 600, 500); //플레이어 위치
        setImage(p2, "Images/Dog/Dog_Idle.png");

        p1.setOtherPlayer(p2);
        p2.setOtherPlayer(p1);

        add(p1.character);
        add(p2.character);
    }

    private void setImage(Player p, String path) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        p.character.setIcon(new ImageIcon(img));
        p.character.setSize(50, 50);
    }

    // 게임 정지 (Player 클래스에 stop 메서드 필요)
    public void stopGame() {
        if(p1 != null) p1.isDead = true;
        if(p2 != null) p2.isDead = true;
    }

    // 게임 리셋 (재시작)
    public void resetGame() {
        stopGame(); // 일단 멈추고
        createPlayers(); // 플레이어 다시 생성
        repaint();
        requestFocus();
        startGameLoop();
    }
    
    // 게임 오버 신호 보내기 (Player가 호출함)
    public void gameOver(String reason) {
        main.triggerGameOver(reason);
    }
    //성공 신호 보내기
    public void success() {
    	main.triggerSuccess();
    }

    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_W) p1.up();
                if (key == KeyEvent.VK_A) p1.left();
                if (key == KeyEvent.VK_D) p1.right();
                if (key == KeyEvent.VK_S) p1.down();
                
                if (key == KeyEvent.VK_UP)    p2.up();
                if (key == KeyEvent.VK_LEFT)  p2.left();
                if (key == KeyEvent.VK_RIGHT) p2.right();
                if (key == KeyEvent.VK_DOWN)  p2.down();
            }
            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_W) p1.up_released();
                if (key == KeyEvent.VK_A) p1.left_released();
                if (key == KeyEvent.VK_D) p1.right_released();
                if (key == KeyEvent.VK_S) p1.down_released();

                if (key == KeyEvent.VK_UP)    p2.up_released();
                if (key == KeyEvent.VK_LEFT)  p2.left_released();
                if (key == KeyEvent.VK_RIGHT) p2.right_released();
                if (key == KeyEvent.VK_DOWN)  p2.down_released();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        
        int[][] mapData = Collision.tileMap;
        int tileSize = Collision.TILE_SIZE;
        for (int row = 0; row < mapData.length; row++) {
            for (int col = 0; col < mapData[0].length; col++) {
                if (mapData[row][col] == 1) {
                    g.drawImage(wallImage, col * tileSize, row * tileSize, tileSize, tileSize, this);
                }
            }
        }
    }
}