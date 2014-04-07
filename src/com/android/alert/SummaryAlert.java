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
import com.project.main.R;

public class SummaryAlert extends WindowAlert
{
	private static final int paddingLeft = 75;
	private static final int paddingTop = 100;
	private static final int paddingRight = 30;
	private static final int paddingBottom = 30;
	
	private static final int imageHeight = 250;
	private static final int imageWidth = 250;
	
	private static final int sizeText = 30;
	private static final int sizeTextSmall = 20;
	
	public SummaryAlert(Context context, String title, String textYes, List<Entidad> listaEnemigos)
	{
		super(context, title);
		
		Typeface textFont = Typeface.createFromAsset(context.getAssets(), "fonts/font_typewriter.ttf");

		RelativeLayout layoutFolder = new RelativeLayout(context);

			ImageView imageFolder = new ImageView(context);
			imageFolder.setImageResource(R.drawable.polaroid_folder_mission);
			
			ScrollView scrollFolder = new ScrollView(context);
			scrollFolder.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
			
			LinearLayout layoutScroll = new LinearLayout(context);
			layoutScroll.setOrientation(LinearLayout.VERTICAL);
			
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
					ImageView imageView = new ImageView(context);
					imageView.setLayoutParams(new LinearLayout.LayoutParams(imageHeight, imageWidth));
					
					Entidad enemigo = it.next();
					switch(enemigo.getTipo())
					{
						case Enemigo:
							imageView.setBackgroundResource(enemigo.getIndiceTextura());
							layoutImagesEnemigos.addView(imageView);
						break;
						case Obstaculo:
							imageView.setBackgroundResource(enemigo.getIndiceTextura());
							layoutImagesObstaculos.addView(imageView);
						break;
						default:
						break;
					}
				}
				
			layoutScroll.addView(layoutEnemigos);
			layoutScroll.addView(layoutObstaculos);
			layoutScroll.addView(layoutMisiles);

		scrollFolder.addView(layoutScroll);
			
		layoutFolder.addView(imageFolder, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutFolder.addView(scrollFolder, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		setView(layoutFolder);
		
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
