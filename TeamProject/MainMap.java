import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainMap extends JPanel {
    Main main; 
    Player p1, p2;
    Timer gameLoop; 
    BgmLoop bgm; 
    Box pushBox;

    Image wallImage, backgroundImage;
    Image trapGirl, trapDog; 
    Image switchOff, switchOnLeft, switchOnRight, doorImg;

    // Main에서 넘어올 때 쓰는 생성자
    public MainMap(Main main) {
        this.main = main;
        initMap();
        setupEscKeyBinding();  // ESC → 일시정지 메뉴
    }

    // 기본 생성자 (혹시 다른 데서 쓰면)
    public MainMap() {
        initMap();
    }

    // ============ 초기 맵 세팅 ============

    private void initMap() {
        int mapHeight = Collision.tileMap.length * Collision.TILE_SIZE;
        int mapWidth  = Collision.tileMap[0].length * Collision.TILE_SIZE;
        setPreferredSize(new Dimension(mapWidth, mapHeight));
        setLayout(null);
        setBackground(Color.BLACK);

        // 이미지 로드
        wallImage       = new ImageIcon("Images/Tile/Wall.png").getImage();
        backgroundImage = new ImageIcon("Images/Background/Background.jpg").getImage();
        trapGirl        = new ImageIcon("Images/Tile/WoodTile1.png").getImage();
        trapDog         = new ImageIcon("Images/Tile/GlassTile1.png").getImage();
        switchOff       = new ImageIcon("Images/Tile/Switch_off.png").getImage();       
        switchOnLeft    = new ImageIcon("Images/Tile/Switch_onleft.png").getImage();     
        switchOnRight   = new ImageIcon("Images/Tile/switch_onright.png").getImage();
        doorImg         = new ImageIcon("Images/Tile/door2.png").getImage();

        // 플레이어 생성
        createPlayers();

        // 밀 수 있는 상자
        pushBox = new Box(150, 512); 
        add(pushBox.boxLabel);
        
        setFocusable(true);
        setupKeyListener();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        // 게임 루프 시작
        startGameLoop();
    }

    // ============ 스위치 / 문 ============

    public void operateSwitch(int switchX, int switchY, int targetDoorType, int finalState) {
        Collision.tileMap[switchY][switchX] = finalState;
        for (int row = 0; row < Collision.tileMap.length; row++) {
            for (int col = 0; col < Collision.tileMap[0].length; col++) {
                if (Collision.tileMap[row][col] == targetDoorType) {
                    Collision.tileMap[row][col] = Collision.EMPTY; 
                }
            }
        }
        repaint(); 
    }
    
    // ============ 게임 루프 ============

    private void startGameLoop() {
        if (gameLoop != null) gameLoop.stop(); 

        gameLoop = new Timer(30, e -> {
            if (p1 != null) p1.update();
            if (p2 != null) p2.update();
            if (pushBox != null) pushBox.update();

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

    // ============ 플레이어 생성 ============

    private void createPlayers() {
        if (p1 != null) remove(p1.character);
        if (p2 != null) remove(p2.character);

        // 소녀 (왼쪽)
        p1 = new Girl(this, 45, 64, null);

        // 강아지 (오른쪽)
        p2 = new Dog(this, 750, 64, null);

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

    // ============ 일시정지 / 재개 / 리셋 / 종료 ============

    // ESC 일시정지용 – 잠깐 멈추기만 함
    public void pauseGame() {
        if (gameLoop != null && gameLoop.isRunning()) {
            gameLoop.stop();          // 타이머만 멈춤
        }
        // BGM은 그냥 계속 나오게 둔다 (원하면 나중에 볼륨 0으로 조절 가능)
    }

    // ESC → 계속하기
    public void resumeGame() {
        if (gameLoop != null && !gameLoop.isRunning()) {
            gameLoop.start();
        }
    }

    // 게임 오버/성공 등 진짜 끝낼 때
    public void stopGame() {
        if (gameLoop != null) gameLoop.stop();
        if (p1 != null) p1.isDead = true;
        if (p2 != null) p2.isDead = true;
        if (bgm != null) bgm.stopMusic();
    }

    // 다시 시작 (맵 리셋)
    public void resetGame() {
        Collision.resetMap(); 

        stopGame();        // 타이머 / BGM 정리
        createPlayers();   // 새 플레이어
        repaint();
        requestFocusInWindow();
        startGameLoop();   // 새 루프 시작

        //다시 시작 또는 처음 시작 때 불림
        startBgm();
    }
    
    public void startBgm() {
        if (bgm == null) {
            bgm = new BgmLoop("sound/main_bgm.wav");
            bgm.start();
        }
    }


    public Box getBox() { return pushBox; }

    public void gameOver(String reason) {
        stopGame();
        if (main != null) main.triggerGameOver(reason);
    }
    
    public void success() {
        stopGame();
        if (main != null) main.triggerSuccess();
    }

    // ============ 소리 조절 (일시정지 메뉴에서 호출) ============

    public void setBgmVolume(float v) {
        if (bgm != null) {
            bgm.setVolume(v);   // 0.0f ~ 1.0f
        }
    }

    // ============ 키 입력 (WASD + 방향키) ============

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

    // ESC → Main에게 “일시정지 메뉴 띄워줘” 요청
    private void setupEscKeyBinding() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "pauseGame");
        am.put("pauseGame", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (main != null) {
                    main.pauseGameAndShowMenu();
                }
            }
        });
    }

    // ============ 그리기 ============

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null)
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        
        int[][] map = Collision.tileMap;
        int ts = Collision.TILE_SIZE;

        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                int tile = map[row][col];
                
                if (tile == Collision.WALL)
                    g.drawImage(wallImage, col*ts, row*ts, ts, ts, this);
                else if (tile == Collision.PAD_GIRL)
                    g.drawImage(trapGirl, col*ts, row*ts, ts, ts, this);
                else if (tile == Collision.PAD_DOG)
                    g.drawImage(trapDog, col*ts, row*ts, ts, ts, this);
                else if (tile == Collision.SWITCH_GIRL 
                      || tile == Collision.SWITCH_DOG 
                      || tile == Collision.SWITCH_GIRL1
                      || tile == Collision.SWITCH_DOG1)
                    g.drawImage(switchOff, col*ts, row*ts, ts, ts, this);
                else if (tile == Collision.SWITCH_ON_LEFT)
                    g.drawImage(switchOnLeft, col*ts, row*ts, ts, ts, this);
                else if (tile == Collision.SWITCH_ON_RIGHT)
                    g.drawImage(switchOnRight, col*ts, row*ts, ts, ts, this);
                else if (tile == Collision.DOOR_GIRL 
                      || tile == Collision.DOOR_DOG 
                      || tile == Collision.DOOR_GIRL1 
                      || tile == Collision.DOOR_DOG1)
                    g.drawImage(doorImg, col*ts, row*ts, ts, ts, this);
            }
        }
    }
}

