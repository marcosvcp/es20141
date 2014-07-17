package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Biscoitinho extends ApplicationAdapter {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;

	private boolean clicked;

	private int rainDropCatcher = 0;
	private SpriteBatch batch;
	private OrthographicCamera camera;

	private Texture bucketImage;
	private Texture dropImage;

	private Array<Rectangle> raindrops;
	private long lastDropTime;

	private Rectangle bucket;

	private Sound drop;
	private Music rain;
	private String[] imageNames = new String[5];

	@Override
	public void create() {
		imageNames[0] = "boneco1.png";
		imageNames[1] = "boneco2.png";
		imageNames[2] = "boneco3.png";
		imageNames[3] = "boneco4.png";
		imageNames[4] = "boneco5.png";
		batch = new SpriteBatch();
		raindrops = new Array<Rectangle>();
		loadCamera();
		loadImages();
		loadSoundAndMusics();
		loadElements();
		spawnRaindrop();
	}

	private void loadElements() {
		bucket = new Rectangle();
		bucket.setSize(110, 125);
		bucket.setX(WIDTH / 2 - bucket.getWidth() / 2);
		bucket.setY(20);

	}

	private void loadSoundAndMusics() {
		drop = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rain = Gdx.audio.newMusic(Gdx.files.internal("dancaDaManivela.mp3"));
		rain.setLooping(true);
		rain.play();
	}

	private void loadImages() {
		dropImage = new Texture(Gdx.files.internal("droplet2.png"));
		bucketImage = new Texture(Gdx.files.internal("boneco1.png"));
		// background = new Texture(Gdx.files.internal("chuva.jpg"));
	}

	private void loadCamera() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		renderPlayer();
		renderRaindDrops();
	}

	private void renderRaindDrops() {
		createRaindDrops();
		batch.begin();
		for (Rectangle rec : raindrops) {
			batch.draw(dropImage, rec.x, rec.y);
		}
		batch.end();
	}

	private void createRaindDrops() {
		if (TimeUtils.nanoTime() - lastDropTime > 1000000) {
			spawnRaindrop();
		}
		Iterator<Rectangle> it = raindrops.iterator();
		while (it.hasNext()) {
			Rectangle rec = it.next();
			rec.y -= 200 * Gdx.graphics.getDeltaTime();
			if (rec.y + rec.height < 0) {
				it.remove();
			}
			if (rec.overlaps(bucket)) {
				rainDropCatcher++;
				drop.play();
				it.remove();
				updateBucket();
			}
		}
	}

	private void updateBucket() {
		bucketImage = new Texture(
				Gdx.files.internal(imageNames[rainDropCatcher % 5]));
	}

	private void spawnRaindrop() {
		Rectangle drop = new Rectangle();
		drop.width = 5;
		drop.height = 6;
		drop.x = MathUtils.random(0, WIDTH - drop.width);
		drop.y = HEIGHT;
		raindrops.add(drop);
		lastDropTime = TimeUtils.nanoTime();
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
		batch.draw(bucketImage, bucket.x, bucket.y);
		batch.end();

	}

	private void moveBucketToAccelerometer() {
		Vector3 vector = new Vector3(16 * (Gdx.input.getAccelerometerY() + 10),
				Gdx.input.getAccelerometerX(), 0);
		camera.unproject(vector);
		if (bucket.x < vector.x) {
			bucket.x += 200 * Gdx.graphics.getDeltaTime();
			if (bucket.x > vector.x) {
				bucket.x = vector.x;
			}
		} else if (bucket.x > vector.x) {
			bucket.x -= 200 * Gdx.graphics.getDeltaTime();
			if (bucket.x < vector.x) {
				bucket.x = vector.x;
			}
		} else {
			clicked = false;
		}

		if (bucket.x > WIDTH - bucket.width) {
			bucket.x = WIDTH - bucket.width;
		}
		if (bucket.x < 0) {
			bucket.x = 0;
		}
	}

	private void moveBucketToTouchLocal() {
		Vector3 vector = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(vector);
		if (bucket.x < vector.x) {
			bucket.x += 200 * Gdx.graphics.getDeltaTime();
			if (bucket.x > vector.x) {
				bucket.x = vector.x;
			}
		} else if (bucket.x > vector.x) {
			bucket.x -= 200 * Gdx.graphics.getDeltaTime();
			if (bucket.x < vector.x) {
				bucket.x = vector.x;
			}
		} else {
			clicked = false;
		}

		if (bucket.x > WIDTH - bucket.width) {
			bucket.x = WIDTH - bucket.width;
		}
		if (bucket.x < 0) {
			bucket.x = 0;
		}
	}

	@Override
	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		drop.dispose();
		rain.dispose();
		batch.dispose();

	}
}
