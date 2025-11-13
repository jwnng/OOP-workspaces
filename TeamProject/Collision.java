// Collision.java
public class Collision {

    // 플레이어가 벽(또는 장애물)에 닿았는지 검사하는 함수
    // playerX, playerY : 플레이어 좌표
    // tileMap          : 2D 배열 형태의 맵 (벽=1, 빈공간=0)
    // TILE_SIZE        : 한 칸의 픽셀 크기
    public static boolean checkWallCollision(int playerX, int playerY, int[][] tileMap, int TILE_SIZE) {
        int tileX = playerX / TILE_SIZE;
        int tileY = playerY / TILE_SIZE;

        // 맵 범위 체크
        if (tileX < 0 || tileY < 0 || tileY >= tileMap.length || tileX >= tileMap[0].length)
            return true; // 화면 밖은 벽으로 취급

        // 1 = 벽, 0 = 빈 공간
        return tileMap[tileY][tileX] == 1;
    }
}

