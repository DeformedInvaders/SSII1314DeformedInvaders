package com.video.video;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.android.storage.AssetsStorageManager;
import com.game.data.Character;
import com.main.model.GamePreferences;
import com.main.model.GameResources;
import com.project.main.R;
import com.video.data.AnimatedObject;
import com.video.data.InanimatedObject;
import com.video.data.MovingObject;
import com.video.data.TTypeActors;
import com.video.data.TTypeAnimation;
import com.video.data.Video;

public class VideoGenerator
{
	private Context mContext;	
	private AssetsStorageManager assetsManager;
	
	private int[] listaFondos;
	private List<int[]> listaMensajes;
	private List<Character> listaPersonajes;
	private List<InanimatedObject> listaObjetos;
	
	
	public VideoGenerator(Context context, AssetsStorageManager manager)
	{
		mContext = context;
		assetsManager = manager;
	}
	
	public void cargarVideo()
	{
		// Mensajes
		
		listaMensajes = new ArrayList<int[]>();
		
		listaMensajes.add(null);
		
		int[] mensajeOutside = {R.string.dialog_outside_1};
		listaMensajes.add(mensajeOutside);
		
		int[] mensajeDoor = {R.string.dialog_door_1};
		listaMensajes.add(mensajeDoor);
		
		int[] mensajeRock = {R.string.dialog_rock_1};
		listaMensajes.add(mensajeRock);
		
		int[] mensajeNoise = {R.string.dialog_noise_1};
		listaMensajes.add(mensajeNoise);
		
		int[] mensajeBrief = {R.string.dialog_brief_1, R.string.dialog_brief_2, R.string.dialog_brief_3, R.string.dialog_brief_4, R.string.dialog_brief_5};
		listaMensajes.add(mensajeBrief);
		
		// Fondos
		
		listaFondos = new int[GamePreferences.NUM_TYPE_BACKGROUNDS_VIDEO];
		for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS_VIDEO; i++)
		{
			listaFondos[i] = obtenerID(GameResources.GET_VIDEO(i));
		}
		
		// Actores

		listaPersonajes = new ArrayList<Character>();
		
		listaPersonajes.add(assetsManager.importActor(TTypeActors.Guitarist));
		listaPersonajes.add(assetsManager.importActor(TTypeActors.Scientific));
		
		// Objetos
		
		listaObjetos = new ArrayList<InanimatedObject>();
		
		int[] texturasAgua = {R.drawable.video_water_1, R.drawable.video_water_2, R.drawable.video_water_3, R.drawable.video_water_4};
		listaObjetos.add(new AnimatedObject(0, texturasAgua, 120.0f, 20.0f, TStateVideo.Brief, R.raw.effect_video_water, TTypeAnimation.Pressed));
		
		int[] texturasEletricidad = {R.drawable.video_electricity_1, R.drawable.video_electricity_2, R.drawable.video_electricity_3, R.drawable.video_electricity_4};
		listaObjetos.add(new AnimatedObject(1, texturasEletricidad, 970.0f, 20.0f, TStateVideo.Brief, R.raw.effect_video_electricity, TTypeAnimation.Pressed));
		
		listaObjetos.add(new InanimatedObject(0, R.drawable.video_microphone_1, 250.0f, 20.0f, TStateVideo.Rock, R.raw.effect_video_microphone));
		listaObjetos.add(new InanimatedObject(1, R.drawable.video_speaker_1, 870.0f, 20.0f, TStateVideo.Rock, R.raw.effect_video_speaker));
		
		int[] texturasPuerta = {R.drawable.video_door_1, R.drawable.video_door_2, R.drawable.video_door_3, R.drawable.video_door_4};
		listaObjetos.add(new AnimatedObject(2, texturasPuerta, 473.0f, 0.0f, TStateVideo.Door, R.raw.effect_video_knockdoor, TTypeAnimation.Step));
		
		int [] texturasPolvo = {R.drawable.video_dust_1, R.drawable.video_dust_2, R.drawable.video_dust_3, R.drawable.video_dust_4};
		listaObjetos.add(new AnimatedObject(3, texturasPolvo, 390.0f, 0.0f, TStateVideo.Noise, -1, TTypeAnimation.Cyclic));
		
		int [] texturasNave = {R.drawable.video_spaceship_1, R.drawable.video_spaceship_2, R.drawable.video_spaceship_3, R.drawable.video_spaceship_4};
		listaObjetos.add(new MovingObject(4, texturasNave, 500.0f, -500.0f, TStateVideo.Spaceship, -1, TTypeAnimation.Cyclic));
	}
	
	private int obtenerID(String id)
	{
		return mContext.getResources().getIdentifier(id, GameResources.RESOURCE_DRAWABLE, mContext.getPackageName());
	}
	
	public Video getVideo()
	{
		Iterator<InanimatedObject> it = listaObjetos.iterator();
		while(it.hasNext())
		{
			it.next().stopAnimation();
		}
		
		return new Video(listaFondos, listaMensajes, listaPersonajes, listaObjetos);
	}
}
