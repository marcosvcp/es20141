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
import control.Maria;
import control.Negresco;
import control.PlayerChar;
import control.RainDrop;
import control.RainDropLarge;
import control.SugarDrop;
import control.Tareco;
import control.Treloso;
import control.ui.utils.Assets;
import control.ui.utils.GameButton;
import control.ui.utils.MapUtil;
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
	private GameButton chooseCharLeft;
	private GameButton chooseCharRight;
	private InputProcessor inputGingerman;

	//
	private final String MY_PREFS = "My Preferences";
	private final String RANKING_PREF = "rankingJSON";
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
	private TextureRegion rankingBG;
	private TextureRegion instructionsBG;

	private PlayerChar[] players = new PlayerChar[5];
	private boolean muted = false;
	private boolean readingInstructions = false;
	private boolean readingRanking = false;
	private long initMilis;
	private Music crankDance;

	private int menuChoosed = 0;
	private TextureRegion[] menuBGS = new TextureRegion[5];

	@Override
	public void create() {
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);
		initInputProcessor();
		initPreferences();
		playerName = new StringBuilder();
		createButtonsAndSetFont();
		batch = new SpriteBatch();
		shape = new ShapeRenderer();
		drops = new Array<DropObject>();
		loadCamera();
		loadSoundAndMusics();
		loadPlayers();
		loadBackGrounds();
		loadMenuBackGrounds();
		background = getCurrentMenuBackGround();
		initMilis = TimeUtils.millis();
	}

	private TextureRegion getCurrentMenuBackGround() {
		return menuBGS[Math.abs(menuChoosed) % 5];
	}

	private void loadBackGrounds() {
		gameBG = initGameBackGround();
		instructionsBG = initInstructionsBackGround();
		rankingBG = initRankingBackGround();
	}

	private void loadMenuBackGrounds() {
		menuBGS[0] = initMenuBackGround();
		menuBGS[1] = initMenuMariaBackGround();
		menuBGS[2] = initMenuSoetBackGround();
		menuBGS[3] = initMenuTarecoGround();
		menuBGS[4] = initMenuTrelosoGround();
	}

	@SuppressWarnings("unchecked")
	private void initPreferences() {
		Preferences prefs = Gdx.app.getPreferences(MY_PREFS);
		String json = prefs.getString(RANKING_PREF);
		if (json != null && !json.isEmpty()) {
			ranking = (HashMap<String, Long>) new Json().fromJson(
					HashMap.class, json);
		} else {
			ranking = new HashMap<String, Long>();
		}
	}

	private void initInputProcessor() {
		inputGingerman = new InputProcessor() {

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer,
					int button) {
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer,
					int button) {
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
				if (keycode == Input.Keys.BACKSPACE) {
					if (playerName.length() >= 1) {
						playerName
								.deleteCharAt(playerName.toString().length() - 1);
					}
				}
				if (keycode == Input.Keys.ENTER) {
					ranking.put(playerName.toString(), totalTime);
					Gdx.input.setOnscreenKeyboardVisible(false);
					persist();
					writing = false;
					state = State.Paused;
					background = getCurrentMenuBackGround();
				}
				return false;
			}

			private void persist() {
				Preferences prefs = Gdx.app.getPreferences(MY_PREFS);
				Json json = new Json();
				String jsonString = json.toJson(ranking);
				prefs.putString(RANKING_PREF, jsonString);
				prefs.flush();
			}

			@Override
			public boolean keyTyped(char character) {
				playerName.append(character);
				return false;
			}

			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Input.Keys.BACKSPACE) {
					playerName.deleteCharAt(playerName.toString().length() - 1);
				}
				return false;
			}
		};
		Gdx.input.setInputProcessor(inputGingerman);
	}

	/**
	 * Cria o background da tela de instruções
	 */
	private TextureRegion initRankingBackGround() {
		Texture texture = new Texture(Gdx.files.internal(Assets.RANKING_IMAGE));
		TextureRegion bg = new TextureRegion(texture, Assets.ZERO, Assets.ZERO,
				WIDTH, HEIGHT);
		return bg;
	}

	/**
	 * Cria o background da tela de instruções
	 */
	private TextureRegion initInstructionsBackGround() {
		Texture texture = new Texture(
				Gdx.files.internal(Assets.INSTRUCTIONS_IMAGE));
		TextureRegion bg = new TextureRegion(texture, Assets.ZERO, Assets.ZERO,
				WIDTH, HEIGHT);
		return bg;
	}

	/**
	 * Cria o background da tela do jogo
	 */
	private TextureRegion initGameBackGround() {
		Texture texture = new Texture(
				Gdx.files.internal(Assets.BACKGROUND_IMAGE));
		TextureRegion bg = new TextureRegion(texture, Assets.ZERO, Assets.ZERO,
				WIDTH, HEIGHT);
		return bg;
	}

	/**
	 * Cria o background da tela de menu
	 */
	private TextureRegion initMenuBackGround() {
		Texture texture = new Texture(
				Gdx.files.internal(Assets.MENU_GINGER_BACKGROUND_IMAGE));
		TextureRegion bg = new TextureRegion(texture, Assets.ZERO, Assets.ZERO,
				WIDTH, HEIGHT);
		return bg;
	}

	/**
	 * Cria o background da tela de menu
	 */
	private TextureRegion initMenuMariaBackGround() {
		Texture texture = new Texture(
				Gdx.files.internal(Assets.MENU_MARIA_BACKGROUND_IMAGE));
		TextureRegion bg = new TextureRegion(texture, Assets.ZERO, Assets.ZERO,
				WIDTH, HEIGHT);
		return bg;
	}

	/**
	 * Cria o background da tela de menu
	 */
	private TextureRegion initMenuSoetBackGround() {
		Texture texture = new Texture(
				Gdx.files.internal(Assets.MENU_SOET_BACKGROUND_IMAGE));
		TextureRegion bg = new TextureRegion(texture, Assets.ZERO, Assets.ZERO,
				WIDTH, HEIGHT);
		return bg;
	}

	/**
	 * Cria o background da tela de menu
	 */
	private TextureRegion initMenuTarecoGround() {
		Texture texture = new Texture(
				Gdx.files.internal(Assets.MENU_TARECO_BACKGROUND_IMAGE));
		TextureRegion bg = new TextureRegion(texture, Assets.ZERO, Assets.ZERO,
				WIDTH, HEIGHT);
		return bg;
	}

	/**
	 * Cria o background da tela de menu
	 */
	private TextureRegion initMenuTrelosoGround() {
		Texture texture = new Texture(
				Gdx.files.internal(Assets.MENU_TRELOSO_BACKGROUND_IMAGE));
		TextureRegion bg = new TextureRegion(texture, Assets.ZERO, Assets.ZERO,
				WIDTH, HEIGHT);
		return bg;
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

		chooseCharLeft = new GameButton(6, 342, null, null, 0, 0);
		chooseCharRight = new GameButton(733, 342, null, null, 0, 0);
	}

	/**
	 * Carrega o personagem principal {@code gingerMan}.
	 */
	private void loadPlayers() {
		players[0] = new Gingerman();
		players[1] = new Maria();
		players[2] = new Negresco();
		players[3] = new Tareco();
		players[4] = new Treloso();
	}

	private void reviveAllPlayers() {
		players[0].revive();
		players[1].revive();
		players[2].revive();
		players[3].revive();
		players[4].revive();
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
			if (!gameOver) {
				renderDrops();
			}
		} else if (!readingInstructions) {
			renderButtons();
		}
	}

	/**
	 * Muda o estado da aplicação quando for presscionada a tecla BACK
	 */
	private void changeStateWhenTouchBack() {
		if (Gdx.input.isKeyPressed(Keys.BACK)
				|| Gdx.input.isKeyPressed(Keys.MENU)) {
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
				background = getCurrentMenuBackGround();
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
		chooseCharLeftButton();
		chooseCharRightButton();
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
	 * Muda a tela
	 */
	private void chooseCharLeftButton() {
		if (Gdx.input.justTouched()) {
			camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					Assets.ZERO));
			if (OverlapTester.pointInRectangle(chooseCharLeft.getBounds(),
					touchPoint.x, touchPoint.y)) {
				menuChoosed++;
				background = getCurrentMenuBackGround();
				reload();
				return;
			}
		}
	}

	/**
	 * Muda a tela
	 */
	private void chooseCharRightButton() {
		if (Gdx.input.justTouched()) {
			camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					Assets.ZERO));
			if (OverlapTester.pointInRectangle(chooseCharRight.getBounds(),
					touchPoint.x, touchPoint.y)) {
				menuChoosed--;
				background = getCurrentMenuBackGround();
				reload();
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
			background = getCurrentMenuBackGround();
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
				reload();
				state = State.Running;
				background = gameBG;
				return;
			}
		}
	}

	private void reload() {
		reviveAllPlayers();
		drops.clear();
		initMilis = TimeUtils.millis();
		totalTime = 0L;
		gameOver = false;
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
				Gdx.input.setOnscreenKeyboardVisible(!writing);
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
			font.draw(batch, LEFT_POINTS_NAME + getCurrentPlayer().getLife(),
					textAppearX, HEIGHT);
		}
		if (readingRanking) {
			printRanking();
		}
		batch.end();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		shape.setProjectionMatrix(camera.combined);
	}

	private PlayerChar getCurrentPlayer() {
		return players[Math.abs(menuChoosed) % 5];
	}

	private void printRanking() {
		int countWinners = 0;
		final int X_RANKING_TEXT = 300;
		final int Y_RANKING_TEXT = 375;
		final int Y_OFFSET = 56;
		@SuppressWarnings("unchecked")
		List<String> list = new ArrayList<String>(MapUtil.sortByValue(ranking)
				.keySet());
		Collections.reverse(list);
		for (String k : list) {
			if (countWinners == 5) {
				break;
			}
			font.draw(batch, k + ": " + ranking.get(k) + " s", X_RANKING_TEXT,
					Y_RANKING_TEXT - (countWinners * Y_OFFSET));
			countWinners++;
		}
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
			if (drop.overlaps(getCurrentPlayer())) {
				if (!muted) {
					final int VIBRATE_TIME = 500;
					if (drop.getDroppable().vibrateWhenOverlaps()) {
						Gdx.input.vibrate(VIBRATE_TIME);
					}
					drop.playSound();
				}
				getCurrentPlayer().update();
				if (!gameOver) {
					getCurrentPlayer().updateLife(
							drop.getDroppable().getModifyOfLife());
				}
				if (!getCurrentPlayer().isAlive()
						&& this.state != State.GameOver) {
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
		}
		if (getCurrentPlayer().x > WIDTH - getCurrentPlayer().width) {
			getCurrentPlayer().x = WIDTH - getCurrentPlayer().width;
		}
		if (getCurrentPlayer().x < Assets.ZERO) {
			getCurrentPlayer().x = Assets.ZERO;
		}
		if (!isPaused()) {
			renderLife();
		}

		batch.begin();
		batch.draw(getCurrentPlayer().getGingermanImage(),
				getCurrentPlayer().x, getCurrentPlayer().y);
		batch.end();

	}

	private void renderLife() {
		shape.begin(ShapeType.Filled);
		Rectangle visualRectangle = getCurrentPlayer().getVisualLifeRectangle();
		shape.setColor(getCurrentPlayer().getColor());
		shape.rect(visualRectangle.x, visualRectangle.y, visualRectangle.width,
				visualRectangle.height);
		shape.end();
	}

	/**
	 * Move o {@code gingerman} para o local tocado.
	 */
	private void moveGingerManToTouchLocal() {
		final int X_ACC = 200;
		Vector3 vector = new Vector3(Gdx.input.getX(), Gdx.input.getY(),
				Assets.ZERO);
		camera.unproject(vector);
		if (getCurrentPlayer().x < vector.x) {
			getCurrentPlayer().x += X_ACC * Gdx.graphics.getDeltaTime();
			if (getCurrentPlayer().x > vector.x) {
				getCurrentPlayer().x = vector.x;
			}
		} else if (getCurrentPlayer().x > vector.x) {
			getCurrentPlayer().x -= X_ACC * Gdx.graphics.getDeltaTime();
			if (getCurrentPlayer().x < vector.x) {
				getCurrentPlayer().x = vector.x;
			}
		} else {
			clicked = false;
		}
	}

	@Override
	public void dispose() {
		drops.clear();
		crankDance.dispose();
		batch.dispose();
		getCurrentPlayer().getGingermanImage().dispose();
		SugarDrop.getInstance().dispose();
		RainDrop.getInstance().dispose();
		RainDropLarge.getInstance().dispose();
		Jellybean.getInstance().dispose();
		background.getTexture().dispose();
	}
}
