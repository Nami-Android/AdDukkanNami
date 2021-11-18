package com.addukkanapp.uis.activity_about_us;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.addukkanapp.R;
import com.addukkanapp.databinding.ActivityAboutUsBinding;
import com.addukkanapp.language.Language;

import io.paperdb.Paper;

public class AboutUsActivity extends AppCompatActivity {
    private ActivityAboutUsBinding binding;
    private String url = "";
    private String lang;
    private String type;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_us);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        type=intent.getStringExtra("type");

    }

    private void initView() {
        if(type.equals("0")){
            binding.tvtitle.setText(getResources().getString(R.string.terms_and_conditions));
        }
        else {
            binding.tvtitle.setText(getResources().getString(R.string.about_app));

        }
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.flBack.setOnClickListener(v -> {

            finish();
        });
        //createDialogAlert();
        binding.webView.getSettings().setAllowFileAccessFromFileURLs(true);
        binding.webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        binding.webView.getSettings().setAllowContentAccess(true);
        binding.webView.getSettings().setAllowFileAccess(true);
        binding.webView.getSettings().setBuiltInZoomControls(false);
        binding.webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setLoadWithOverviewMode(true);
        binding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        binding.webView.loadUrl(url);

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                binding.progBar.setVisibility(View.GONE);

            }


        });


    }


}