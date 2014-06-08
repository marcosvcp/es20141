package com.gridrunner.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GridRunner extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	private int x;
	private int y;
	private int width;
	private int height;
	private int x2;
	private int y2;

	@Override
	public void create() {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, x, y);
		batch.end();
		// ShapeRenderer shapeRenderer = new ShapeRenderer();
		// shapeRenderer.begin(ShapeType.Line);
		// shapeRenderer.setColor(1, 1, 0, 1);
		// shapeRenderer.line(x, y, x2, y2);
		// shapeRenderer.rect(x, y, width, height);
		// shapeRenderer.circle(x, y, 10);
		// shapeRenderer.end();
	}

	public void move() {
		ShapeRenderer shapeRenderer = new ShapeRenderer();
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(1, 1, 0, 1);
		shapeRenderer.line(x, y, x2, y2);
		shapeRenderer.rect(x, y, width, height);
		shapeRenderer.circle(x, y, 10);
		shapeRenderer.end();
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		x += 10;
		batch.draw(img, x, y);
		batch.end();
	}
}
