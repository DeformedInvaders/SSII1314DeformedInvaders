package com.game.data;

public class Missil extends Rectangle
{
	/* Constructora */

	public Missil(int texture, int id)
	{
		typeEntity = TTypeEntity.Missil;
		idEntity = id;
		textureEntity = texture;
	}
}
