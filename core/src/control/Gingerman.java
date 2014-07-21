package control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Game;

public class Gingerman extends Rectangle {
	private Texture gingermanImage;
	private String[] imageNames = new String[5];
	private int life;
	/**
	 * 
	 */
	private static final long serialVersionUID = -9185186586940085148L;

	public Gingerman() {
		setLife(0);
		imageNames[0] = Assets.GINGERMAN_IMAGE_ONE;
		imageNames[1] = Assets.GINGERMAN_IMAGE_TWO;
		imageNames[2] = Assets.GINGERMAN_IMAGE_THREE;
		imageNames[3] = Assets.GINGERMAN_IMAGE_FOUR;
		imageNames[4] = Assets.GINGERMAN_IMAGE_FIVE;
		setGingermanImage(new Texture(
				Gdx.files.internal(Assets.GINGERMAN_IMAGE_ONE)));
		this.setSize(110, 125);
		this.setX(Game.WIDTH / 2 - this.getWidth() / 2);
		this.setY(20);
	}

	public Texture getGingermanImage() {
		return gingermanImage;
	}

	public void update() {
		gingermanImage = new Texture(Gdx.files.internal(imageNames[Math
				.abs(getLife()) % 5]));
	}

	public void setGingermanImage(Texture gingermanImage) {
		this.gingermanImage = gingermanImage;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life += life;
	}
}
