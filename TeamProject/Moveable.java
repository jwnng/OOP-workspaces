public interface Moveable {
	 	//위로 가는 기능
		public abstract void up();
		//아래로 가는 기능
	    public abstract void down();
	    // 왼쪽으로 가는 기능
	    public abstract void left();
	   // 오른쪽으로 가는 기능을 꼭 만들어라
	    public abstract void right();
	    //가만히 서 있는 기능을 꼭 만들어라
	    public abstract void idle(); 
	    // 죽는 기능을 꼭 만들어라
	    public abstract void dead();
	    //애니메이션 순서를 초기화하는 기능
	    public abstract void initIndex();
	    //왼쪽 키에서 손을 뗐을 때 멈추는 기능
	    public abstract void left_released();
	    //오른쪽 키에서 손을 뗐을 때 멈추는 기능
	    public abstract void right_released();
	}
