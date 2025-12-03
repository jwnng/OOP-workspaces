public class Collision {
    public final static int TILE_SIZE = 32;

    // 🔢 타일 번호
    public final static int EMPTY = 0;       
    public final static int WALL = 1;        
    public final static int PAD_GIRL = 2;    // 소녀용 발판 (밟으면 리스폰)
    public final static int PAD_DOG = 3;     // 강아지용 발판 (밟으면 리스폰)
    public final static int SWITCH_GIRL = 4;  //스위치를 누르면 DOOR_GIRL이 열림
    public final static int SWITCH_DOG = 5; //스위치를 누르면 DOOR_DOG이 열림
    public final static int DOOR_GIRL = 6;  //SWITCH_GIRL   
    public final static int DOOR_DOG = 7;   //SWITCH_DOG  
    public final static int SWITCH_ON_LEFT = 8;   // 눌린 스위치(왼쪽)
    public final static int SWITCH_ON_RIGHT = 9;  // 눌린 스위치(오른쪽)
    public final static int SWITCH_GIRL1 = 10;
    public final static int SWITCH_DOG1 = 11;
    public final static int DOOR_GIRL1 = 12;
    public final static int DOOR_DOG1 = 13;
    
    public static int[][] tileMap;

    // 🗺️ 포레스트 템플 스테이지 1
    public static int[][] originalMap = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1},
        {1,1,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,0,0,1},
        {1,4,0,0,0,0,0,0,7,0,0,0,0,0,0,1,0,0,0,0,0,0,5,6,0,0,0,0,0,1},
        {1,1,1,1,2,2,2,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,1,3,3,3,1,1,1},
        {1,0,0,1,1,1,1,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,1,1,1,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1,2,2,2,1,3,3,3,1,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,1,0,0,0,0,0,0,0,0,13,0,0,0,0,12,0,0,1,3,3,1,0,0,0,1},
        {1,0,0,0,0,1,1,1,0,0,0,0,0,0,13,0,0,0,0,12,0,0,1,1,1,1,1,0,11,1},
        {1,1,0,0,0,0,1,1,1,0,0,0,0,0,1,0,0,0,0,1,1,1,1,0,0,0,1,1,1,1},
        {1,0,0,0,0,0,0,0,1,2,2,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,0,0,0,0,0,10,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1} 
    };

    public static void resetMap() {
        tileMap = new int[originalMap.length][originalMap[0].length];
        for(int i = 0; i < originalMap.length; i++) {
            System.arraycopy(originalMap[i], 0, tileMap[i], 0, originalMap[i].length);
        }
    }

    static {
        resetMap();
    }

    // ★ 추가: "진짜 벽 역할"만 골라서 충돌로 보는 함수
    public static boolean isSolidTile(int tile) {
        return tile == WALL 
            || tile == DOOR_GIRL || tile == DOOR_DOG
            || tile == DOOR_GIRL1 || tile == DOOR_DOG1;
        // PAD나 SWITCH는 여기 넣지 말 것
    }

    // ★ 추가: 사각형 전체 기준 충돌
    public static boolean isCollidingRect(int x, int y, int w, int h) {
        if (tileMap == null) resetMap();

        int left   = x / TILE_SIZE;
        int right  = (x + w - 1) / TILE_SIZE;
        int top    = y / TILE_SIZE;
        int bottom = (y + h - 1) / TILE_SIZE;

        for (int ty = top; ty <= bottom; ty++) {
            for (int tx = left; tx <= right; tx++) {
                // 맵 밖은 벽 취급
                if (ty < 0 || ty >= tileMap.length || tx < 0 || tx >= tileMap[0].length) {
                    return true;
                }
                int tile = tileMap[ty][tx];
                if (isSolidTile(tile)) return true;
            }
        }
        return false;
    }

    // ★ 수정: 예전 4 모서리 체크 대신, 사각형 전체 체크 사용
    public static boolean isColliding(int x, int y, int w, int h) {
        return isCollidingRect(x, y, w, h);
    }

    // (혹시 1픽셀 포인트로 쓰는 곳 있으면 유지)
    public static boolean isColliding(int x, int y) {
        if (tileMap == null) resetMap();
        int tx = x / TILE_SIZE;
        int ty = y / TILE_SIZE;
        if (ty < 0 || ty >= tileMap.length || tx < 0 || tx >= tileMap[0].length) return true;

        int tile = tileMap[ty][tx];
        return isSolidTile(tile);
    }
}
