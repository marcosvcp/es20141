package control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Game;

public class Gingerman extends Rectangle {
	private Texture gingermanImage;
	private Texture[] imageNames = new Texture[5];
	private int life;
	private Rectangle visualLifeRectangle;
	private Color color;

	public static final int MAXIMUM_LIFE = 100;
	public static final int MINIMUM_LIFE = 0;
	public static final float INITIAL_RECTANGLE_LIFE_HEIGHT = Game.HEIGHT - 40;
	/**
	 * 
	 */
	private static final long serialVersionUID = -9185186586940085148L;

	public Gingerman() {
		this.life = MAXIMUM_LIFE;
		color = Color.GREEN;
		visualLifeRectangle = new Rectangle(Game.WIDTH - 40, 20, 20,
				INITIAL_RECTANGLE_LIFE_HEIGHT);
		initImages();
		this.setSize(110, 125);
		this.setX(Game.WIDTH / 2 - this.getWidth() / 2);
		this.setY(20);
	}

	private void initImages() {
		imageNames[0] = new Texture(
				Gdx.files.internal(Assets.GINGERMAN_IMAGE_ONE));
		imageNames[1] = new Texture(
				Gdx.files.internal(Assets.GINGERMAN_IMAGE_TWO));
		imageNames[2] = new Texture(
				Gdx.files.internal(Assets.GINGERMAN_IMAGE_THREE));
		imageNames[3] = new Texture(
				Gdx.files.internal(Assets.GINGERMAN_IMAGE_FOUR));
		imageNames[4] = new Texture(
				Gdx.files.internal(Assets.GINGERMAN_IMAGE_FIVE));
		setGingermanImage(new Texture(
				Gdx.files.internal(Assets.GINGERMAN_IMAGE_ONE)));
	}

	public Texture getGingermanImage() {
		return gingermanImage;
	}

	public boolean isAlive() {
		return life > 0;
	}

	public void update() {
		gingermanImage = imageNames[Math.abs(getLife()) % 5];
	}

	public void setGingermanImage(Texture gingermanImage) {
		this.gingermanImage = gingermanImage;
	}

	public int getLife() {
		return life;
	}

	public void updateLife(int life) {
		this.life += life;
		if (this.life > MAXIMUM_LIFE) {
			this.life = MAXIMUM_LIFE;
		} else if (this.life < MINIMUM_LIFE) {
			this.life = MINIMUM_LIFE;

		}

		visualLifeRectangle.height = (this.life * INITIAL_RECTANGLE_LIFE_HEIGHT) / 100; // Regra
	}

	public Rectangle getVisualLifeRectangle() {
		return visualLifeRectangle;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
