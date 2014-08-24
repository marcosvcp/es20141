package control;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Game;

public abstract class PlayerChar extends Rectangle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int life;
	private Rectangle visualLifeRectangle;
	private Color color;
	private Texture[] charImagens;

	public static final int MAXIMUM_LIFE = 100;
	public static final int MINIMUM_LIFE = 0;
	public static final float INITIAL_RECTANGLE_LIFE_HEIGHT = Game.HEIGHT - 40;

	public PlayerChar() {
		this.charImagens = new Texture[5];
		this.life = MAXIMUM_LIFE;
		color = Color.GREEN;
		visualLifeRectangle = new Rectangle(Game.WIDTH - 40, 20, 20,
				INITIAL_RECTANGLE_LIFE_HEIGHT);
		initImages();
		this.setSize(110, 125);
		this.setX(Game.WIDTH / 2 - this.getWidth() / 2);
		this.setY(20);
	}

	public abstract void initImages();

	public boolean isAlive() {
		return life > 0;
	}

	public abstract Texture getGingermanImage();

	public abstract void update();

	public void revive() {
		this.life = MAXIMUM_LIFE;
	}

	public int getLife() {
		return life;
	}

	public void updateLife(int life) {
		this.life += life;
		if (this.life >= MAXIMUM_LIFE) {
			this.life = MAXIMUM_LIFE;
		} else if (this.life <= MINIMUM_LIFE) {
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

	public Texture[] getCharImagens() {
		return charImagens;
	}

	public void setCharImagens(Texture[] charImagens) {
		this.charImagens = charImagens;
	}
}
