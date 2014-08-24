package control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import control.ui.utils.Assets;

/**
 * Classe que representa uma gota de Chuva.
 */
public class Jellybean implements Droppable {
	private Sound jellyDropSound;
	private static Jellybean instance;
	private Texture rainDropImage;

	private Jellybean() {
		jellyDropSound = Gdx.audio.newSound(Gdx.files
				.internal(Assets.JELLY_DROP_SOUND));
		rainDropImage = new Texture(
				Gdx.files.internal(Assets.JELLY_DROP_IMAGE_LARGE));
	}

	public static Jellybean getInstance() {
		if (instance == null) {
			instance = new Jellybean();
		}
		return instance;
	}

	@Override
	public int getModifyOfLife() {
		return Gingerman.MAXIMUM_LIFE;
	}

	@Override
	public Texture getDropImage() {
		return this.getJellyDropImage();
	}

	@Override
	public Sound getDropSound() {
		return this.getJellyDropSound();
	}

	public void setDropSound(Sound dropSound) {
		this.setJellyDropSound(dropSound);
	}

	public Sound getJellyDropSound() {
		return jellyDropSound;
	}

	public void setJellyDropSound(Sound jellyDropSound) {
		this.jellyDropSound = jellyDropSound;
	}

	public Texture getJellyDropImage() {
		return rainDropImage;
	}

	public void setJellyDropImage(Texture jellyDropImage) {
		this.rainDropImage = jellyDropImage;
	}

	@Override
	public void dispose() {
		getJellyDropImage().dispose();
	}
}
