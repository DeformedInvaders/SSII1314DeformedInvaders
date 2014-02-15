package com.android.alert;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;

@SuppressLint("SetJavaScriptEnabled")
public abstract class WebAlert
{
	private AlertDialog.Builder alert;
	private AlertDialog dialog;
	
	private WebView webView;
	
	public WebAlert(Context context, String title, String textNo)
	{	
		alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		
		alert.setNegativeButton(textNo, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				dismiss();
			}
		});
		
		LinearLayout layout = new LinearLayout(context);
		
		webView = new WebView(context);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebClient());	
		
		EditText keyboardHack = new EditText(context);
		keyboardHack.setVisibility(View.GONE);
		
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(webView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		layout.addView(keyboardHack, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); 
		
		alert.setView(layout);
		
		dialog = alert.create();
	}
	
	public void loadURL(String url)
	{
		webView.loadUrl(url);
	}
	
	public void show()
	{
		dialog.show();
	}
	
	public void dismiss()
	{
		dialog.dismiss();
	}
	
	public abstract boolean evaluarURL(String url);
	
	public class WebClient extends WebViewClient
	{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            return false;
        }
        
        @Override  
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {    
        	if(evaluarURL(url))
        	{
        		super.onPageStarted(view, url, favicon);
        	}
    	}
    }
}
