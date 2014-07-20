package com.mygdx.game;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import control.RainDropLarge;
import control.SugarDrop;

/**
 * Classe principal do Jogo
 */
public class Biscoitinho extends ApplicationAdapter {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;

	private boolean clicked;

	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Array<DropObject> drops;
	private long lastDropTime;
	private BitmapFont font;
	private TextureRegion region;
	private Gingerman gingerMan;

	private Music crankDance;

	@Override
	public void create() {
		font = new BitmapFont();
		font.scale(2.5f);
		font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		batch = new SpriteBatch();
		drops = new Array<DropObject>();
		loadCamera();
		loadSoundAndMusics();
		loadElements();
		spawnDrop();
		Texture texture = new Texture(Gdx.files.internal("bg.jpg"));
		region = new TextureRegion(texture, 0, 0, WIDTH, HEIGHT);
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
		batch.begin();
		batch.draw(region, 0, 0);
		font.draw(batch, "Crashes :  " + gingerMan.getLife(), 25, 400);
		batch.end();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		renderPlayer();
		renderDrops();
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
		if (TimeUtils.nanoTime() - lastDropTime > 1000000) {
			spawnDrop();
		}
		Iterator<DropObject> it = drops.iterator();
		while (it.hasNext()) {
			DropObject drop = it.next();
			drop.y -= 200 * Gdx.graphics.getDeltaTime();
			if (drop.y + drop.height < 0) {
				it.remove();
			}
			if (drop.overlaps(gingerMan)) {
				gingerMan.setLife(drop.getDroppable().getModifyOfLife());
				drop.getDroppable().getDropSound().play();
				it.remove();
				gingerMan.update();
			}
		}
	}

	/**
	 * Renderiza o objeto escolhido em {@link Biscoitinho#initializeDrop}.
	 */
	private void spawnDrop() {
		DropObject drop = initializeDrop();
		drop.width = 5;
		drop.height = 6;
		drop.x = MathUtils.random(0, WIDTH - drop.width);
		drop.y = HEIGHT;
		drops.add(drop);
		lastDropTime = TimeUtils.nanoTime();
	}

	/**
	 * Inicializa qual tipo de objeto vai cair.
	 */
	private DropObject initializeDrop() {
		DropObject drop;
		if (getRandPercent(5)) {
			drop = new DropObject(SugarDrop.getInstance());
		} else if (getRandPercent(1)) {
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
		if (clicked) {
			moveGingerManToTouchLocal();
		} else {
			moveGingermanToAccelerometer();
		}
		batch.begin();
		batch.draw(gingerMan.getGingermanImage(), gingerMan.x, gingerMan.y);
		batch.end();

	}

	/**
	 * Move o {@code gingerman} de acordo com o acelerometro.
	 */
	private void moveGingermanToAccelerometer() {
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

	/**
	 * Move o {@code gingerman} para o local tocado.
	 */
	private void moveGingerManToTouchLocal() {
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
		crankDance.dispose();
		batch.dispose();
		gingerMan.getGingermanImage().dispose();
		SugarDrop.getInstance().dispose();
		RainDrop.getInstance().dispose();
		RainDropLarge.getInstance().dispose();
		region.getTexture().dispose();
	}
}
