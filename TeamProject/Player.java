import project;
import java.awt.Point;
import java.awt.Rectangle;//충돌 확인
import java.util.ArrayList;//여러개의 이미지를 담음
import java.util.List;       // 리스트(목록) 도구
import javax.swing.ImageIcon; // 이미지 아이콘(파일에서 그림 불러오기)
import javax.swing.JLabel;    // 화면에 그림을 붙일 스티커 같은 것
import javax.swing.JPanel;    // 그림이 붙을 도화지(패널)
import javax.swing.Timer;     // 시간을 재주는 타이머
// 주인공 캐릭터의 이미지(사진 에셋) 데이터를 저장하고 다루기 위해 필요합니다.



import java.awt.event.KeyEvent;
// KeyEvent 클래스를 가져옵니다. 
// 키보드 입력(예: 방향키)을 감지하고, 해당 키보드 입력의 고유한 정수 코드(KeyCode)를 사용하기 위해 필요합니다.

public class Player implements Moveable{ // 주인공(플레이어) 객체의 상태와 행동을 정의하는 클래스
	
	int x,y;
	int width =50, height =50;
	public static final String LEFT_MOVE = "LEFT_MOVE";   // 왼쪽 이동
    public static final String RIGHT_MOVE = "RIGHT_MOVE"; // 오른쪽 이동
	
    //여자 아이의 기본 이미지
    public static final String basePath = "C:\\Users\\user\\eclipse-workspace\\OOP-workspaces\\TeamProject\\Images\\Girls\\Girl_Idle.png";
    Point position;      // 내 캐릭터가 지금 어디 있는지 (X, Y 좌표)
    Direction direction; // 내가 지금 어디를 보고 있는지 (왼쪽? 오른쪽?)
    
    private MainMap mainMap;   // 우리가 뛰어놀 맵 정보
    private OptionPane o_pane; // 게임 옵션 창 (체력 같은 거 표시)
    private JPanel m_map;      // 실제 맵이 그려지는 도화지
    boolean left;    // 왼쪽
    boolean right;   // 오른쪽
    boolean up;      // 점프
    boolean down;    // 떨어지는가
    boolean idle;    // 가만히 서 있니?
    boolean isDead;  // 죽었는지 안죽었는지
    boolean loop;    //게임이 끝났나
    
    private final int SPEED = 4;   // 달리기 속도 (숫자가 클수록 빨라)
    private final int JUMPSPEED = 2; // 점프하는 힘
    private JLabel character; // 내 캐릭터를 화면에 보여줄 스티커
   
    private ArrayList<ImageIcon> i_rightMove; // 오른쪽으로 걷는 사진들
    private ArrayList<ImageIcon> i_leftMove;  // 왼쪽으로 걷는 사진들

    private int rightMoveIndex = 0; //몇번째인지 확인
    private int leftMoveIndex = 0; 
    

    private boolean isActive = false; // 뭔가 작동 중인가?
    private boolean onGround = false; // 땅에 발이 닿아 있나?

    // 맵과 화면 크기 설정
    int panelWidth = 715;  // 내 컴퓨터 화면 창의 가로 크기
    int mapWidth = 1800;   // 전체 맵의 진짜 가로 크기 (엄청 길어!)
    int screenCenterX = panelWidth / 2; // 화면의 정가운데 좌표
    int mapX = 0; // 맵이 현재 얼마나 옆으로 밀렸는지 나타내는 좌표
    
 
    public Player(JPanel m_map, OptionPane o_pane) {
        this.position = new Point(10, 400); // 곰은 (10, 400) 위치에서 태어날 거야.
        character = new JLabel(); // 곰 스티커를 하나 꺼냄
        character.setBounds(position.x, position.y, 85, 93); // 곰의 위치와 크기(가로 85, 세로 93) 설정
        
        loadMoveImage(); // "준비된 그림들을 사진첩에 채워 넣으세요!"
        
        // 처음엔 아무것도 안 하고 있으니까 모두 거짓(false)으로 설정
        left = false; right = false; up = false; down = false; 
        idle = false; loop = false;
        
        direction = Direction.RIGHT; // 처음엔 오른쪽을 바라보자.
        mainMap = new MainMap();     // 맵 정보를 가져옴
        this.m_map = m_map;          // 도화지 연결
        this.o_pane = o_pane;        // 옵션 창 연결
        heart = 3;                   // 목숨은 3개!
        
        idle();      // "자, 일단 숨 고르고 서 있어봐." (기본 동작 시작)
        gameLoop();  // "이제 물리 법칙(중력 등)을 시작해!"
    }

    // ... (중간 생략: 이미지를 불러오는 loadMoveImage 부분은 파일 하나하나 읽어서 리스트에 넣는 단순 노동이야) ...

    // --- 캐릭터 이동 검사 (가장 중요한 부분!) ---
    private boolean moveCharacter(int deltaX) {
        int nextX = position.x + deltaX; // "내가 만약 옆으로 조금 움직인다면..." (예상 위치)
        List<Rectangle> platforms = mainMap.getPlatforms(); // 맵에 있는 모든 발판 정보를 가져와.
        
        // 모든 발판을 하나씩 확인해 보자.
        for (Rectangle platform : platforms) {
            // "내가 움직일 예상 위치에 발판이 있어서 서로 겹치나요?" (부딪혔니?)
            if (new Rectangle(nextX, position.y, 85, 93).intersects(platform)) {
                // "어, 부딪혔어! 더 이상 못 가."
                return false; // 이동 실패!
            }
        }
        // "부딪힌 게 없네? 그럼 가도 좋아."
        return true; // 이동 성공!
    }

