package com.huijimuhe.monolog.ui.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.ui.base.AbstractActivity;

public class WebActivity extends AbstractActivity {
    private WebView mWebView;
    protected Toolbar toolbar;

    private String mUrl;
    private String mTitle;

    public static Intent newIntent(String url, String title) {
        Intent intent = new Intent(AppContext.getInstance(),
                WebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        //get url
        if (savedInstanceState == null) {
            mUrl = getIntent().getStringExtra("url");
            mTitle=getIntent().getStringExtra("title");
        } else {
            mUrl = savedInstanceState.getString("url");
            mTitle=savedInstanceState.getString("title");
        }
        initUI();
        initWeb();
    }

    private void initUI() {
        //Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(Color.BLACK);
        //bind view
        mWebView = (WebView) findViewById(R.id.webview);
    }

    private void initWeb() {
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.addJavascriptInterface(new JsObject(), "injectedObject");
        mWebView.setWebViewClient(new WebViewClientEx());
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                }
                return false;
            }
        });
        mWebView.loadUrl(mUrl);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("url", mUrl);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class WebViewClientEx extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

        }
    }

    /**
     * 防漏洞
     */
    class JsObject {
        @JavascriptInterface
        public String toString() { return "injectedObject"; }
    }
}
