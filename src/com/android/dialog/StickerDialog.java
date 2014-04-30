package com.android.dialog;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.creation.data.TTipoSticker;
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
		super(context, R.layout.dialog_sticker_layout);
		
		imageWidth = (int) mContext.getResources().getDimension(R.dimen.StickerButton_LayoutWidth_Dimen);
		imageHeight = (int) mContext.getResources().getDimension(R.dimen.StickerButton_LayoutHeight_Dimen);
		
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.linearLayoutSticker1);
		LinearLayout[] stickerLayout = new LinearLayout[GamePreferences.NUM_TYPE_STICKERS];
		
		TTipoSticker[] pegatinas = TTipoSticker.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
		{
			stickerLayout[i] = construirLayout(mainLayout, pegatinas[i]);
			construirPegatinas(stickerLayout[i], estadisticas, pegatinas[i], new OnAddStickerClickListener(pegatinas[i]));
		}
	}
	
	private LinearLayout construirLayout(LinearLayout mainLayout, TTipoSticker tipo)
	{
		LinearLayout typeLayout = new LinearLayout(mContext);
		typeLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		typeLayout.setOrientation(LinearLayout.VERTICAL);
		
			TextView titulo = new TextView(mContext);
			titulo.setText(tipo.getTitle());
			titulo.setTextAppearance(mContext, R.style.Text_Section_Dialog_Style);
			
			ScrollView scroll = new ScrollView(mContext);
			scroll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 3 * imageHeight));
			
				LinearLayout layout = new LinearLayout(mContext);
				layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setPadding(10, 0, 10, 0);
			
			scroll.addView(layout);
			
			typeLayout.addView(titulo);
			typeLayout.addView(scroll);
		
		mainLayout.addView(typeLayout);
		
		return layout;
	}

	private void cargarPegatina(LinearLayout layout, int idImagen, int tagImagen, OnClickListener listener)
	{
		ImageView image = new ImageView(mContext, null, R.style.Button_Fragment_Style);
		image.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));
		image.setBackgroundResource(idImagen);
		image.setOnClickListener(listener);
		image.setTag(tagImagen);
		
		layout.addView(image);		
	}
	
	private void cargarPegatina(LinearLayout layout, String nombrePegatina, int tagImagen, OnClickListener listener)
	{
		int idImagen = mContext.getResources().getIdentifier(nombrePegatina, GameResources.RESOURCE_DRAWABLE, mContext.getPackageName());
		cargarPegatina(layout, idImagen, tagImagen, listener);
	}
	
	private void construirPegatinas(LinearLayout layout, GameStatistics[] estadisticas, TTipoSticker tipo, OnClickListener listener)
	{
		boolean pegatinaAnyiadida = false;
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS(tipo); i++)
		{
			if (tipo == TTipoSticker.Eyes || tipo == TTipoSticker.Mouth)
			{
				if (i < GamePreferences.NUM_TYPE_STICKERS(tipo) - 4)
				{
					// Pegatinas Básicas
					cargarPegatina(layout, GameResources.GET_STICKER(tipo, i), i, listener);
					pegatinaAnyiadida = true;
				}
				else if(estadisticas[0].isPerfected())
				{
					// Pegatinas Nivel Luna
					cargarPegatina(layout, GameResources.GET_STICKER(tipo, i), i, listener);
					pegatinaAnyiadida = true;
				}
			}
			else
			{
				// Pegatinas Resto Niveles
				int pos = (i / (GamePreferences.NUM_TYPE_LEVELS - 1)) + 1;
				if (estadisticas[pos].isPerfected())
				{
					cargarPegatina(layout, GameResources.GET_STICKER(tipo, i), i, listener);
					pegatinaAnyiadida = true;
				}
			}
		}
		
		// Opción Eliminar Pegatina
		if (pegatinaAnyiadida)
		{
			cargarPegatina(layout, R.drawable.sticker_delete, 0, new OnDeleteStickerClickListener(tipo));
			cargarPegatina(layout, R.drawable.sticker_edit, 0, new OnEditStickerClickListener(tipo));
		}
	}

	/* Métodos Abstractos */

	public abstract void onAddSticker(int tag, TTipoSticker tipo);
	public abstract void onDeleteSticker(TTipoSticker tipo);
	public abstract void onEditSticker(TTipoSticker tipo);

	/* Métodos Abstractos WindowDialog */

	@Override
	protected void onTouchOutsidePopUp(View v, MotionEvent event) { }

	/* Métodos Listener onClick */
	
	public class OnAddStickerClickListener implements OnClickListener
	{
		private TTipoSticker tipoPegatina;
		
		public OnAddStickerClickListener(TTipoSticker tipo)
		{
			super();
			
			tipoPegatina = tipo;
		}
		
		@Override
		public void onClick(View v)
		{
			onAddSticker((Integer) v.getTag(), tipoPegatina);
			dismiss();
		}
	}
	
	public class OnDeleteStickerClickListener implements OnClickListener
	{
		private TTipoSticker tipoPegatina;
		
		public OnDeleteStickerClickListener(TTipoSticker tipo)
		{
			super();
			
			tipoPegatina = tipo;
		}
		
		@Override
		public void onClick(View v)
		{
			onDeleteSticker(tipoPegatina);
			dismiss();
		}		
	}
	
	public class OnEditStickerClickListener implements OnClickListener
	{
		private TTipoSticker tipoPegatina;
	
		public OnEditStickerClickListener(TTipoSticker tipo)
		{
			super();
			
			tipoPegatina = tipo;
		}
	
		@Override
		public void onClick(View v)
		{
			onEditSticker(tipoPegatina);
			dismiss();
		}		
	}
}