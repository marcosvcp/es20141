package control;

import com.badlogic.gdx.math.Rectangle;

public class DropObject extends Rectangle {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9201295632803242589L;

	private Droppable droppable;

	public DropObject(Droppable objectDroppable) {
		this.setDroppable(objectDroppable);
	}

	public Droppable getDroppable() {
		return droppable;
	}

	public void setDroppable(Droppable objectDroppable) {
		this.droppable = objectDroppable;
	}
	
	public void playSound(){
		droppable.getDropSound().play();
	}
}
