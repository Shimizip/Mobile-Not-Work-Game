package de.thk.syp.mobilenotworkgame.ui.kartenansicht;


import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.android.volley.toolbox.Volley;

import android.Manifest;

import de.thk.syp.mobilenotworkgame.R;
import de.thk.syp.mobilenotworkgame.databinding.FragmentKartenansichtBinding;

public class KartenansichtFragment extends Fragment {
    private WebView karte;
    private FragmentKartenansichtBinding binding;
    private static String SID = "";
    private static String ACCESSTOKEN = "";
    private LocationManager locationManager;
    private LocationListener locationListener;
    SharedPreferences sh;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentKartenansichtBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sh = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());

        // SID und JWT auslesen
        SID = sh.getString("SID", null);
        ACCESSTOKEN = sh.getString("ACCESSTOKEN", null);

        karte = binding.karte;
        karte.setWebViewClient(new WebViewClient());
        karte.loadUrl(getResources().getString(R.string.base_url) + ":PORT" + "?sid=" + SID + "&token=" + ACCESSTOKEN);
        WebSettings webSettings=karte.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        karte.setWebViewClient(new CustomWebViewClient());

        locationManager = (LocationManager) getActivity().getSystemService(this.getActivity().LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Sendet die aktuellen Koordinaten an die Webansicht
                sendLocationToWeb(location.getLatitude(), location.getLongitude());
            }
        };

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, locationListener);
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, locationListener);
                }
            }
        }
    }

    public void sendLocationToWeb(double latitude, double longitude) {
        karte.post(() -> {
            karte.evaluateJavascript("javascript: window.receiveLocationFromAndroid(" + latitude + "," + longitude + ")", null);
        });
    }

    public void triggerMapUpdate() {
        karte.post(() -> {
            karte.evaluateJavascript("javascript: window.triggerBoundsLogger()", null);
        });
    }


    private class CustomWebViewClient extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            // Lade benutzerdefinierten HTML-Inhalt, wenn die Seite nicht geladen werden kann
            String htmlContent = "<html><body>" +
                    "<h2>Offline-Modus</h2>" +
                    "<p>Die Seite konnte nicht geladen werden. Bitte überprüfe deine Internetverbindung.</p>" +
                    "<p>Deine Messungen werden auf dem Gerät zwischengespeichert und sobald du wieder Empfang hast, erhältst du Punkte dafür.</p>" +
                    "</body></html>";
            view.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null);
        }
    }

}