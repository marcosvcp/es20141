package com.mygdx.game;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import control.Assets;
import control.DropObject;
import control.Gingerman;
import control.Jellybean;
import control.OverlapTester;
import control.RainDrop;
import control.RainDropLarge;
import control.SugarDrop;

/**
 * Classe principal do Jogo
 */
public class Game extends ApplicationAdapter {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;

	private boolean clicked;

	public enum State {
		Running, Paused, GameOver
	}

	// Buttons
	private Vector3 touchPoint;
	private Rectangle restartBounds;
	private Rectangle muteBounds;
	private Rectangle pauseBounds;

	// In nanoseconds
	private final int TIME_TO_SPAWN_NEW_DROP = 1000000;
	private long totalTime = 0L;
	private boolean gameOver;
	private State state = State.Running;
	private ShapeRenderer shape;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Array<DropObject> drops;
	private long lastDropTime;
	private BitmapFont font;
	private TextureRegion background;
	private Gingerman gingerMan;
	private boolean muted = false;
	private long initMilis;
	private Music crankDance;
	private int fontScaleRectangleMenuItens;

	@Override
	public void create() {
		Gdx.app.log(Gdx.graphics.getWidth() + "", "HEIGHT");
		createButtonsAndSetFont();
		batch = new SpriteBatch();
		shape = new ShapeRenderer();
		drops = new Array<DropObject>();
		loadCamera();
		loadSoundAndMusics();
		loadElements();
		spawnDrop();
		Texture texture = new Texture(
				Gdx.files.internal(Assets.BACKGROUND_IMAGE));
		background = new TextureRegion(texture, Assets.ZERO, Assets.ZERO,
				WIDTH, HEIGHT);
		initMilis = TimeUtils.millis();
	}

	private void createButtonsAndSetFont() {
		final int mediumScaleTextWidth = 120;
		final int offSetButtons = 210;
		final float FONT_SCALE = 1.5f;
		final int initialXRectangle = 10;
		int numberOfButtons = 0;
		fontScaleRectangleMenuItens = (int) (FONT_SCALE * 16);

		restartBounds = new Rectangle(initialXRectangle
				+ (numberOfButtons * offSetButtons), HEIGHT - 50,
				mediumScaleTextWidth + 20, fontScaleRectangleMenuItens);

		numberOfButtons++;

		muteBounds = new Rectangle(initialXRectangle
				+ (numberOfButtons * offSetButtons), HEIGHT - 50,
				mediumScaleTextWidth, fontScaleRectangleMenuItens);

		numberOfButtons++;

		pauseBounds = new Rectangle(initialXRectangle
				+ (numberOfButtons * offSetButtons), HEIGHT - 50,
				mediumScaleTextWidth, fontScaleRectangleMenuItens);

		numberOfButtons++;
		touchPoint = new Vector3();
		font = new BitmapFont();
		font.scale(FONT_SCALE);
		font.setColor(Color.WHITE);
	}

	/**
	 * Carrega o personagem principal {@code gingerMan}.
	 */
	private void loadElements() {
		gingerMan = new Gingerman();

	}

	/**
	 * Carrega a música do jogo {@code crankDance}.
	 */
	private void loadSoundAndMusics() {
		crankDance = Gdx.audio.newMusic(Gdx.files.internal(Assets.GAME_MUSIC));
		crankDance.setLooping(true);
		crankDance.play();
	}

