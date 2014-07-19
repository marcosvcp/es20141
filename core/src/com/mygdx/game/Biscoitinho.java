package com.mygdx.game;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import control.Assets;
import control.DropObject;
import control.Gingerman;
import control.RainDrop;
import control.SugarDrop;

public class Biscoitinho extends ApplicationAdapter {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;

	private boolean clicked;

	private SpriteBatch batch;
	private OrthographicCamera camera;

	private Array<DropObject> drops;
	private long lastDropTime;

	private TextureRegion region;
	private Gingerman gingerMan;

	private Music rain;

	@Override
	public void create() {
		batch = new SpriteBatch();
		drops = new Array<DropObject>();
		loadCamera();
		loadSoundAndMusics();
		loadElements();
		spawnRaindrop();
		Texture texture = new Texture(Gdx.files.internal("bg.jpg"));
		region = new TextureRegion(texture, 0, 0, WIDTH, HEIGHT);
	}

	private void loadElements() {
		gingerMan = new Gingerman();
		gingerMan.setSize(110, 125);
		gingerMan.setX(WIDTH / 2 - gingerMan.getWidth() / 2);
		gingerMan.setY(20);

	}

	private void loadSoundAndMusics() {
		rain = Gdx.audio.newMusic(Gdx.files.internal(Assets.GAME_MUSIC));
		rain.setLooping(true);
		rain.play();
	}

	private void loadCamera() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
	}

	@Override
	public void render() {
		batch.begin();
		batch.draw(region, 0, 0);
		batch.end();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		renderPlayer();
		renderRaindDrops();
	}

	private void renderRaindDrops() {
		createRaindDrops();
		batch.begin();
		for (DropObject rec : drops) {
			batch.draw(rec.getDroppable().getDropImage(), rec.x, rec.y);
		}
		batch.end();
	}

	private void createRaindDrops() {
		if (TimeUtils.nanoTime() - lastDropTime > 1000000) {
			spawnRaindrop();
		}
		Iterator<DropObject> it = drops.iterator();
		while (it.hasNext()) {
			DropObject rec = it.next();
			rec.y -= 200 * Gdx.graphics.getDeltaTime();
			if (rec.y + rec.height < 0) {
				it.remove();
			}
			if (rec.overlaps(gingerMan)) {
				gingerMan.setLife(rec.getDroppable().getModifyOfLife());
				rec.getDroppable().getDropSound().play();
				it.remove();
				gingerMan.update();
			}
		}
	}

	private void spawnRaindrop() {
		DropObject drop = initializeDrop();
		drop.width = 5;
		drop.height = 6;
		drop.x = MathUtils.random(0, WIDTH - drop.width);
		drop.y = HEIGHT;
		drops.add(drop);
		lastDropTime = TimeUtils.nanoTime();
	}

	private DropObject initializeDrop() {
		DropObject drop;
		if (getRandPercent(5)) {
			drop = new DropObject(new SugarDrop());
		} else {
			drop = new DropObject(new RainDrop());
		}
		return drop;
	}

	public boolean getRandPercent(int percent) {
		Random rand = new Random();
		return rand.nextInt(100) <= percent;
	}

	private void renderPlayer() {
		if (Gdx.input.isTouched()) {
			clicked = true;
		}
		if (clicked) {
			moveBucketToTouchLocal();
		} else {
			moveBucketToAccelerometer();
		}
		batch.begin();
		batch.draw(gingerMan.getGingermanImage(), gingerMan.x, gingerMan.y);
		batch.end();

	}

	private void moveBucketToAccelerometer() {
		Vector3 vector = new Vector3(16 * (Gdx.input.getAccelerometerY() + 10),
				Gdx.input.getAccelerometerX(), 0);
		camera.unproject(vector);
		if (gingerMan.x < vector.x) {
			gingerMan.x += 200 * Gdx.graphics.getDeltaTime();
			if (gingerMan.x > vector.x) {
				gingerMan.x = vector.x;
			}
		} else if (gingerMan.x > vector.x) {
			gingerMan.x -= 200 * Gdx.graphics.getDeltaTime();
			if (gingerMan.x < vector.x) {
				gingerMan.x = vector.x;
			}
		} else {
			clicked = false;
		}

		if (gingerMan.x > WIDTH - gingerMan.width) {
			gingerMan.x = WIDTH - gingerMan.width;
		}
		if (gingerMan.x < 0) {
			gingerMan.x = 0;
		}
	}

	private void moveBucketToTouchLocal() {
		Vector3 vector = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(vector);
		if (gingerMan.x < vector.x) {
			gingerMan.x += 200 * Gdx.graphics.getDeltaTime();
			if (gingerMan.x > vector.x) {
				gingerMan.x = vector.x;
			}
		} else if (gingerMan.x > vector.x) {
			gingerMan.x -= 200 * Gdx.graphics.getDeltaTime();
			if (gingerMan.x < vector.x) {
				gingerMan.x = vector.x;
			}
		} else {
			clicked = false;
		}

		if (gingerMan.x > WIDTH - gingerMan.width) {
			gingerMan.x = WIDTH - gingerMan.width;
		}
		if (gingerMan.x < 0) {
			gingerMan.x = 0;
		}
	}

	@Override
	public void dispose() {
		rain.dispose();
		batch.dispose();
		gingerMan.getGingermanImage().dispose();
		for (DropObject drop : drops) {
			drop.getDroppable().getDropImage().dispose();
		}
		region.getTexture().dispose();
	}
}
