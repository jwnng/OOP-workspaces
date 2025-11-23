import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainMap extends JPanel {
    // ... (변수 선언 등 위쪽 코드는 그대로 유지) ...
    Main main; 
    Player p1, p2;
    Timer gameLoop; 
    BgmLoop bgm; 

    Image wallImage, backgroundImage;
    Image trapGirl, trapDog; 
    Image switchOff, switchOnLeft, switchOnRight, doorImg;

    public MainMap(Main main) {
        this.main = main;
        initMap();
    }
    public MainMap() {
        initMap(); 
    }

    private void initMap() {
        int mapHeight = Collision.tileMap.length * Collision.TILE_SIZE;
        int mapWidth = Collision.tileMap[0].length * Collision.TILE_SIZE;
        setPreferredSize(new Dimension(mapWidth, mapHeight));
        setLayout(null);
        setBackground(Color.BLACK);

        // 1. 이미지 로드
        wallImage = new ImageIcon("Images/Tile/WoodTile1.png").getImage();
        backgroundImage = new ImageIcon("Images/Background/Background.jpg").getImage();
        trapGirl = new ImageIcon("Images/Tile/trap_fire.png").getImage();
        trapDog = new ImageIcon("Images/Tile/trap_water.png").getImage();
        switchOff = new ImageIcon("Images/Tile/Switch_off.png").getImage();       
        switchOnLeft = new ImageIcon("Images/Tile/Switch_onleft.png").getImage();     
        switchOnRight = new ImageIcon("Images/Tile/switch_onright.png").getImage();
        doorImg = new ImageIcon("Images/Tile/door.png").getImage();

        createPlayers(); // 여기서 위치 설정
        
        setFocusable(true);
        setupKeyListener();
        startGameLoop();
        
        bgm = new BgmLoop("sound/main_bgm.wav");
        bgm.start();
    }

    // ... (operateSwitch, startGameLoop, checkMeeting 등 중간 코드 유지) ...
    public void operateSwitch(int switchX, int switchY, int targetDoorType, int finalState) {
        Collision.tileMap[switchY][switchX] = finalState;
        for(int row=0; row < Collision.tileMap.length; row++) {
            for(int col=0; col < Collision.tileMap[0].length; col++) {
                if(Collision.tileMap[row][col] == targetDoorType) {
                    Collision.tileMap[row][col] = Collision.EMPTY; 
                }
            }
        }
        repaint(); 
    }
    
    private void startGameLoop() {
        if (gameLoop != null) gameLoop.stop(); 
        gameLoop = new Timer(30, e -> {
            if (p1 != null) p1.update();
            if (p2 != null) p2.update();
            checkMeeting(); 
            repaint();      
        });
        gameLoop.start();
    }

    private void checkMeeting() {
        if (p1 == null || p2 == null || p1.isDead || p2.isDead) return;
        Rectangle r1 = p1.character.getBounds();
        Rectangle r2 = p2.character.getBounds();
        if (r1.intersects(r2)) success(); 
    }

    // ⭐ [수정] 캐릭터 시작 위치 설정
    private void createPlayers() {
        if (p1 != null) remove(p1.character);
        if (p2 != null) remove(p2.character);

        // 1. 소녀 (왼쪽 상단)
        p1 = new Player(this, 64, 64, 1); 
        setImage(p1, "Images/Girls/Girl_Idle.png");

        // 2. 강아지 (오른쪽 하단)
        // 맵 크기가 30칸 x 20칸이므로, 대략 (28*32, 18*32) 위치
        p2 = new Player(this, 850, 550, 2); 
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

    // ... (나머지 하단 코드들 그대로 유지) ...
    public void stopGame() {
        if(p1 != null) p1.isDead = true;
        if(p2 != null) p2.isDead = true;
        if (bgm != null) bgm.stopMusic();
    }

    public void resetGame() {
        stopGame(); 
        createPlayers(); 
        repaint();
        requestFocus();
        startGameLoop();
        bgm = new BgmLoop("sound/main_bgm.wav");
        bgm.start();
    }
    
    public void gameOver(String reason) {
        if (bgm != null) bgm.stopMusic(); 
        if (main != null) main.triggerGameOver(reason);
    }
    
    public void success() {
        if (bgm != null) bgm.stopMusic(); 
        if (main != null) main.triggerSuccess();
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
        
        int[][] map = Collision.tileMap;
        int ts = Collision.TILE_SIZE;

        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                int tile = map[row][col];
                
                if (tile == Collision.WALL) g.drawImage(wallImage, col*ts, row*ts, ts, ts, this);
                else if (tile == Collision.PAD_GIRL) g.drawImage(trapGirl, col*ts, row*ts, ts, ts, this);
                else if (tile == Collision.PAD_DOG) g.drawImage(trapDog, col*ts, row*ts, ts, ts, this);
                
                else if (tile == Collision.SWITCH_RED || tile == Collision.SWITCH_BLUE) {
                    g.drawImage(switchOff, col*ts, row*ts, ts, ts, this);
                }
                else if (tile == Collision.SWITCH_ON_LEFT) { 
                    g.drawImage(switchOnLeft, col*ts, row*ts, ts, ts, this);
                }
                else if (tile == Collision.SWITCH_ON_RIGHT) { 
                    g.drawImage(switchOnRight, col*ts, row*ts, ts, ts, this);
                }
                
                else if (tile == Collision.DOOR_RED || tile == Collision.DOOR_BLUE) {
                    g.drawImage(doorImg, col*ts, row*ts, ts, ts, this);
                }
            }
        }
    }
}