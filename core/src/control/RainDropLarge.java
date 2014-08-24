package control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import control.ui.utils.Assets;

/**
 * Classe que representa uma gota de Chuva.
 */
public class RainDropLarge implements Droppable {
	private Sound rainDropSound;
	private static RainDropLarge instance;
	private Texture rainDropImage;

	private RainDropLarge() {
		rainDropSound = Gdx.audio.newSound(Gdx.files
				.internal(Assets.RAIN_DROP_SOUND_LARGE));
		rainDropImage = new Texture(
				Gdx.files.internal(Assets.RAIN_DROP_IMAGE_LARGE));
	}

	public static RainDropLarge getInstance() {
		if (instance == null) {
			instance = new RainDropLarge();
		}
		return instance;
	}

	@Override
	public int getModifyOfLife() {
		return -10;
	}

	@Override
	public Texture getDropImage() {
		return this.getRainDropImage();
	}

	@Override
	public Sound getDropSound() {
		return this.getRainDropSound();
	}

	public void setDropSound(Sound dropSound) {
		this.setRainDropSound(dropSound);
	}

	public Sound getRainDropSound() {
		return rainDropSound;
	}

	public void setRainDropSound(Sound rainDropSound) {
		this.rainDropSound = rainDropSound;
	}

	public Texture getRainDropImage() {
		return rainDropImage;
	}

	public void setRainDropImage(Texture rainDropImage) {
		this.rainDropImage = rainDropImage;
	}

	@Override
	public void dispose() {
		getRainDropImage().dispose();
	}

	@Override
	public boolean vibrateWhenOverlaps() {
		return true;
	}

}
