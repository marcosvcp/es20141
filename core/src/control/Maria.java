package control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import control.ui.utils.Assets;

public class Maria extends PlayerChar {
	private Texture gingermanImage;

	/**
	 * 
	 */
	private static final long serialVersionUID = -9185186586940085148L;

	public Maria() {
		super();
	}

	@Override
	public void initImages() {
		super.getCharImagens()[0] = new Texture(
				Gdx.files.internal(Assets.MARIA_IMAGE_ONE));
		super.getCharImagens()[1] = new Texture(
				Gdx.files.internal(Assets.MARIA_IMAGE_TWO));
		super.getCharImagens()[2] = new Texture(
				Gdx.files.internal(Assets.MARIA_IMAGE_THREE));
		super.getCharImagens()[3] = new Texture(
				Gdx.files.internal(Assets.MARIA_IMAGE_FOUR));
		super.getCharImagens()[4] = new Texture(
				Gdx.files.internal(Assets.MARIA_IMAGE_FIVE));
		gingermanImage = new Texture(
				Gdx.files.internal(Assets.MARIA_IMAGE_ONE));
	}

	@Override
	public Texture getGingermanImage() {
		return gingermanImage;
	}

	@Override
	public void update() {
		gingermanImage = super.getCharImagens()[Math.abs(getLife()) % 5];
	}
}
