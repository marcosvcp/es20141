package com.gridrunner.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GridRunner extends ApplicationAdapter {

	double coolDown;
	List<Obstacle> obstacles;
	private SpriteBatch batch;
	int yCreate = 300;

	@Override
	public void create() {
		batch = new SpriteBatch();
		Gdx.gl.glClearColor(1, 0, 0, 1);
		obstacles = new ArrayList<Obstacle>();
		obstacles.add(new Obstacle(batch, 0, yCreate, -1));
		obstacles.add(new Obstacle(batch, 0, yCreate + 100, -1));
	}

	@Override
	public void render() {
		if (coolDown % 40 == 0) {
			yCreate += 50;
			obstacles.add(new Obstacle(batch, 0, yCreate, -1));
			obstacles.add(new Obstacle(batch, 0, yCreate + 100, -1));
		}
		coolDown += 0.25;
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		if (obstacles.get(0).getYposition() < 0) {
			obstacles.remove(obstacles.get(0));
		}
		for (Obstacle obstacle : obstacles) {
			obstacle.render();
		}
		batch.end();
	}
}
