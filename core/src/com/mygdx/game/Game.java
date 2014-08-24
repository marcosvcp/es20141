package com.mygdx.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;

import control.DropObject;
import control.Gingerman;
import control.Jellybean;
import control.RainDrop;
import control.RainDropLarge;
import control.SugarDrop;
import control.ui.utils.Assets;
import control.ui.utils.GameButton;
import control.ui.utils.OverlapTester;
import control.ui.utils.UIUtils;

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
	private GameButton startButton;
	private GameButton muteButton;
	private GameButton pauseButton;
	private GameButton exitButton;
	private GameButton instructionsButton;
	private GameButton rankingButton;

	//
	private Map<String, Long> ranking;
	private boolean writing = false;
	// In nanoseconds
	// Current player
	private StringBuilder playerName;
	private int timeToSpawnNewDrop = 1000000;
	private long totalTime = 0L;
	private boolean gameOver;
	private State state = State.Paused;
	private ShapeRenderer shape;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Array<DropObject> drops;
	private long lastDropTime;
	private BitmapFont font;

	private TextureRegion background;
	private TextureRegion gameBG;
	private TextureRegion menuBG;
	private TextureRegion rankingBG;
	private TextureRegion instructionsBG;

	private Gingerman gingerMan;
	private boolean muted = false;
	private boolean readingInstructions = false;
	private boolean readingRanking = false;
	private long initMilis;
	private Music crankDance;

	@SuppressWarnings("unchecked")
	@Override
	public void create() {
		Gdx.input.setCatchBackKey(true);
		Preferences prefs = Gdx.app.getPreferences("My Preferences");
		String json = prefs.getString("rankingJSON");
		if (json != null && !json.isEmpty()) {
			ranking = (HashMap<String, Long>) new Json().fromJson(
					HashMap.class, json);
		} else {
			ranking = new HashMap<String, Long>();
		}
		playerName = new StringBuilder();
		createButtonsAndSetFont();
		batch = new SpriteBatch();
		shape = new ShapeRenderer();
		drops = new Array<DropObject>();
		loadCamera();
		loadSoundAndMusics();
		loadElements();
		menuBG = initMenuBackGround();
		gameBG = initGameBackGround();
		instructionsBG = initInstructionsBackGround();
		rankingBG = initRankingBackGround();
		background = menuBG;
		initMilis = TimeUtils.millis();
	}

	/**
	 * Cria o background da tela de instruções
	 */
	private TextureRegion initRankingBackGround() {
		Texture texture = getRankingBackGround();
		TextureRegion bg = new TextureRegion(texture, Assets.ZERO, Assets.ZERO,
				WIDTH, HEIGHT);
		return bg;
	}

	/**
	 * Instancia e retorna a {@code texture} do background de instruções
	 */
	private Texture getRankingBackGround() {
		Texture texture = new Texture(Gdx.files.internal(Assets.RANKING_IMAGE));
		return texture;
	}

	/**
	 * Cria o background da tela de instruções
	 */
	private TextureRegion initInstructionsBackGround() {
		Texture texture = getInstructionsBackGround();
		TextureRegion bg = new TextureRegion(texture, Assets.ZERO, Assets.ZERO,
				WIDTH, HEIGHT);
		return bg;
	}

	/**
	 * Cria o background da tela do jogo
	 */
	private TextureRegion initGameBackGround() {
		Texture texture = getGameBackGround();
		TextureRegion bg = new TextureRegion(texture, Assets.ZERO, Assets.ZERO,
				WIDTH, HEIGHT);
		return bg;
	}

	/**
	 * Instancia e retorna a {@code texture} do background de instruções
	 */
	private Texture getInstructionsBackGround() {
		Texture texture = new Texture(
				Gdx.files.internal(Assets.INSTRUCTIONS_IMAGE));
		return texture;
	}

	/**
	 * Cria o background da tela de menu
	 */
	private TextureRegion initMenuBackGround() {
		Texture texture = getMenuBackGround();
		TextureRegion bg = new TextureRegion(texture, Assets.ZERO, Assets.ZERO,
				WIDTH, HEIGHT);
		return bg;
	}

	/**
	 * Instancia e retorna a {@code texture} do background do jogo
	 */
	private Texture getGameBackGround() {
		Texture texture = new Texture(
				Gdx.files.internal(Assets.BACKGROUND_IMAGE));
		return texture;
	}

	/**
	 * Instancia e retorna a {@code texture} do background do menu
	 */
	private Texture getMenuBackGround() {
		Texture texture = new Texture(
				Gdx.files.internal(Assets.MENU_BACKGROUND_IMAGE));
		return texture;
	}

	/**
	 * Instancia os botões a e fonte usada no texto deles.
	 */
	private void createButtonsAndSetFont() {
		final float FONT_SCALE = 1.5f;
		touchPoint = new Vector3();
		font = new BitmapFont();
		font.scale(FONT_SCALE);
		font.setColor(Color.WHITE);
		startButton = new GameButton(UIUtils.SECOND_BOX_MENU_LEFTBOTTOM_X
				+ UIUtils.BOX_MENU_RECT_OFFSET_X,
				UIUtils.SECOND_BOX_MENU_LEFTBOTTOM_Y
						+ UIUtils.BOX_MENU_RECT_OFFSET_Y, font, "Start",
				UIUtils.SECOND_BOX_MENU_LEFTBOTTOM_X,
				UIUtils.SECOND_BOX_MENU_LEFTBOTTOM_Y);
		muteButton = new GameButton(UIUtils.FIRST_BOX_MENU_LEFTBOTTOM_X
				+ UIUtils.BOX_MENU_RECT_OFFSET_X,
				UIUtils.FIRST_BOX_MENU_LEFTBOTTOM_Y
						+ UIUtils.BOX_MENU_RECT_OFFSET_Y, font, "Mute",
				UIUtils.FIRST_BOX_MENU_LEFTBOTTOM_X,
				UIUtils.FIRST_BOX_MENU_LEFTBOTTOM_Y);

		pauseButton = new GameButton(UIUtils.THIRD_BOX_MENU_LEFTBOTTOM_X
				+ UIUtils.BOX_MENU_RECT_OFFSET_X,
				UIUtils.THIRD_BOX_MENU_LEFTBOTTOM_Y
						+ UIUtils.BOX_MENU_RECT_OFFSET_Y, font, "Resume",
				UIUtils.THIRD_BOX_MENU_LEFTBOTTOM_X - 27,// pixels to centralize
				UIUtils.THIRD_BOX_MENU_LEFTBOTTOM_Y);

		exitButton = new GameButton(UIUtils.FOURTH_BOX_MENU_LEFTBOTTOM_X
				+ UIUtils.BOX_MENU_RECT_OFFSET_X,
				UIUtils.FOURTH_BOX_MENU_LEFTBOTTOM_Y
						+ UIUtils.BOX_MENU_RECT_OFFSET_Y, font, "Exit",
				UIUtils.FOURTH_BOX_MENU_LEFTBOTTOM_X,
				UIUtils.FOURTH_BOX_MENU_LEFTBOTTOM_Y);

		instructionsButton = new GameButton(UIUtils.FIFTH_BOX_MENU_LEFTBOTTOM_X
				+ UIUtils.BOX_MENU_RECT_OFFSET_X,
				UIUtils.FIFTH_BOX_MENU_LEFTBOTTOM_Y
						+ UIUtils.BOX_MENU_RECT_OFFSET_Y, font, "Instructions",
				UIUtils.FIFTH_BOX_MENU_LEFTBOTTOM_X - 48, // Pixels to
															// centralize
				UIUtils.FIFTH_BOX_MENU_LEFTBOTTOM_Y);

		rankingButton = new GameButton(UIUtils.SIXTH_BOX_MENU_LEFTBOTTOM_X
				+ UIUtils.BOX_MENU_RECT_OFFSET_X,
				UIUtils.SIXTH_BOX_MENU_LEFTBOTTOM_Y
						+ UIUtils.BOX_MENU_RECT_OFFSET_Y, font, "Ranking",
				UIUtils.SIXTH_BOX_MENU_LEFTBOTTOM_X - 48, // Pixels to
															// centralize
				UIUtils.SIXTH_BOX_MENU_LEFTBOTTOM_Y);
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
		changeStateWhenTouchBack();
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
		if (!isPaused()) {
			renderPlayer();
			renderDrops();
		} else if (!readingInstructions) {
			renderButtons();
		}
	}

	/**
	 * Muda o estado da aplicação quando for presscionada a tecla BACK
	 */
	private void changeStateWhenTouchBack() {
		if (Gdx.input.isKeyPressed(Keys.BACK)) {
			if (!isPaused()) {
				if (!gameOver) {
					totalTime += (TimeUtils.millis() - initMilis) / 1000;
				}
				changeBackGroundWhenPaused();
				state = State.Paused;
				playerName = new StringBuilder();
				pauseButton();
			} else if (readingInstructions) {
				readingInstructions = false;
				readingRanking = false;
				background = menuBG;
			}
		}
	}

	/**
	 * Renderiza os botões do menu
	 */
	private void renderButtons() {
		startButton();
		muteButton();
		pauseButton();
		exitButton();
		instructionsButton();
		rankingButton();
	}

	/**
	 * Pausa a música {@code crankDance}
	 */
	private void mute() {
		this.muted = true;
		crankDance.pause();
	}

	/**
	 * Desapausa a música {@code crankDance}
	 */
	private void unMute() {
		this.muted = false;
		crankDance.play();
	}

	/**
	 * Pausa o jogo
	 */
	private void pauseButton() {
		pauseButton.drawFont(batch);
		if (Gdx.input.justTouched()) {
			camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					Assets.ZERO));
			if (OverlapTester.pointInRectangle(pauseButton.getBounds(),
					touchPoint.x, touchPoint.y)) {
				changeBackGroundWhenPaused();
				initMilis = TimeUtils.millis();
				return;
			}
		}
	}

	/**
	 * Leva a tela de instruções
	 */
	private void instructionsButton() {
		instructionsButton.drawFont(batch);
		if (Gdx.input.justTouched()) {
			camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					Assets.ZERO));
			if (OverlapTester.pointInRectangle(instructionsButton.getBounds(),
					touchPoint.x, touchPoint.y)) {
				background = instructionsBG;
				readingInstructions = true;
				return;
			}
		}
	}

	/**
	 * Leva a tela de rankings
	 */
	private void rankingButton() {
		rankingButton.drawFont(batch);
		if (Gdx.input.justTouched()) {
			camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					Assets.ZERO));
			if (OverlapTester.pointInRectangle(rankingButton.getBounds(),
					touchPoint.x, touchPoint.y)) {
				background = rankingBG;
				readingInstructions = true;
				readingRanking = true;
				return;
			}
		}
	}

	/**
	 * Finaliza a aplicação
	 */
	private void exitButton() {
		exitButton.drawFont(batch);
		if (Gdx.input.justTouched()) {
			camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					Assets.ZERO));
			if (OverlapTester.pointInRectangle(exitButton.getBounds(),
					touchPoint.x, touchPoint.y)) {
				Gdx.app.exit();
				return;
			}
		}
	}

	/**
	 * Volta pro menu ao estar jogando
	 */
	private void changeBackGroundWhenPaused() {
		if (isPaused()) {
			background = gameBG;
			state = State.Running;
		} else {
			background = menuBG;
			state = State.Paused;
		}
	}

	/**
	 * Retorna true caso seja verdadeiro
	 */
	public boolean isPaused() {
		return state == State.Paused;
	}

	/**
	 * Botão referente ao mute
	 */
	private void muteButton() {
		if (muted) {
			muteButton.getFont().setColor(Color.GRAY);
		}
		muteButton.drawFont(batch);
		muteButton.getFont().setColor(Color.WHITE);
		if (Gdx.input.justTouched()) {
			camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					Assets.ZERO));

			if (OverlapTester.pointInRectangle(muteButton.getBounds(),
					touchPoint.x, touchPoint.y)) {
				changeSound();
				return;
			}
		}
	}

	/**
	 * Muda o estado de mute para unmute ou o inverso
	 */
	private void changeSound() {
		if (muted) {
			unMute();
		} else {
			mute();
		}
	}

	/**
	 * Botão que inicializa as instancias de imagens e botões
	 */
	private void startButton() {
		startButton.drawFont(batch);
		if (Gdx.input.justTouched()) {
			camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					Assets.ZERO));
			if (OverlapTester.pointInRectangle(startButton.getBounds(),
					touchPoint.x, touchPoint.y)) {
				loadCamera();
				loadElements();
				drops.clear();
				initMilis = TimeUtils.millis();
				totalTime = 0L;
				state = State.Running;
				gameOver = false;
				background = gameBG;
				return;
			}
		}
	}

	private void gameOver() {
		final int textAppearX = 25;
		if (!gameOver) {
			totalTime += (TimeUtils.millis() - initMilis) / 1000;
		}
		String totalTimeText = "Game Over ! Your Time: " + totalTime + " s";
		batch.begin();
		batch.draw(background, Assets.ZERO, Assets.ZERO);
		if (!isPaused()) {
			font.draw(batch, (totalTimeText), textAppearX, HEIGHT);
			font.draw(batch, "Name: " + playerName.toString(),
					textAppearX + 170, HEIGHT - 100);
			if (writing) {
				writing = false;
				Gdx.input.setInputProcessor(new InputProcessor() {

					@Override
					public boolean touchUp(int screenX, int screenY,
							int pointer, int button) {
						return false;
					}

					@Override
					public boolean touchDragged(int screenX, int screenY,
							int pointer) {
						return false;
					}

					@Override
					public boolean touchDown(int screenX, int screenY,
							int pointer, int button) {
						return false;
					}

					@Override
					public boolean scrolled(int amount) {
						return false;
					}

					@Override
					public boolean mouseMoved(int screenX, int screenY) {
						return false;
					}

					@Override
					public boolean keyUp(int keycode) {
						if (keycode == Input.Keys.ENTER) {
							ranking.put(playerName.toString(), totalTime);
							Gdx.input.setOnscreenKeyboardVisible(false);
							persist();
							writing = false;
						} else if (keycode == Input.Keys.BACKSPACE) {
							playerName.deleteCharAt(playerName.length() - 1);
						}
						return false;
					}

					private void persist() {
						Preferences prefs = Gdx.app
								.getPreferences("My Preferences");
						Json json = new Json();
						String jsonString = json.toJson(ranking);
						prefs.putString("rankingJSON", jsonString);
						prefs.flush();
					}

					@Override
					public boolean keyTyped(char character) {
						playerName.append(character);
						return false;
					}

					@Override
					public boolean keyDown(int keycode) {
						return false;
					}
				});
				Gdx.input.setOnscreenKeyboardVisible(true);
			}
		}
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
		if (!isPaused()) {
			font.draw(batch, LEFT_POINTS_NAME + gingerMan.getLife(),
					textAppearX, HEIGHT);
		}
		if (readingRanking) {
			int countWinners = 0;
			List<String> listset = new ArrayList<String>(ranking.keySet());
			Collections.sort(listset);
			for (String k : listset) {
				if (countWinners == 5) {
					break;
				}
				font.draw(batch, k + ": " + ranking.get(k) + " s", 300,
						375 - (countWinners * 55));
				countWinners++;
			}
		}
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
		if (TimeUtils.nanoTime() - lastDropTime > timeToSpawnNewDrop) {
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
				if (!gingerMan.isAlive() && this.state != State.GameOver) {
					writing = true;
					Gdx.input.setOnscreenKeyboardVisible(true);
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
				timeToSpawnNewDrop = (int) (timeToSpawnNewDrop / 1.2);
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
		final int MAX_PERCENT = 100;
		Random rand = new Random();
		return rand.nextInt(MAX_PERCENT) <= percent;
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
		if (!isPaused()) {
			renderLife();
		}

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
		final int X_ACC = 200;
		Vector3 vector = new Vector3(Gdx.input.getX(), Gdx.input.getY(),
				Assets.ZERO);
		camera.unproject(vector);
		if (gingerMan.x < vector.x) {
			gingerMan.x += X_ACC * Gdx.graphics.getDeltaTime();
			if (gingerMan.x > vector.x) {
				gingerMan.x = vector.x;
			}
		} else if (gingerMan.x > vector.x) {
			gingerMan.x -= X_ACC * Gdx.graphics.getDeltaTime();
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
