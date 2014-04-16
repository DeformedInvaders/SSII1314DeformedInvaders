package com.android.alert;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.project.main.R;

@SuppressLint("SetJavaScriptEnabled")
public abstract class WebAlert extends WindowAlert
{
	private WebView webView;

	/* Constructora */

	public WebAlert(Context context, int title, int textNo)
	{
		super(context, title, false);
		
		setView(R.layout.alert_web_layout);
		webView = (WebView) findViewById(R.id.webViewWebAlert1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebClient());
		
		setNegativeButton(textNo, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton) { }
		});
	}

	/* Métodos Abstractos */

	public abstract boolean evaluarURL(String url);

	/* Métodos Públicos */

	public void loadURL(String url)
	{
		webView.loadUrl(url);
	}

	/* Listener WebClient */

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
			if (evaluarURL(url))
			{
				super.onPageStarted(view, url, favicon);
			}
		}
	}
}
