package com.android.alert;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.game.data.Entidad;
import com.project.main.GamePreferences;
import com.project.main.R;

public class SummaryAlert extends WindowAlert
{	
	private static final int sizeText = 30;
	private static final int sizeTextSmall = 20;
	
	public SummaryAlert(Context context, String title, String textYes, List<Entidad> listaEnemigos)
	{
		super(context, title);
		
		Typeface textFont = Typeface.createFromAsset(context.getAssets(), GamePreferences.FONT_TYPEWRITER_PATH);

		RelativeLayout layoutFolder = new RelativeLayout(context);

			ImageView imageFolder = new ImageView(context);
			imageFolder.setImageResource(R.drawable.polaroid_folder_mission);
			
			ScrollView scrollFolder = new ScrollView(context);
			
			LinearLayout layoutScroll = new LinearLayout(context);
			layoutScroll.setOrientation(LinearLayout.VERTICAL);
			
				// Inicio Mensaje
				TextView textInitial = new TextView(context);
				textInitial.setText(context.getString(R.string.title_alert_initial) + "\n");
				textInitial.setTextSize(sizeText);
				textInitial.setTypeface(textFont);
			
				// Enemigos
				LinearLayout layoutImagesEnemigos = new LinearLayout(context);
				LinearLayout layoutEnemigos = construirLayoutSeccion(context, textFont, layoutImagesEnemigos, R.string.title_alert_enemies, R.string.title_alert_weaknesses_enemies);
					
				// Obstaculos
				
				LinearLayout layoutImagesObstaculos = new LinearLayout(context);
				LinearLayout layoutObstaculos = construirLayoutSeccion(context, textFont, layoutImagesObstaculos, R.string.title_alert_obstacles, R.string.title_alert_weaknesses_obstacles);
					
				// Misiles
				
				LinearLayout layoutImagesMisiles = new LinearLayout(context);
				LinearLayout layoutMisiles = construirLayoutSeccion(context, textFont, layoutImagesMisiles, R.string.title_alert_missiles, R.string.title_alert_weaknesses_missiles);
				
				// Procesamiento
					
				Iterator<Entidad> it = listaEnemigos.iterator();
				while(it.hasNext())
				{
					Entidad enemigo = it.next();
					
					ImageView imageView = new ImageView(context);
					imageView.setLayoutParams(new LinearLayout.LayoutParams(GamePreferences.PICTURE_ENEMY_WIDTH(), GamePreferences.PICTURE_ENEMY_WIDTH()));
					imageView.setBackgroundResource(enemigo.getIndiceTextura());
					
					switch(enemigo.getTipo())
					{
						case Enemigo:
							layoutImagesEnemigos.addView(imageView);
						break;
						case Obstaculo:
							layoutImagesObstaculos.addView(imageView);
						break;
						case Misil:
							imageView.setLayoutParams(new LinearLayout.LayoutParams(GamePreferences.PICTURE_ENEMY_WIDTH(), GamePreferences.PICTURE_ENEMY_WIDTH() / 2));
							layoutImagesMisiles.addView(imageView);
						default:
						break;
					}
				}
				
				// Mensaje Final
				TextView textFinal = new TextView(context);
				textFinal.setText("\n" + context.getString(R.string.title_alert_final));
				textFinal.setTextSize(sizeText);
				textFinal.setTypeface(textFont);
				
			layoutScroll.addView(textInitial);
			layoutScroll.addView(layoutEnemigos);
			layoutScroll.addView(layoutObstaculos);
			layoutScroll.addView(layoutMisiles);
			layoutScroll.addView(textFinal);

		scrollFolder.addView(layoutScroll);
			
		layoutFolder.addView(imageFolder, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutFolder.addView(scrollFolder, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		setView(layoutFolder);
		
		imageFolder.measure(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		int imageFolderWidth = imageFolder.getMeasuredHeight();
		int imageFolderHeight = imageFolder.getMeasuredWidth();
		
		scrollFolder.setPadding(imageFolderWidth / 8, imageFolderHeight / 6, imageFolderWidth / 20, imageFolderHeight / 20);
		
		setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) { }
		});
	}
	
	private LinearLayout construirLayoutSeccion(Context context, Typeface textFont, LinearLayout imagesLayout, int title, int title_weaknesses)
	{
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		
			TextView text = new TextView(context);
			text.setText(title);
			text.setTextSize(sizeText);
			text.setTypeface(textFont);
			
			TextView textWeaknesses = new TextView(context);
			textWeaknesses.setText(title_weaknesses);
			textWeaknesses.setTextSize(sizeTextSmall);
			textWeaknesses.setTypeface(textFont);
			
			imagesLayout.setOrientation(LinearLayout.VERTICAL);
			
		layout.addView(text);
		layout.addView(textWeaknesses);
		layout.addView(imagesLayout);
		
		return layout;
	}
}
