package com.gridrunner.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Obstacle extends ApplicationAdapter {
	private Texture firstBar;
	private Texture secondBar;
	private int xPosition, yPosition, velY;
	private SpriteBatch batch;

	public Obstacle(SpriteBatch batch, int xPosition, int yPosition, int vely) {
		this.batch = batch;
		this.setXposition(xPosition);
		this.setYposition(yPosition);
		this.velY = vely;
		create();
	}

	@Override
	public void create() {
		setFirstBar(new Texture("barra.jpg"));
		setSecondBar(new Texture("barra.jpg"));
	}

	@Override
	public void render() {
		setXposition(getXposition() + (int) Math.random() * 1000);
		if (getXposition() > getSecondBar().getWidth() * 2 + 10) {
			setXposition(0);
		}
		setYposition(getYposition() + velY);
		batch.draw(getFirstBar(), getXposition(), (int) getYposition());
		batch.draw(getSecondBar(), getXposition() + getFirstBar().getWidth()
				+ 10, (int) getYposition());
	}

	public Texture getFirstBar() {
		return firstBar;
	}

	public void setFirstBar(Texture img) {
		this.firstBar = img;
	}

	public Texture getSecondBar() {
		return secondBar;
	}

	public void setSecondBar(Texture bar2) {
		this.secondBar = bar2;
	}

	public int getXposition() {
		return xPosition;
	}

	public void setXposition(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getYposition() {
		return yPosition;
	}

	public void setYposition(int yPosition) {
		this.yPosition = yPosition;
	}
}
