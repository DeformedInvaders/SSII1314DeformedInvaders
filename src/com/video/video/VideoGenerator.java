package com.video.video;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.android.storage.AssetsStorageManager;
import com.game.data.Personaje;
import com.main.model.GamePreferences;
import com.main.model.GameResources;
import com.project.main.R;
import com.video.data.ObjetoAnimado;
import com.video.data.ObjetoInanimado;
import com.video.data.TTipoActores;
import com.video.data.TTipoAnimacion;
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
		int[] mensajeOutside = {R.string.dialog_outside_1};
		int[] mensajeNoise = {R.string.dialog_noise_1};
		int[] mensajeBrief = {R.string.dialog_brief_1, R.string.dialog_brief_2, R.string.dialog_brief_3, R.string.dialog_brief_4, R.string.dialog_brief_5};
		
		int[] idFondos = new int[GamePreferences.NUM_TYPE_BACKGROUNDS_VIDEO];
		for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS_VIDEO; i++)
		{
			idFondos[i] = obtenerID(GameResources.GET_VIDEO(i));
		}

		List<Personaje> listaPersonajes = new ArrayList<Personaje>();
		
		listaPersonajes.add(assetsManager.importarActor(TTipoActores.Guitarrista));
		listaPersonajes.add(assetsManager.importarActor(TTipoActores.Cientifico));
		
		List<ObjetoInanimado> listaObjetos = new ArrayList<ObjetoInanimado>();
		
		int[] texturasAgua = {R.drawable.video_water_1, R.drawable.video_water_2, R.drawable.video_water_3, R.drawable.video_water_4};
		listaObjetos.add(new ObjetoAnimado(0, texturasAgua, 120.0f, 20.0f, TEstadoVideo.Brief, R.raw.effect_video_water, TTipoAnimacion.Ciclico));
		
		int[] texturasEletricidad = {R.drawable.video_electricity_1, R.drawable.video_electricity_2, R.drawable.video_electricity_3, R.drawable.video_electricity_4};
		listaObjetos.add(new ObjetoAnimado(1, texturasEletricidad, 970.0f, 20.0f, TEstadoVideo.Brief, R.raw.effect_video_electricity, TTipoAnimacion.Ciclico));
		
		listaObjetos.add(new ObjetoInanimado(0, R.drawable.video_microphone_1, 250.0f, 20.0f, TEstadoVideo.Rock, R.raw.effect_video_microphone));
		listaObjetos.add(new ObjetoInanimado(1, R.drawable.video_speaker_1, 870.0f, 20.0f, TEstadoVideo.Rock, R.raw.effect_video_speaker));
		
		int[] texturasPuerta = {R.drawable.video_door_1, R.drawable.video_door_2, R.drawable.video_door_3, R.drawable.video_door_4};
		listaObjetos.add(new ObjetoAnimado(2, texturasPuerta, 498.0f, 110.0f, TEstadoVideo.Door, R.raw.effect_video_knockdoor, TTipoAnimacion.Pasos));
		
		video = new Video(idFondos, mensajeOutside, mensajeNoise, mensajeBrief, listaPersonajes, listaObjetos);
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
