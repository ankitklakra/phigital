package com.phigital.ai.News;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.phigital.ai.BaseActivity;
import com.phigital.ai.R;


public class StoreActivity extends BaseActivity {

    WebView  webView ;
    ProgressBar pd;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        String url = "https://sites.google.com/view/phigital/product";
        webView = findViewById(R.id.webView);
        pd = findViewById(R.id.pb);
        Intent i3 = new Intent(Intent.ACTION_VIEW);
        i3.setData(Uri.parse(url));
        startActivity(i3);
//        webView.setWebViewClient(new WebViewClient());
//        webView.loadUrl(url);
//        WebSettings webSettings =webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webView.setWebChromeClient(new WebChromeClient(){
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                pd.setProgress(newProgress);
//                if (newProgress ==100){
//                    pd.setVisibility(View.GONE);
//                }
//                super.onProgressChanged(view, newProgress);
//            }
//        });

//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebViewClient(new HelloWebViewClient());
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
