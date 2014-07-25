package control;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public interface Droppable {

	/**
	 * Apaga o objeto da tela
	 */
	void dispose();

	/**
	 * Retorna o quanto o personagem deve modificar ao tocar no objeto
	 */
	int getModifyOfLife();

	/**
	 * Retorna a imagem do objeto
	 */
	Texture getDropImage();

	/**
	 * Retorna o som do objeto.
	 */
	Sound getDropSound();

}
