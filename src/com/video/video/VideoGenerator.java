package com.video.video;

import android.content.Context;

import com.android.storage.AssetsStorageManager;
import com.game.data.Personaje;
import com.main.model.GamePreferences;
import com.main.model.GameResources;
import com.video.data.TTipoActores;
import com.video.data.Video;

public class VideoGenerator
{
	private Context mContext;

	private Video video;
	
	private AssetsStorageManager assetsManager;
	
	public VideoGenerator(Context context, AssetsStorageManager manager)
	{
		mContext = context;
		assetsManager = manager;
	}
	
	public void cargarVideo()
	{
		int[] idFondos = new int[GamePreferences.NUM_TYPE_BACKGROUNDS_VIDEO];
		for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS_VIDEO; i++)
		{
			idFondos[i] = obtenerID(GameResources.GET_VIDEO(i));
		}

		Personaje guitarrista = assetsManager.importarActor(TTipoActores.Guitarrista);
		Personaje cientifico = assetsManager.importarActor(TTipoActores.Cientifico);
		
		video = new Video(idFondos, guitarrista, cientifico);
	}
	
	private int obtenerID(String id)
	{
		return mContext.getResources().getIdentifier(id, GameResources.RESOURCE_DRAWABLE, mContext.getPackageName());
	}
	
	public Video getVideo()
	{
		return video;
	}
}
