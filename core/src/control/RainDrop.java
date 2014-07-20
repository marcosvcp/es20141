package control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class RainDrop implements Droppable {
	private Sound rainDropSound;
	private static RainDrop instance;
	private Texture rainDropImage;

	private RainDrop() {
		rainDropSound = Gdx.audio.newSound(Gdx.files
				.internal(Assets.RAIN_DROP_SOUND));
		rainDropImage = new Texture(
				Gdx.files.internal(Assets.RAIN_DROP_IMAGE_SMALL));
	}

	public static RainDrop getInstance() {
		if (instance == null) {
			instance = new RainDrop();
		}
		return instance;
	}

	@Override
	public int getModifyOfLife() {
		return 1;
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
}
