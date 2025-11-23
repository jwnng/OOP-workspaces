import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class MainMap extends JPanel {
    Main main; // 메인 프레임 참조
    Player p1, p2;
    Image wallImage, backgroundImage;

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
    }

    private void createPlayers() {
        // 기존 플레이어가 있다면 제거 (재시작 시)
        if (p1 != null) remove(p1.character);
        if (p2 != null) remove(p2.character);

        p1 = new Player(this, 100, 100);
        setImage(p1, "Images/Girls/Girl_Idle.png");

        p2 = new Player(this, 200, 100);
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