    // --- 맵 움직이기 (카메라 효과) ---
    private void updateMap() {
        int characterX = position.x; // 곰의 현재 위치
        
        // 곰이 화면 가운데보다 오른쪽으로 가고, 맵 끝이 아니면?
        if (characterX > screenCenterX && mapX > -(mapWidth - panelWidth) && mapX > -(mapWidth - screenCenterX - 20)) {
            screenCenterX += SPEED; // 화면 중심점도 이동
            mapX -= SPEED;  // 배경 맵을 왼쪽으로 밀어버려 (곰이 오른쪽으로 가는 것처럼 보임)
        } 
        // 반대로 곰이 왼쪽으로 가면?
        else if (characterX < screenCenterX && mapX < 0) {
            mapX += SPEED;  // 배경 맵을 오른쪽으로 밀어버려
            screenCenterX -= SPEED;
        }

        // 계산된 위치로 맵을 진짜로 이동시킴!
        m_map.setLocation(mapX, 0);
    }
    
    // --- 점프하기 (위로 가기) ---
    @Override
    public void up() {
        // 이미 점프 중이거나 떨어지는 중이 아닐 때만 점프 가능!
        if(!up && !down) {
            onGround = false; // 땅에서 발 뗐다!
            up = true;        // 점프 상태 켜짐
            
            // 별도의 일꾼(스레드)을 고용해서 점프를 처리해 (메인 화면이 멈추지 않게)
            new Thread(() -> {
                for (int i = 0; i < 120; i++) { // 120번 반복하며 조금씩 위로 올라감
                    if (!upCharacter()) { // 위로 올라갈 수 있으면 계속 올라가고, 천장에 닿으면 멈춰
                        // 점프하는 그림으로 바꿔주기
                        character.setIcon(direction == Direction.RIGHT ? i_rightJump.get(0) : i_leftJump.get(0));
                        updateCharacterPosition(); // 화면상 위치 갱신
                    } else 
                        break; // 천장에 닿았으면 반복문 탈출!
                    
                    try {
                        Thread.sleep(5); // 0.005초 쉼 (너무 순식간에 올라가면 안 되니까)
                    } catch (Exception e) { }
                }
                up = false; // 점프 끝!
                down(); // "자, 올라갔으니 이제 떨어져야지?" (하강 함수 호출)
            }).start(); // 일꾼 출발!
        }
    }

    // --- 왼쪽으로 달리기 ---
    @Override
    public void left() {
        if (!left) { // 이미 왼쪽으로 가는 중이 아니면 실행
            left = true; // 왼쪽 이동 스위치 켜기
            direction = Direction.LEFT; // 왼쪽 바라보기
            
            // 달리기도 별도의 일꾼(스레드)이 담당해
            new Thread(() -> {
                while (left) { // 왼쪽 키를 누르고 있는 동안 계속 반복
                    gameLoop(); // 중력 체크
                    position.x = position.x - SPEED; // 좌표를 왼쪽으로 이동
                    
                    // 벽에 부딪혔는지 확인
                    if (!moveCharacter(-SPEED)) { 
                        position.x = position.x + SPEED; // 부딪혔으면 다시 원위치 (못 가게)
                    } else {
                        updateMap(); // 안 부딪혔으면 맵(배경) 업데이트
                    }
                    updateCharacterPosition(); // 곰 위치 업데이트
                    
                    // 점프 중이 아니면 달리는 그림 보여주기 (플립북 넘기기)
                    if (!up && !down) {
                        leftMoveIndex = (leftMoveIndex + 1) % i_leftMove.size(); // 다음 사진 번호
                        character.setIcon(i_leftMove.get(leftMoveIndex)); // 사진 갈아끼우기
                    }
                    try {
                        Thread.sleep(30); // 0.03초마다 한 발자국씩
                    } catch (Exception e) { }
                }
                left = false; // 멈추면 스위치 끄기
            }).start();
        }
    }

    // ... (오른쪽 이동은 왼쪽과 방향만 반대고 똑같음) ...

    // --- 죽었을 때 ---
    @Override
    public void dead() {
        if (isDead) return; // 이미 죽어있으면 또 죽지 않음
        isDead = true; // "으악 죽었다!" 표시
        heart -= 1;    // 목숨 1개 감소
        o_pane.updateHeart(하트); // 화면 위 하트 그림도 줄여줘
        
        // 죽는 연출 시작 (유령이 되어 날아가는 것 같은 효과)
        new Thread(() -> {
            // ... (죽는 이미지 애니메이션을 보여주면서 위로 살짝 떴다가 아래로 푹 떨어지는 코드) ...
            
            // 목숨이 다 떨어졌으면 여기서 끝!
            if (heart == 0) {
                return;
            }
            
            // 아직 목숨 남았으면 부활!
            position.x = 10; // 처음 위치로 강제 이동
            position.y = 400;
            updateCharacterPosition();
            // 모든 상태 초기화 (새로 태어난 것처럼)
            left = false; right = false; up = false; down = false; isDead = false;
            idle(); // 다시 숨 고르기
            
            // 맵도 처음 위치로 되돌리기
            mapX = 0;
            m_map.setLocation(mapX, 0);
        }).start(); 
    }
}
}
