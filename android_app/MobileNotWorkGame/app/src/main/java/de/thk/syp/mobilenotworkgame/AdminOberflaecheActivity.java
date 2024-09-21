package de.thk.syp.mobilenotworkgame;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminOberflaecheActivity extends Activity {

    private WebView webView;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        webView = (WebView) findViewById(R.id.admin_webview);
        backButton = (Button) findViewById(R.id.back_to_login_button);

        // WebView konfigurieren
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(getResources().getString(R.string.base_url) + ":PORT" + "/admin");
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Button Listener hinzufügen
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cookies löschen
                WebStorage.getInstance().deleteAllData();

                // Starte wieder die RegistrierungActivity
                Intent intent = new Intent(AdminOberflaecheActivity.this, RegistrierungActivity.class);
                // Flags setzen, um den Activity-Stack zu bereinigen
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}

