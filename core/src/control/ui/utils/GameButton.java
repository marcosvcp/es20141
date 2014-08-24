package control.ui.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Representa um botão no jogo Gingerman
 */
public class GameButton {
	private final int BOX_WIDTH = 260;
	private int BOX_HEIGHT = 90;
	private Rectangle bounds;
	private BitmapFont font;
	private String text;
	private int xText;
	private int yText;

	public GameButton(int x_rect, int y_rect, BitmapFont font, String text,
			int xText, int yText) {
		this.bounds = new Rectangle(x_rect, y_rect, BOX_WIDTH, BOX_HEIGHT);
		this.font = font;
		this.text = text;
		this.xText = xText;
		this.yText = yText;
	}

	public BitmapFont getFont() {
		return font;
	}

	public void setFont(BitmapFont font) {
		this.font = font;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public void drawFont(SpriteBatch batch) {
		batch.begin();
		font.draw(batch, text, xText, yText);
		batch.end();
	}

	public int getxText() {
		return xText;
	}

	public void setxText(int xText) {
		this.xText = xText;
	}

	public int getyText() {
		return yText;
	}

	public void setyText(int yText) {
		this.yText = yText;
	}
}
