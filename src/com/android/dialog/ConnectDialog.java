package com.android.dialog;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.project.main.R;
import com.test.social.SocialFragment;

public class ConnectDialog extends WindowDialog
{
	private SocialFragment fragmento;
	
	private int textId;
	
	private Button botonConectar, botonCancelar;
	private TextView titulo;
	private EditText textoNombre, textoContrasenya;
	
	public ConnectDialog(Context context, SocialFragment view, int text)
	{
		super(context, R.layout.dialog_connect_layout);
		
		fragmento = view;
		textId = text;
		
		titulo = (TextView) findViewById(R.id.textViewConnect1);
		titulo.setText(textId);
		
		textoNombre = (EditText) findViewById(R.id.editTextConnect1);
		textoContrasenya = (EditText) findViewById(R.id.editTextConnect2);
		
		botonConectar = (Button) findViewById(R.id.imageButtonConnect1);
		botonCancelar = (Button) findViewById(R.id.imageButtonConnect2);
		
		botonConectar.setOnClickListener(new OnConnectConectarClickListener());
		botonCancelar.setOnClickListener(new OnConnectCancelarClickListener());
		
	}
	
	@Override
	protected void onTouchOutsidePopUp(View v, MotionEvent event)
	{
		dismiss();
	}
	
	private class OnConnectConectarClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			fragmento.conectar(textoNombre.getText().toString(), textoContrasenya.getText().toString(), textId);
			dismiss();
		}
	}
	
	private class OnConnectCancelarClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			dismiss();
		}
	}
}
