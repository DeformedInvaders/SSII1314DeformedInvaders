package com.video.data;

import java.util.List;

import com.game.data.Character;
import com.video.video.TStateVideo;

public class Video
{
	private int[] listBackgrounds;
	private List<int[]> listQuotes;
	private List<Character> listActors;
	private List<InanimatedObject> listObjects;
	
	public Video(int[] backgrounds, List<int[]> quotes, List<Character> actors, List<InanimatedObject> objects)
	{
		listBackgrounds = backgrounds;
		listQuotes = quotes;
		listActors = actors;
		listObjects = objects;
	}
	
	public int[] getListBackgrounds()
	{
		return listBackgrounds;
	}
	
	public Character getActor(TTypeActors actor)
	{
		return listActors.get(actor.ordinal());
	}
	
	public List<InanimatedObject> getListObjects()
	{
		return listObjects;
	}
	
	public int[] getQuote(TStateVideo state)
	{
		if (state.ordinal() < listQuotes.size())
		{
			return listQuotes.get(state.ordinal());
		}
		
		return null;
	}
}
