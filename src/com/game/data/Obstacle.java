package com.game.data;

public class Obstacle extends Rectangle
{
	/* Constructora */

	public Obstacle(int texture, int id)
	{
		typeEntity = TTypeEntity.Obstacle;
		idEntity = id;
		textureEntity = texture;
	}
}
