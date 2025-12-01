public class Collision {
    public final static int TILE_SIZE = 32;

    // 🔢 타일 번호
    public final static int EMPTY = 0;       
    public final static int WALL = 1;        
    public final static int PAD_GIRL = 2;    // 소녀용 발판 (밟으면 리스폰)
    public final static int PAD_DOG = 3;     // 강아지용 발판 (밟으면 리스폰)
    public final static int SWITCH_GIRL = 4;  //스위치를 누르면 DOOR_GIRL이 열림
    public final static int SWITCH_DOG = 5; //스위치를 누르면 DOOR_DOG이 열림
    public final static int DOOR_GIRL = 6; //SWICH_GIRL   
    public final static int DOOR_DOG = 7; //SWICH_DOG  
    public final static int SWITCH_ON_LEFT = 8;  //스위치가 눌렸을 때 그림을 눌린 스위치 이미지로 바꾸기 위해 존재하는 번호
    public final static int SWITCH_ON_RIGHT = 9; //MainMap의 paintComponent 함수는 tileMap의 숫자를 하나씩 꺼내서, 이 변수들(WALL, DOOR_RED 등)과 비교한 뒤 맞는 그림을 화면에 찍어냅니다.
    public final static int SWITCH_GIRL1 = 10;
    public final static int SWITCH_DOG1= 11;
    public final static int DOOR_GIRL1 = 12;
    public final static int DOOR_DOG1 = 13;
    
    public static int[][] tileMap;
    // 🗺️ 포레스트 템플 스테이지 1 (이미지 기반 구현)
    public static int[][] originalMap = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1}, // 🚪 문 (우측 상단)
        {1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1}, // 2층 출구 발판
        {1,1,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,0,0,1},
        {1,4,0,0,0,0,0,0,7,0,0,0,0,0,0,1,0,0,0,0,0,0,5,6,0,0,0,0,0,1},
        {1,1,1,1,2,2,2,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,1,3,3,3,1,1,1}, // 🔘 스위치들
        {1,0,0,1,1,1,1,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,1,1,1,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1,2,2,2,1,3,3,3,1,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,1}, // 🔥2층 함정
        {1,0,0,0,0,1,0,0,0,0,0,0,0,0,13,0,0,0,0,12,0,0,1,3,3,1,0,0,0,1},
        {1,0,0,0,0,1,1,1,0,0,0,0,0,0,13,0,0,0,0,12,0,0,1,1,1,1,1,0,11,1},//<-강아지 스위치
        {1,1,0,0,0,0,1,1,1,0,0,0,0,0,1,0,0,0,0,1,1,1,1,0,0,0,1,1,1,1},
        {1,0,0,0,0,0,0,0,1,2,2,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,0,0,0,0,0,10,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1}, // ☠️ 바닥 함정 (독 대신 섞어둠)
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1}, // 🐶 강아지 시작
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1} 
    };

    public static void resetMap() {
        // 배열의 크기만큼 새로 공간을 만들고
        tileMap = new int[originalMap.length][originalMap[0].length];
        
        // 원본 데이터를 하나하나 복사합니다 
        for(int i=0; i<originalMap.length; i++) {
            System.arraycopy(originalMap[i], 0, tileMap[i], 0, originalMap[i].length);
        }
    }

    // 프로그램 처음 켜질 때 자동으로 한 번 실행
    static {
        resetMap();
    }
    public static boolean isColliding(int x, int y, int w, int h) {
        return isColliding(x, y) || isColliding(x + w, y) || isColliding(x, y + h) || isColliding(x + w, y + h);
    }

    public static boolean isColliding(int x, int y) {
    	if (tileMap == null) resetMap(); // 안전장치
        int tx = x / TILE_SIZE;
        int ty = y / TILE_SIZE;
        if (ty < 0 || ty >= tileMap.length || tx < 0 || tx >= tileMap[0].length) return true;
        
        int tile = tileMap[ty][tx];
        return tile == WALL || tile == DOOR_GIRL || tile == DOOR_DOG || tile == DOOR_GIRL1 || tile == DOOR_DOG1;
    }
}