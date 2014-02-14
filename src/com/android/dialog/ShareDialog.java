package com.android.dialog;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.project.main.R;
import com.test.social.SocialFragment;

public class ShareDialog extends WindowDialog
{
	private SocialFragment fragmento;
	
	private Button botonCompartir, botonCancelar;
	private EditText textoEstado;
	
	public ShareDialog(Context context, SocialFragment view)
	{
		super(context, R.layout.dialog_share_layout);
		
		fragmento = view;
		
		textoEstado = (EditText) findViewById(R.id.editTextShare1);
		
		botonCompartir = (Button) findViewById(R.id.imageButtonShare1);
		botonCancelar = (Button) findViewById(R.id.imageButtonShare2);
		
		botonCompartir.setOnClickListener(new OnShareCompartirClickListener());
		botonCancelar.setOnClickListener(new OnShareCancelarClickListener());
	}
	
	@Override
	protected void onTouchOutsidePopUp(View v, MotionEvent event)
	{
		dismiss();
	}
	
	private class OnShareCompartirClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			fragmento.publicar(textoEstado.getText().toString());
			dismiss();
		}
	}
	
	private class OnShareCancelarClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			dismiss();
		}
	}
}
