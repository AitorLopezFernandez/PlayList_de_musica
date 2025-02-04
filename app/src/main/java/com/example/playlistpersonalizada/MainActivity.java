package com.example.playlistpersonalizada;

import android.content.pm.ActivityInfo;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ImageButton btnPlay, btnStop, btnSiguiente;
    private Button btnAddVideo;
    private EditText editTextUrl;

    private static final int MAX_VIDEOS = 300;
    private static String[] playlist = new String[MAX_VIDEOS];
    private int indexActual = 0;
    private int currentVideoCount = 0;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("PlaylistPreferences", MODE_PRIVATE);
        loadPlaylist();

        webView = findViewById(R.id.webView);
        btnPlay = findViewById(R.id.btnPlay);
        btnStop = findViewById(R.id.btnStop);
        btnSiguiente = findViewById(R.id.btnNext);
        btnAddVideo = findViewById(R.id.btnAddVideo);
        editTextUrl = findViewById(R.id.editTextUrl);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        btnPlay.setOnClickListener(v -> {
            if (currentVideoCount > 0) {
                playVideo();
            } else {
                Toast.makeText(this, "No hay videos en la lista.", Toast.LENGTH_SHORT).show();
            }
        });

        btnStop.setOnClickListener(v -> webView.loadUrl("about:blank"));

        btnSiguiente.setOnClickListener(v -> {
            if (currentVideoCount > 0) {
                indexActual = (indexActual + 1) % currentVideoCount;
                playVideo();
            } else {
                Toast.makeText(this, "No hay videos en la lista.", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddVideo.setOnClickListener(v -> {
            String nuevaUrl = editTextUrl.getText().toString().trim();
            if (!nuevaUrl.isEmpty() && nuevaUrl.contains("youtube.com")) {
                if (currentVideoCount < MAX_VIDEOS) {
                    playlist[currentVideoCount] = nuevaUrl;
                    currentVideoCount++;
                    savePlaylist();
                    editTextUrl.setText("");
                    Toast.makeText(this, "Video agregado a la lista.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No puedes agregar más videos, lista llena.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese una URL válida de YouTube.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void playVideo() {
        if (playlist[indexActual] != null) {
            webView.loadUrl(playlist[indexActual]);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    private void loadPlaylist() {
        for (int i = 0; i < MAX_VIDEOS; i++) {
            String url = sharedPreferences.getString("video_" + i, null);
            if (url != null) {
                playlist[i] = url;
                currentVideoCount++;
            }
        }
    }

    private void savePlaylist() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < MAX_VIDEOS; i++) {
            if (playlist[i] != null) {
                editor.putString("video_" + i, playlist[i]);
            } else {
                editor.remove("video_" + i);
            }
        }
        editor.apply();
    }
}
