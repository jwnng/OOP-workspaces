
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Player implements Moveable {

    int x, y;           // 내 위치
    int width = 50, height = 50; // 내 크기
    
    double xSpeed = 0;  // 옆으로 가는 속도
    double ySpeed = 0;  // 위아래로 가는 속도

    // 2. 상태 체크
    boolean left, right, up, down; // 키보드 눌렸니?
    boolean onGround = false;      // 땅에 발이 닿았니?
    boolean isDead = false;        // 죽었니?

    // 3. 게임 설정값
    final double GRAVITY = 0.5;    // 중력 (계속 아래로 당기는 힘)
    final double JUMP_POWER = -12; // 점프하는 힘 (위로 솟구침)
    final double RUN_SPEED = 5;    // 달리기 속도

    JLabel character;
    MainMap mainMap; // 맵 정보(발판, 함정 등)
    Player otherPlayer; //다른 플레이어

    public Player(JPanel m_map,int startX, int startY) {
        this.x = startX; // 시작 위치
        this.y = startY;
        
        character = new JLabel();
        character.setBounds(x, y, width, height);
        mainMap = new MainMap(); //맵 가져오기
        startPhysicsLoop();
    }
    
    // 나중에 다른 플레이어가 접속하면 이 함수로 알려줘
    public void setOtherPlayer(Player p) {
        this.otherPlayer = p;
    }

    //게임 속 세상을 움직이는 함수 
    private void startPhysicsLoop() {
        new Thread(() -> {
            while (!isDead) { // 죽지 않았으면 계속 반복
                
                //좌우 움직임 계산
                if (left) xSpeed = -RUN_SPEED; // 왼쪽으로 가라
                else if (right) xSpeed = RUN_SPEED; // 오른쪽으로 가라
                else xSpeed = 0; // 키 안 누르면 멈춤

                //점프 계산
                if (up && onGround) { // 땅에 있을 때만 점프 가능!
                    ySpeed = JUMP_POWER; // 위로 슝!
                    onGround = false;    // 땅에서 발 떨어짐
                }

                //중력 적용 (항상 아래로 당김)
                ySpeed = ySpeed + GRAVITY; 

                //실제로 위치 옮기기 & 충돌 검사
                moveAndCheckCollision();

                //화면에 그림 다시 그리기
                character.setLocation(x, y);

                try { Thread.sleep(20); } catch (Exception e) {} // 0.02초마다 반복
            }
        }).start();
    }

    //충돌 검사
    private void moveAndCheckCollision() {
        
        // [가로 이동] 벽인지 확인
        x += xSpeed; 
        Rectangle myRectH = new Rectangle(x, y, width, height);
        
        for (Rectangle wall : mainMap.getWalls()) {
            if (myRectH.intersects(wall)) { // 벽에 부딪히면
                if (xSpeed > 0) x = wall.x - width; // 오른쪽 벽이면 바로 옆에 멈춤
                else if (xSpeed < 0) x = wall.x + wall.width; // 왼쪽 벽이면 바로 옆에 멈춤
                xSpeed = 0;
            }
        }

        // [세로 이동] 위아래로 움직여보고 땅/천장인지 확인
        y += ySpeed;
        Rectangle myRectV = new Rectangle(x, y, width, height);
        onGround = false; // 일단 공중에 있다고 가정
        for (Rectangle wall : mainMap.getWalls()) {
            if (myRectV.intersects(wall)) {
                if (ySpeed > 0) { // 아래로 떨어지다가 땅에 닿음
                    y = wall.y - height; // 땅 바로 위에 착지!
                    onGround = true;     // "나 땅 밟았다!"
                    ySpeed = 0;          // 떨어지는 속도 0
                } else if (ySpeed < 0) { // 위로 점프하다가 천장에 쿵!
                    y = wall.y + wall.height;
                    ySpeed = 0;
                }
            }
        }

        // 함정 밟으면 죽는 기능
        Rectangle me = new Rectangle(x, y, width, height);
        for (Rectangle trap : mainMap.getTraps()) { // 함정 리스트 가져오기
            if (me.intersects(trap))dead();  
        }
      //친구 만나면 게임 종료 기능
        if (otherPlayer != null) {
            //나와 친구가 만나면 게임 종료
            Rectangle friend = new Rectangle(otherPlayer.x, otherPlayer.y, width, height);
            if (me.intersects(friend)) {
                System.out.println("게임 클리어!");
                System.exit(0); //게임 종료
            }
        }
    }

    //키보드 입력 
    @Override public void left() { left = true; }
    @Override public void right() { right = true; }
    @Override public void up() { up = true; } // 점프 키 누름
    @Override public void down() { down = true; }

    @Override public void left_released() { left = false; }
    @Override public void right_released() { right = false; }
    public void up_released() { up = false; } // 점프 키 뗌
    
    @Override
    public void dead() {
        isDead = true;
        System.out.println("함정에 빠져서 녹아버렸어... ㅠㅠ");
        // 여기에 게임 오버 창 띄우기
    }

    // 안 쓰는 기능들 (Moveable 규칙 때문에 빈 칸으로 둠)
    @Override public void idle() {}
    @Override public void initIndex() {}
}