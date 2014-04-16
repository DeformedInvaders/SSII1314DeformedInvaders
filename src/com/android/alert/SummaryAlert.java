package com.android.alert;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.game.data.Entidad;
import com.project.main.R;
import com.project.model.GamePreferences;
import com.project.model.GameResources;

public abstract class SummaryAlert extends WindowAlert
{	
	private static final int sizeText = 30;
	private static final int sizeTextSmall = 20;
	
	public SummaryAlert(Context context, int title, int textYes, List<Entidad> listaEnemigos)
	{
		super(context, title, false);
		
		Typeface textFont = Typeface.createFromAsset(context.getAssets(), GameResources.FONT_TYPEWRITER_PATH);

		setView(R.layout.alert_summary_layout);
		
		ImageView imageFolder = (ImageView) findViewById(R.id.imageViewSummaryAlert1);
		imageFolder.measure(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		int imageFolderWidth = imageFolder.getMeasuredHeight();
		int imageFolderHeight = imageFolder.getMeasuredWidth();
		
		TextView textoInicial = (TextView) findViewById(R.id.textViewSummaryAlert1);
		TextView textoFinal = (TextView) findViewById(R.id.textViewSummaryAlert2);
		textoInicial.setTypeface(textFont);
		textoFinal.setTypeface(textFont);
		
		ScrollView scrollLayout = (ScrollView) findViewById(R.id.scrollLayoutSummaryAlert1);
		scrollLayout.setPadding(imageFolderWidth / 8, imageFolderHeight / 6, imageFolderWidth / 20, imageFolderHeight / 20);

		LinearLayout layoutScroll = (LinearLayout) findViewById(R.id.linearLayoutSummaryAlert1);
	
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
		
		layoutScroll.addView(layoutEnemigos);
		layoutScroll.addView(layoutObstaculos);
		layoutScroll.addView(layoutMisiles);
		
		setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onPossitiveButtonClick();
			}
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
	
	/* Métodos Abstractos */

	public abstract void onPossitiveButtonClick();
}
