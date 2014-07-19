package control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class SugarDrop implements Droppable {
	private Texture sugarImage;
	private Sound sugarSound;

	public SugarDrop() {
		sugarImage = new Texture(Gdx.files.internal(Assets.SUGAR_DROP_IMAGE));
		sugarSound = Gdx.audio.newSound(Gdx.files
				.internal(Assets.SUGAR_DROP_SOUND));
	}

	@Override
	public int getModifyOfLife() {
		return -1;
	}

	@Override
	public Texture getDropImage() {
		return this.getSugarImage();
	}

	@Override
	public Sound getDropSound() {
		return this.getSugarSound();
	}

	public Texture getSugarImage() {
		return sugarImage;
	}

	public void setSugarImage(Texture sugarImage) {
		this.sugarImage = sugarImage;
	}

	public Sound getSugarSound() {
		return sugarSound;
	}

	public void setSugarSound(Sound sugarSound) {
		this.sugarSound = sugarSound;
	}
}
