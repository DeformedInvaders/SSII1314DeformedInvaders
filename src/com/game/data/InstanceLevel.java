package com.game.data;

import java.util.List;

import com.game.select.TTypeLevel;

public class InstanceLevel
{
	private TTypeLevel levelType;
	private String levelName;

	private List<Entity> levelEnemyType;
	private List<InstanceEntity> levelEnemyList;
	private Boss levelBoss;
	private Background levelBackground;

	public InstanceLevel(TTypeLevel type, String name, List<Entity> enemyType, Boss boss, List<InstanceEntity> enemyList, Background background)
	{
		levelType = type;
		levelName = name;
		levelEnemyType = enemyType;
		levelEnemyList = enemyList;
		levelBoss = boss;
		levelBackground = background;
	}

	public TTypeLevel getLevelType()
	{
		return levelType;
	}

	public String getLevelName()
	{
		return levelName;
	}

	public List<Entity> getEnemyType()
	{
		return levelEnemyType;
	}
	
	public Boss getBoss()
	{
		return levelBoss;
	}

	public List<InstanceEntity> getEnemyList()
	{
		return levelEnemyList;
	}

	public Background getBackground()
	{
		return levelBackground;
	}
}
