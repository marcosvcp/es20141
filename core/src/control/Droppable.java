package control;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public interface Droppable {

	void dispose();

	int getModifyOfLife();

	Texture getDropImage();

	Sound getDropSound();
}
