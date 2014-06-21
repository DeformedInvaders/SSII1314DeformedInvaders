package com.android.alert;

import java.util.Iterator;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.game.data.Entity;
import com.game.data.InstanceLevel;
import com.game.data.Boss;
import com.main.model.GamePreferences;
import com.main.model.GameResources;
import com.project.main.R;

public abstract class SummaryAlert extends WindowAlert
{	
	private static final int sizeText = 30;
	private static final int sizeTextSmall = 20;
	
	public SummaryAlert(Context context, int title, int textYes, InstanceLevel nivel)
	{
		super(context, title, false);
		
		Typeface textFont = Typeface.createFromAsset(context.getAssets(), GameResources.FONT_TYPEWRITER_PATH);

		setView(R.layout.alert_summary_layout);
		
		ImageView imageFolder = (ImageView) findViewById(R.id.imageViewSummaryAlert1);
		imageFolder.measure(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		int imageFolderWidth = imageFolder.getMeasuredHeight();
		int imageFolderHeight = imageFolder.getMeasuredWidth();
		
		TextView textBegin = (TextView) findViewById(R.id.textViewSummaryAlert1);
		TextView textEnd = (TextView) findViewById(R.id.textViewSummaryAlert2);
		textBegin.setTypeface(textFont);
		textEnd.setTypeface(textFont);
		
		ScrollView scrollLayout = (ScrollView) findViewById(R.id.scrollLayoutSummaryAlert1);
		scrollLayout.setPadding(imageFolderWidth / 8, imageFolderHeight / 6, imageFolderWidth / 20, imageFolderHeight / 20);

		LinearLayout layoutScroll = (LinearLayout) findViewById(R.id.linearLayoutSummaryAlert1);
	
			// Enemies
		
			LinearLayout layoutImagesEnemies = new LinearLayout(context);
			LinearLayout layoutEnemies = buildLayoutSection(context, textFont, layoutImagesEnemies, R.string.title_alert_enemies, R.string.title_alert_weaknesses_enemies);
				
			// Obstacles
			
			LinearLayout layoutImagesObstacles = new LinearLayout(context);
			LinearLayout layoutObstacles = buildLayoutSection(context, textFont, layoutImagesObstacles, R.string.title_alert_obstacles, R.string.title_alert_weaknesses_obstacles);
				
			// Missiles
			
			LinearLayout layoutImagesMissiles = new LinearLayout(context);
			LinearLayout layoutMissiles = buildLayoutSection(context, textFont, layoutImagesMissiles, R.string.title_alert_missiles, R.string.title_alert_weaknesses_missiles);
	
			// Boss
			
			LinearLayout layoutImagesBoss = new LinearLayout(context);
			LinearLayout layoutBoss = buildLayoutSection(context, textFont, layoutImagesBoss, R.string.title_alert_boss, R.string.title_alert_weaknesses_boss);
	
			// Procesamiento
				
			Iterator<Entity> it = nivel.getEnemyType().iterator();
			while(it.hasNext())
			{
				Entity enemy = it.next();
				
				ImageView imageView = new ImageView(context);
				imageView.setLayoutParams(new LinearLayout.LayoutParams(GamePreferences.PICTURE_ENEMY_WIDTH(), GamePreferences.PICTURE_ENEMY_WIDTH()));
				imageView.setBackgroundResource(enemy.getIndexTexture());
				
				switch(enemy.getType())
				{
					case Enemy:
						layoutImagesEnemies.addView(imageView);
					break;
					case Obstacle:
						layoutImagesObstacles.addView(imageView);
					break;
					case Missil:
						imageView.setLayoutParams(new LinearLayout.LayoutParams(GamePreferences.PICTURE_ENEMY_WIDTH(), GamePreferences.PICTURE_ENEMY_WIDTH() / 2));
						layoutImagesMissiles.addView(imageView);
					default:
					break;
				}
			}
			
			Boss jefe = nivel.getBoss();
			
			ImageView imageView = new ImageView(context);
			imageView.setLayoutParams(new LinearLayout.LayoutParams(GamePreferences.PICTURE_ENEMY_WIDTH(), GamePreferences.PICTURE_ENEMY_WIDTH()));
			imageView.setBackgroundResource(jefe.getIndexTexture());
			layoutImagesBoss.addView(imageView);
			
		layoutScroll.addView(layoutEnemies);
		layoutScroll.addView(layoutObstacles);
		layoutScroll.addView(layoutMissiles);
		layoutScroll.addView(layoutBoss);
		
		setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onPossitiveButtonClick();
			}
		});
	}
	
	private LinearLayout buildLayoutSection(Context context, Typeface textFont, LinearLayout imagesLayout, int title, int title_weaknesses)
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
