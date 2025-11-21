public interface Moveable {
    // --- 이동 관련 (키 눌렀을 때) ---
    
    // 위로 가는 기능
    public abstract void up();
    
    // 아래로 가는 기능
    public abstract void down();
    
    // 왼쪽으로 가는 기능
    public abstract void left();
    
    // 오른쪽으로 가는 기능
    public abstract void right();

    // --- 멈춤 관련 (키 뗐을 때) - 여기가 부족했었습니다! ---

    // 왼쪽 키에서 손을 뗐을 때 멈추는 기능
    public abstract void left_released();
    
    // 오른쪽 키에서 손을 뗐을 때 멈추는 기능
    public abstract void right_released();
    
    // 위쪽 키에서 손을 뗐을 때 멈추는 기능 (추가됨)
    public abstract void up_released();
    
    // 아래쪽 키에서 손을 뗐을 때 멈추는 기능 (추가됨)
    public abstract void down_released();

    // --- 기타 상태 ---

    // 가만히 서 있는 기능
    public abstract void idle(); 
    
    // 죽는 기능
    public abstract void dead();
    
    // 애니메이션 순서를 초기화하는 기능
    public abstract void initIndex();
}