package com.game.data;

public class Boss extends Enemy
{
	public Boss(int texture, int id)
	{
		super(texture, id);
		
		typeEntity = TTypeEntity.Boss;
	}
}
