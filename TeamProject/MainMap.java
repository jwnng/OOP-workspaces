import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Dimension; // 이거 import 필요

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMap extends JPanel {

    // 주인공 두 명 선언
    Player p1; // 왼쪽 친구 (WASD 키)
    Player p2; // 오른쪽 친구 (방향키)
    Image wallImage; // 🧱 벽돌 이미지 변수 추가
    Image backgroundImage; // 배경 이미지 (맵)

    public MainMap() {
        // 1. 무대 설정
        setLayout(null); // "내 마음대로 위치를 잡을 거야!" (자동 정렬 끄기)
        setBackground(Color.BLACK); // 배경은 일단 검은색

        // 1. 맵 데이터 크기 계산
        int mapHeight = Collision.tileMap.length * Collision.TILE_SIZE;     // 세로 칸 수 * 32
        int mapWidth = Collision.tileMap[0].length * Collision.TILE_SIZE;   // 가로 칸 수 * 32

        // 2. 패널의 크기를 맵 크기에 딱 맞춤!
        setPreferredSize(new Dimension(mapWidth, mapHeight)); 

        wallImage = new ImageIcon("Images/Tile/WoodTile1.png").getImage();
        backgroundImage = new ImageIcon("Images/Background/Background.jpg").getImage();
        
        // 2. 플레이어 1 (왼쪽 친구) 만들기
        p1 = new Player(this, 100, 500); 
        // 🖼️ 이미지 크기 줄이기 (리사이징)
        ImageIcon p1Icon = new ImageIcon("Images/Girls/Girl_Idle.png");
        // 원본 이미지를 가져와서 50x50 크기로 부드럽게 줄임
        Image p1Resized = p1Icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        // 줄어든 이미지를 캐릭터에게 입힘
        p1.character.setIcon(new ImageIcon(p1Resized));

        // 3. 플레이어 2 (오른쪽 친구) 만들기
        p2 = new Player(this, 500, 500);
        // p2에게 이미지 입히기
        p2.character.setIcon(new ImageIcon("Images/Dog/Dog_Idle.png")); 
        // 🖼️ 이미지 크기 줄이기 (리사이징)
        // 주의: 절대 경로(/Users/...) 대신 상대 경로("Images/...") 사용!
        ImageIcon p2Icon = new ImageIcon("Images/Dog/Dog_Idle.png"); 
        Image p2Resized = p2Icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        p2.character.setIcon(new ImageIcon(p2Resized));
        
        // 4. 서로 친구라고 소개시켜주기 (만나면 게임 끝내야 하니까)
        p1.setOtherPlayer(p2);
        p2.setOtherPlayer(p1);

        // 5. 무대에 배우들 올리기 (add)
        // 주의! 배경보다 캐릭터를 나중에 붙어야 캐릭터가 위에 보여.
        add(p1.character);
        add(p2.character);

        // 6. 지휘자(키보드 관리) 설정
        setFocusable(true); // "나 이제 키보드 입력 받을게!" (이거 중요!)
        requestFocus();     // "포커스(주목) 나한테 줘!"
        
        // 키보드 감시자 붙이기
        addKeyListener(new KeyAdapter() {
            
            // 키를 눌렀을 때 ("움직여!")
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                // --- 🟥 P1 (WASD) ---
                if (key == KeyEvent.VK_W) p1.up();
                if (key == KeyEvent.VK_A) p1.left();
                if (key == KeyEvent.VK_D) p1.right();
                if (key == KeyEvent.VK_S) p1.down();
                
                // --- 🟦 P2 (방향키) ---
                if (key == KeyEvent.VK_UP)    p2.up();
                if (key == KeyEvent.VK_LEFT)  p2.left();
                if (key == KeyEvent.VK_RIGHT) p2.right();
                if (key == KeyEvent.VK_DOWN)  p2.down();
            }

            // 키를 뗐을 때 ("멈춰!")
            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();

                // --- 🟥 P1 멈춤 ---
                if (key == KeyEvent.VK_W) p1.up_released();
                if (key == KeyEvent.VK_A) p1.left_released();
                if (key == KeyEvent.VK_D) p1.right_released();
                if (key == KeyEvent.VK_S) p1.down_released();

                // --- 🟦 P2 멈춤 ---
                if (key == KeyEvent.VK_UP)    p2.up_released();
                if (key == KeyEvent.VK_LEFT)  p2.left_released();
                if (key == KeyEvent.VK_RIGHT) p2.right_released();
                if (key == KeyEvent.VK_DOWN)  p2.down_released();
            }
        });
    }
    
    // (옵션) 배경 그림 그리기 기능
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 배경 이미지가 있으면 그리기
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // 2. 맵(벽돌) 그리기
        // Collision 클래스의 지도를 가져와서 반복문 돌리기
        int[][] mapData = Collision.tileMap; 
        int tileSize = Collision.TILE_SIZE; // 32

        for (int row = 0; row < mapData.length; row++) {
            for (int col = 0; col < mapData[0].length; col++) {
                
                // 만약 지도가 '1' (벽) 이라면?
                if (mapData[row][col] == 1) {
                    // 해당 위치(col * 32, row * 32)에 벽돌 이미지를 그린다!
                    g.drawImage(wallImage, col * tileSize, row * tileSize, tileSize, tileSize, this);
                }
                // 만약 '2' (함정) 같은 게 있다면 else if로 추가 가능
            }
        }
    }
}