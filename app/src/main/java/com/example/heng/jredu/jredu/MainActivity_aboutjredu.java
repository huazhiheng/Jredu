package com.example.heng.jredu.jredu;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.heng.jredu.R;
import com.example.heng.jredu.util.SystemBarTintManager;

public class MainActivity_aboutjredu extends Activity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_aboutjredu);

        webView = (WebView) findViewById(R.id.myWebView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://m.amap.com/navi/?" +
                "dest=121.442946,37.468676&" +
                "destName=杰瑞教育&" +
                "hideRouteIcon=1&" +
                "key=4c6b9ba6213d1e804dbd4682ab89cea2");

        setTranslucentStatus();
    }
    /**
     * 沉浸式状态栏
     */
    public void setTranslucentStatus() {
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.tongzhilan);

    }

}
