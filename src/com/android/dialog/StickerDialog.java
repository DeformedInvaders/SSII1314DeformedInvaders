package com.android.dialog;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.creation.data.TTypeSticker;
import com.main.model.GamePreferences;
import com.main.model.GameResources;
import com.main.model.GameStatistics;
import com.project.main.R;

public abstract class StickerDialog extends WindowDialog
{
	/* Constructora */
	private int imageWidth, imageHeight;
	
	public StickerDialog(Context context, GameStatistics[] estadisticas)
	{
		super(context, R.layout.dialog_sticker_layout, true);
		
		imageWidth = (int) mContext.getResources().getDimension(R.dimen.StickerButton_LayoutWidth_Dimen);
		imageHeight = (int) mContext.getResources().getDimension(R.dimen.StickerButton_LayoutHeight_Dimen);
		
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.linearLayoutSticker1);
		LinearLayout[] stickerLayout = new LinearLayout[GamePreferences.NUM_TYPE_STICKERS];
		
		TTypeSticker[] pegatinas = TTypeSticker.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
		{
			stickerLayout[i] = buildStickerLayout(mainLayout, pegatinas[i]);
			buildSticker(stickerLayout[i], estadisticas, pegatinas[i], new OnAddStickerClickListener(pegatinas[i]));
		}
	}
	
	private LinearLayout buildStickerLayout(LinearLayout mainLayout, TTypeSticker tipo)
	{
		LinearLayout typeLayout = new LinearLayout(mContext);
		typeLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		typeLayout.setOrientation(LinearLayout.VERTICAL);
		typeLayout.setPadding(10, 0, 10, 0);
		
			TextView titulo = new TextView(mContext);
			titulo.setText(tipo.getTitle());
			titulo.setTextAppearance(mContext, R.style.Text_Section_Dialog_Style);
			
			ScrollView scroll = new ScrollView(mContext);
			scroll.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, 3 * imageHeight));
			
				LinearLayout layout = new LinearLayout(mContext);
				layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				layout.setOrientation(LinearLayout.VERTICAL);
			
			scroll.addView(layout);
			
			typeLayout.addView(titulo);
			typeLayout.addView(scroll);
		
		mainLayout.addView(typeLayout);
		
		return layout;
	}

	private void loadSticker(LinearLayout layout, int idImagen, int tagImagen, OnClickListener listener)
	{
		ImageView image = new ImageView(mContext);
		image.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));
		image.setBackgroundResource(idImagen);
		image.setOnClickListener(listener);
		image.setTag(tagImagen);
		
		layout.addView(image);		
	}
	
	private void loadSticker(LinearLayout layout, String nombrePegatina, int tagImagen, OnClickListener listener)
	{
		int idImagen = mContext.getResources().getIdentifier(nombrePegatina, GameResources.RESOURCE_DRAWABLE, mContext.getPackageName());
		loadSticker(layout, idImagen, tagImagen, listener);
	}
	
	private void buildSticker(LinearLayout layout, GameStatistics[] estadisticas, TTypeSticker tipo, OnClickListener listener)
	{
		boolean gameCompleted = true;
		boolean stickerAdded = false;
		
		for (int i = 0; i < estadisticas.length; i++)
		{
			gameCompleted &= estadisticas[i].isPerfected();
		}
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS(tipo); i++)
		{
			if (GamePreferences.IS_DEBUG_ENABLED())
			{
				loadSticker(layout, GameResources.GET_STICKER(tipo, i), i, listener);
				stickerAdded = true;
			}
			else if (tipo == TTypeSticker.Eyes || tipo == TTypeSticker.Mouth)
			{
				if (i < GamePreferences.NUM_TYPE_STICKERS(tipo) - 4)
				{
					// Pegatinas Básicas
					loadSticker(layout, GameResources.GET_STICKER(tipo, i), i, listener);
					stickerAdded = true;
				}
				else if(estadisticas[0].isPerfected())
				{
					// Pegatinas Nivel Luna
					loadSticker(layout, GameResources.GET_STICKER(tipo, i), i, listener);
					stickerAdded = true;
				}
			}
			else
			{
				if (i < GamePreferences.NUM_TYPE_STICKERS(tipo) - 2)
				{
					// Pegatinas Resto Niveles
					int pos = (i / (GamePreferences.NUM_TYPE_LEVELS - 1)) + 1;
					if (estadisticas[pos].isPerfected())
					{
						loadSticker(layout, GameResources.GET_STICKER(tipo, i), i, listener);
						stickerAdded = true;
					}
				}
				else if (gameCompleted)
				{
					// Pegatinas Personajes Video
					loadSticker(layout, GameResources.GET_STICKER(tipo, i), i, listener);
					stickerAdded = true;
				}
			}
		}
		
		// Opción Eliminar Pegatina
		if (stickerAdded)
		{
			loadSticker(layout, R.drawable.sticker_delete, 0, new OnDeleteStickerClickListener(tipo));
			loadSticker(layout, R.drawable.sticker_edit, 0, new OnEditStickerClickListener(tipo));
		}
	}

	/* Métodos Abstractos */

	public abstract void onAddSticker(int tag, TTypeSticker tipo);
	public abstract void onDeleteSticker(TTypeSticker tipo);
	public abstract void onEditSticker(TTypeSticker tipo);

	/* Métodos Listener onClick */
	
	public class OnAddStickerClickListener implements OnClickListener
	{
		private TTypeSticker typeSticker;
		
		public OnAddStickerClickListener(TTypeSticker type)
		{
			super();
			
			typeSticker = type;
		}
		
		@Override
		public void onClick(View v)
		{
			onAddSticker((Integer) v.getTag(), typeSticker);
			dismiss();
		}
	}
	
	public class OnDeleteStickerClickListener implements OnClickListener
	{
		private TTypeSticker typeSticker;
		
		public OnDeleteStickerClickListener(TTypeSticker type)
		{
			super();
			
			typeSticker = type;
		}
		
		@Override
		public void onClick(View v)
		{
			onDeleteSticker(typeSticker);
			dismiss();
		}		
	}
	
	public class OnEditStickerClickListener implements OnClickListener
	{
		private TTypeSticker typeSticker;
	
		public OnEditStickerClickListener(TTypeSticker type)
		{
			super();
			
			typeSticker = type;
		}
	
		@Override
		public void onClick(View v)
		{
			onEditSticker(typeSticker);
			dismiss();
		}		
	}
}