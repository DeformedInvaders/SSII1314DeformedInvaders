package com.project.model;

import java.io.Serializable;

public class GameStatistics implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int maxScore, numDeaths, numVictories;
	private boolean unlocked, completed, perfected;
	
	public GameStatistics()
	{
		maxScore = 0;
		numDeaths = 0;
		numVictories = 0;
		unlocked = false;
		completed = false;
		perfected = false;
	}
	
	public int getMaxScore()
	{
		return maxScore;
	}
	
	public int getNumDeaths()
	{
		return numDeaths;
	}
	
	public int getNumVictories()
	{
		return numVictories;
	}
	
	public boolean isUnlocked()
	{
		return unlocked;
	}
	
	public boolean isCompleted()
	{
		return completed;
	}
	
	public boolean isPerfected()
	{
		return perfected;
	}

	public void setMaxScore(int score)
	{
		if(score > maxScore)
		{
			maxScore = score;
		}
	}

	public void increaseNumDeaths()
	{
		numDeaths++;
	}

	public void increaseVictories()
	{
		numVictories++;
	}

	public void setUnlocked()
	{
		unlocked = true;
	}

	public void setCompleted()
	{
		completed = true;
	}

	public void setPerfected()
	{
		perfected = true;
	}	
}