	/**
	 * Carrega a {@code camera}.
	 */
	private void loadCamera() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
	}

	@Override
	public void render() {
		switch (state) {
		case Running:
			updateAll();
			break;
		case GameOver:
			gameOver();
			gameOver = true;
			break;
		case Paused:
			updateAll();
			break;
		default:
			break;
		}
		renderButtons();
		renderPlayer();
		if (!isPaused()) {
			renderDrops();
		}
	}

	private void renderButtons() {
		restartButton();
		muteButton();
		pauseButton();
	}

	private void mute() {
		this.muted = true;
		crankDance.pause();
	}

	private void unMute() {
		this.muted = false;
		crankDance.play();
	}

	private void pauseButton() {
		batch.begin();
		font.setColor(Color.MAROON);
		if (isPaused()) {
			font.setColor(Color.DARK_GRAY);
		}
		font.draw(batch, "Pause", 430, HEIGHT);
		batch.end();
		font.setColor(Color.WHITE);
		if (Gdx.input.justTouched()) {
			camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					Assets.ZERO));

			if (OverlapTester.pointInRectangle(pauseBounds, touchPoint.x,
					touchPoint.y)) {
				state = isPaused() ? State.Running : State.Paused;
				return;
			}
		}
	}

	private boolean isPaused() {
		return state == State.Paused;
	}

	private void muteButton() {
		batch.begin();
		if (muted) {
			font.setColor(Color.GRAY);
		}
		font.draw(batch, "Mute", 220, HEIGHT);
		batch.end();
		font.setColor(Color.WHITE);
		if (Gdx.input.justTouched()) {
			camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					Assets.ZERO));

			if (OverlapTester.pointInRectangle(muteBounds, touchPoint.x,
					touchPoint.y)) {
				changeSound();
				return;
			}
		}
	}

	private void changeSound() {
		if (muted) {
			unMute();
		} else {
			mute();
		}
	}

	private void restartButton() {
		batch.begin();
		font.draw(batch, "Restart", 10, HEIGHT);
		batch.end();
		if (Gdx.input.justTouched()) {
			camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					Assets.ZERO));

			if (OverlapTester.pointInRectangle(restartBounds, touchPoint.x,
					touchPoint.y)) {
				loadCamera();
				loadElements();
				drops.clear();
				initMilis = TimeUtils.millis();
				state = State.Running;
				gameOver = false;
				return;
			}
		}
	}

	private void gameOver() {
		final int textAppearX = 25;
		if (!gameOver) {
			totalTime = (TimeUtils.millis() - initMilis) / 1000;
		}
		String totalTimeText = "Game Over ! Your Time: " + totalTime + " s";
		batch.begin();
		batch.draw(background, Assets.ZERO, Assets.ZERO);
		font.draw(batch, (totalTimeText), textAppearX, HEIGHT - 80);
		batch.end();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		shape.setProjectionMatrix(camera.combined);
	}

	private void updateAll() {
		final String LEFT_POINTS_NAME = "Left Points: ";
		final int textAppearX = 25;
		batch.begin();
		batch.draw(background, Assets.ZERO, Assets.ZERO);
		font.draw(batch, LEFT_POINTS_NAME + gingerMan.getLife(), textAppearX,
				HEIGHT - 80);
		batch.end();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		shape.setProjectionMatrix(camera.combined);
	}

	/**
	 * Renderiza todas as gotas e grãos de açucar criados.
	 */
	private void renderDrops() {
		createDrops();
		batch.begin();
		for (DropObject rec : drops) {
			batch.draw(rec.getDroppable().getDropImage(), rec.x, rec.y);
		}
		batch.end();
	}

	/**
	 * Cria o cenário de gotas de chuva e grãos de açucar.
	 */
	private void createDrops() {
		if (TimeUtils.nanoTime() - lastDropTime > TIME_TO_SPAWN_NEW_DROP) {
			spawnDrop();
		}
		Iterator<DropObject> it = drops.iterator();
		while (it.hasNext()) {
			DropObject drop = it.next();
			drop.y -= 200 * Gdx.graphics.getDeltaTime();
			if (drop.y + drop.height < Assets.ZERO) {
				it.remove();
			}
			if (drop.overlaps(gingerMan)) {
				if (!muted) {
					drop.playSound();
				}
				gingerMan.update();
				if (!gameOver) {
					gingerMan.updateLife(drop.getDroppable().getModifyOfLife());
				}
				if (!gingerMan.isAlive()) {
					this.state = State.GameOver;
				}
				it.remove();
			}

		}
	}

	/**
	 * Renderiza o objeto escolhido em {@link Game#initializeDrop}.
	 */
	private void spawnDrop() {
		DropObject drop = createDrop();
		spawnDrop(drop);
	}

	private void spawnDrop(DropObject drop) {
		Texture dropImage = drop.getDroppable().getDropImage();
		float imageWidth = dropImage.getWidth();
		drop.x = MathUtils.random(imageWidth, WIDTH - imageWidth);
		drop.y = HEIGHT;
		drops.add(drop);
		lastDropTime = TimeUtils.nanoTime();
	}

	/**
	 * Inicializa qual tipo de objeto vai cair.
	 */
	private DropObject createDrop() {
		final int PERCENT_TO_SPAWN_SUGAR = 5;
		final int PERCENT_TO_SPAWN_JELLYBEAN = 5; // If Rain Large was dropped
		final int PERCENT_TO_SPAWN_RAIN_LARGE = 1;

		DropObject drop;
		if (getRandPercent(PERCENT_TO_SPAWN_SUGAR)) {
			drop = new DropObject(SugarDrop.getInstance());
		} else if (getRandPercent(PERCENT_TO_SPAWN_RAIN_LARGE)) {
			if (getRandPercent(PERCENT_TO_SPAWN_JELLYBEAN)) {
				spawnDrop(new DropObject(Jellybean.getInstance()));
			}
			drop = new DropObject(RainDropLarge.getInstance());
		} else {
			drop = new DropObject(RainDrop.getInstance());
		}
		return drop;
	}

	/**
	 * Retorna se um certo evento deve ocorrer de acordo com a {@code percent}.
	 */
	public boolean getRandPercent(int percent) {
		Random rand = new Random();
		return rand.nextInt(100) <= percent;
	}

	/**
	 * Desenha o {@code gingerman}.
	 */
	private void renderPlayer() {
		if (Gdx.input.isTouched()) {
			clicked = true;
		}
		if (clicked && !isPaused()) {
			moveGingerManToTouchLocal();
		} else {
			moveGingermanToAccelerometer();
		}
		if (gingerMan.x > WIDTH - gingerMan.width) {
			gingerMan.x = WIDTH - gingerMan.width;
		}
		if (gingerMan.x < Assets.ZERO) {
			gingerMan.x = Assets.ZERO;
		}

		renderLife();

		batch.begin();
		batch.draw(gingerMan.getGingermanImage(), gingerMan.x, gingerMan.y);
		batch.end();

	}

	private void renderLife() {
		shape.begin(ShapeType.Filled);
		Rectangle visualRectangle = gingerMan.getVisualLifeRectangle();
		shape.setColor(gingerMan.getColor());
		shape.rect(visualRectangle.x, visualRectangle.y, visualRectangle.width,
				visualRectangle.height);
		shape.end();
	}

	/**
	 * Move o {@code gingerman} de acordo com o acelerometro.
	 */
	private void moveGingermanToAccelerometer() {
		// int constant = Gdx.graphics.getHeight() / 10;
		// Vector3 vector = new Vector3(constant
		// * (Gdx.input.getAccelerometerY() + 10),
		// Gdx.input.getAccelerometerX(), 0);
		// camera.unproject(vector);
		// if (gingerMan.x < vector.x) {
		// gingerMan.x += 200 * Gdx.graphics.getDeltaTime();
		// if (gingerMan.x > vector.x) {
		// gingerMan.x = vector.x;
		// }
		// } else if (gingerMan.x > vector.x) {
		// gingerMan.x -= 200 * Gdx.graphics.getDeltaTime();
		// if (gingerMan.x < vector.x) {
		// gingerMan.x = vector.x;
		// }
		// } else {
		// clicked = false;
		// }

	}

	/**
	 * Move o {@code gingerman} para o local tocado.
	 */
	private void moveGingerManToTouchLocal() {
		Vector3 vector = new Vector3(Gdx.input.getX(), Gdx.input.getY(),
				Assets.ZERO);
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

	}

	@Override
	public void dispose() {
		crankDance.dispose();
		batch.dispose();
		gingerMan.getGingermanImage().dispose();
		SugarDrop.getInstance().dispose();
		RainDrop.getInstance().dispose();
		RainDropLarge.getInstance().dispose();
		Jellybean.getInstance().dispose();
		background.getTexture().dispose();
	}
}
