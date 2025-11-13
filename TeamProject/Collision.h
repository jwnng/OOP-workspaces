#pragma once
#include <vector>
#include <cmath>
#include <algorithm>
#include <SFML/Graphics.hpp>

// 기존 프로젝트에서 타일 크기 80, 오프셋 40을 자주 쓰므로 상수화
constexpr int TILE = 80;
constexpr int OFFSET = 40;

// 레벨 맵 접근용 시그니처 가정:
// extern const int H, W;               // Level 높이, 너비
// extern std::vector<std::vector<std::string>> levelsMap; // [level][row][col]

// 현재 문자 맵에서 "벽/바닥"으로 간주할 타일 정의
inline bool isSolid(char c) {
    // 돌 블록/좌/우, 버튼이나 문은 상황 따라 달라질 수 있지만
    // "통과 불가한 벽"만 우선 포함
    switch (c) {
        case 'M': // Stone Mid
        case 'L': // Stone Left
        case 'R': // Stone Right
            return true;
        default:
            return false;
    }
}

// 화면 좌표(px) → 타일 좌표(index)
inline int worldToTile(float pos) {
    // 본 프로젝트는 각 타일의 왼쪽위 좌표가 (j*TILE+OFFSET, i*TILE+OFFSET) 패턴
    // OFFSET 보정 포함
    return static_cast<int>(std::floor((pos - OFFSET) / TILE));
}

// 타일 좌표(index) → 화면 좌표(px)
inline float tileToWorld(int tileIndex) {
    return tileIndex * TILE + OFFSET;
}

// 플레이어 스프라이트의 AABB(경계 박스) 계산
inline sf::FloatRect getAABB(const sf::Sprite& spr) {
    // 스프라이트의 글로벌 바운딩박스 사용
    // (스케일/회전 없다는 가정. 회전이 있다면 별도 처리 필요)
    return spr.getGlobalBounds();
}

// 특정 축(수평/수직)으로만 이동하며 충돌 해결
inline void moveAndCollideAxis(sf::Sprite& spr, float& vx, float& vy, int levelIndex,
                               bool horizontal) {
    sf::FloatRect box = getAABB(spr);

    // 이동량 적용(가상)
    float dx = horizontal ? vx : 0.f;
    float dy = horizontal ? 0.f : vy;

    sf::FloatRect next = box;
    next.left += dx;
    next.top  += dy;

    // AABB가 겹칠 가능성이 있는 타일 범위만 검사 (주변 타일만 스캔)
    int topTile    = std::max(0, worldToTile(next.top));
    int bottomTile = std::min(H-1, worldToTile(next.top + next.height - 1));
    int leftTile   = std::max(0, worldToTile(next.left));
    int rightTile  = std::min(W-1, worldToTile(next.left + next.width - 1));

    bool collided = false;

    for (int i = topTile; i <= bottomTile; ++i) {
        for (int j = leftTile; j <= rightTile; ++j) {
            char tile = levelsMap[levelIndex][i][j];
            if (!isSolid(tile)) continue;

            // 타일의 월드 좌표 박스
            sf::FloatRect tileBox(
                tileToWorld(j),
                tileToWorld(i) - (i == 8 ? OFFSET : 0), // 기존 코드에 i==8 보정이 있어 반영
                static_cast<float>(TILE),
                static_cast<float>(TILE)
            );

            if (next.intersects(tileBox)) {
                collided = true;

                if (horizontal) {
                    // 오른쪽으로 가는 중이면 타일 왼쪽면에 붙이고, 속도 0
                    if (vx > 0) {
                        next.left = tileBox.left - next.width;
                    } else if (vx < 0) {
                        next.left = tileBox.left + tileBox.width;
                    }
                    vx = 0.f;
                } else {
                    // 아래로 낙하 중이면 타일 위에 얹고, 위로 점프 중이면 타일 아래에 밀착
                    if (vy > 0) {
                        next.top = tileBox.top - next.height;
                    } else if (vy < 0) {
                        next.top = tileBox.top + tileBox.height;
                    }
                    vy = 0.f;
                }
            }
        }
    }

    if (collided) {
        // 충돌로 조정된 좌표를 실제 스프라이트에 반영
        // (글로벌 바운즈 기준이므로 setPosition은 좌상단 기준)
        spr.setPosition(next.left, next.top);
    } else {
        // 충돌 없으면 원래 이동
        spr.move(dx, dy);
    }
}

// 통합: 수평→수직 순으로 이동 및 충돌 해결(틸팅 최소화)
inline void moveAndCollide(sf::Sprite& spr, float& vx, float& vy, int levelIndex) {
    moveAndCollideAxis(spr, vx, vy, levelIndex, /*horizontal=*/true);
    moveAndCollideAxis(spr, vx, vy, levelIndex, /*horizontal=*/false);
}
