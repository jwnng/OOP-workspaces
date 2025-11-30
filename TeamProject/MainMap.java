import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainMap extends JPanel {
    Main main; 
    Player p1, p2;
    Box pushBox; 
    Timer gameLoop; 
    BgmLoop bgm; 

    Image wallImage, backgroundImage;
    Image trapGirl, trapDog; 
    Image switchOff, switchOnLeft, switchOnRight, doorImg;
    Image breakableWall, switchGreen, switchGreenOn; 

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

        wallImage = new ImageIcon("Images/Tile/Wall.png").getImage();
        backgroundImage = new ImageIcon("Images/Background/Background.jpg").getImage();
        trapGirl = new ImageIcon("Images/Tile/trap_girl.png").getImage(); 
        trapDog = new ImageIcon("Images/Tile/trap_dog.png").getImage();
        
        switchOff = new ImageIcon("Images/Tile/Switch_off.png").getImage();       
        switchOnLeft = new ImageIcon("Images/Tile/Switch_onleft.png").getImage();     
        switchOnRight = new ImageIcon("Images/Tile/switch_onright.png").getImage();
        doorImg = new ImageIcon("Images/Tile/door.png").getImage();

        breakableWall = new ImageIcon("Images/Tile/Wall.png").getImage(); 
        switchGreen = new ImageIcon("Images/Tile/Switch_off.png").getImage(); 
        switchGreenOn = new ImageIcon("Images/Tile/Switch_onleft.png").getImage(); 

        createPlayers(); 
        
        pushBox = new Box(850, 400); 
        add(pushBox.boxLabel); 

        add(p1.character);
        add(p2.character);
        
        setFocusable(true);
        setupKeyListener();
        startGameLoop();
        
        bgm = new BgmLoop("sound/main_bgm.wav");
        bgm.start();
    }
    
    public Box getBox() { return pushBox; }

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

    private void createPlayers() {
        if (p1 != null) remove(p1.character);
        if (p2 != null) remove(p2.character);

        p1 = new Girl(this, null); 
        setImage(p1, "Images/Girls/Girl_Idle.png");

        p2 = new Dog(this, null); 
        p2.x = 850; p2.y = 550; 
        setImage(p2, "Images/Dog/Dog_Idle.png");

        p1.setOtherPlayer(p2);
        p2.setOtherPlayer(p1);
    }

    private void setImage(Player p, String path) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(p.width, p.height, Image.SCALE_SMOOTH);
        p.character.setIcon(new ImageIcon(img));
        p.character.setSize(p.width, p.height);
    }

    public void stopGame() {
        if(p1 != null) p1.isDead = true;
        if(p2 != null) p2.isDead = true;
        if (bgm != null) bgm.stopMusic();
    }

    public void resetGame() {
        stopGame(); 
        if(pushBox != null) { pushBox.x = 850; pushBox.y = 400; }
        createPlayers(); 
        add(p1.character); 
        add(p2.character);
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
                
                // ⭐ [수정 완료] SWITCH_GIRL, SWITCH_DOG 사용
                else if (tile == Collision.SWITCH_GIRL || tile == Collision.SWITCH_DOG) {
                    g.drawImage(switchOff, col*ts, row*ts, ts, ts, this);
                }
                else if (tile == Collision.SWITCH_ON_LEFT) g.drawImage(switchOnLeft, col*ts, row*ts, ts, ts, this);
                else if (tile == Collision.SWITCH_ON_RIGHT) g.drawImage(switchOnRight, col*ts, row*ts, ts, ts, this);
                
                // ⭐ [수정 완료] DOOR_GIRL, DOOR_DOG 사용
                else if (tile == Collision.DOOR_GIRL || tile == Collision.DOOR_DOG) {
                    g.drawImage(doorImg, col*ts, row*ts, ts, ts, this);
                }
                
                else if (tile == Collision.WALL_BREAKABLE) {
                    g.drawImage(breakableWall, col*ts, row*ts, ts, ts, this);         
                }
                else if (tile == Collision.SWITCH_GREEN) g.drawImage(switchGreen, col*ts, row*ts, ts, ts, this);
                else if (tile == Collision.SWITCH_GREEN_ON) g.drawImage(switchGreenOn, col*ts, row*ts, ts, ts, this);
            }
        }
    }
}