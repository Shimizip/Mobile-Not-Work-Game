package de.thk.syp.mobilenotworkgame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;


import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.thk.syp.mobilenotworkgame.database.AppDataBase;
import de.thk.syp.mobilenotworkgame.database.Messung;
import de.thk.syp.mobilenotworkgame.databinding.ActivityMainBinding;
import de.thk.syp.mobilenotworkgame.ui.kartenansicht.KartenansichtFragment;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    protected TelephonyManager telephonyManager;
    protected ConnectivityManager connManager;
    protected NetworkInfo networkInfo;
    private String IP_Adress = "8.8.8.8";
    private double latitude = 0;
    private double longitude = 0;
    private static int SPIELER_SID;
    private int ksid = 0;
    private int mfid = 0;
    private int msid = 0;
    private int lastestRSSI = 0;
    private int RTT = 0;
    private int latestSNR = 0;
    double finalRtt;
    private RequestQueue queue;
    private long REFRESH_TIME = 60000; // 20000 = 20 second;
    final private long OFFLINE_TIME_INTERVAL = 20000;
    final private int IS_WLAN = 99;
    final private int NO_SIGNAL = 2147483647;
    private static String AUTH_TOKEN;
    int statusCode;
    String TAG = "MyActivity";
    private Messung messungOffline = new Messung();
    private Context context;
    final String[] kartenSegmentUrl = new String[1];

    private int anzahlMessungen = 0;
    private boolean isOfflineThreadRunning = false;

    private AppDataBase db;
    SharedPreferences sh;

    int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO remove
        super.onCreate(savedInstanceState);
        //Context der App
        context = getApplicationContext();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        sh = PreferenceManager.getDefaultSharedPreferences(context);

        // Holen der Spieler ID, des Access Tokens und des Polling Intervals
        try {
            SPIELER_SID = Integer.parseInt(sh.getString("SID", "0"));
        } catch (NumberFormatException e) {
            SPIELER_SID = 0; // Default-Wert, falls die Konvertierung fehlschlägt
        }

        AUTH_TOKEN = sh.getString("ACCESSTOKEN", null);




        //Metadaten Services

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connManager.getActiveNetworkInfo();

        //Request Queue

        queue = Volley.newRequestQueue(this);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_kartenansicht, R.id.nav_scoreboard, R.id.nav_topten, R.id.nav_einstellungen)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Update der Version der Database
        final Migration MIGRATION_1_2 = new Migration(1, 2) {
            @Override
            public void migrate(SupportSQLiteDatabase database) {
                // Fügen Sie der Tabelle 'Messung' zwei neue Spalten 'lat' und 'lon' vom Typ REAL (entspricht double in Java) hinzu
                database.execSQL("ALTER TABLE Messung ADD COLUMN lat REAL NOT NULL DEFAULT 0");
                database.execSQL("ALTER TABLE Messung ADD COLUMN lon REAL NOT NULL DEFAULT 0");
            }
        };
        //Database mit Migration
        db = Room.databaseBuilder(context,
                        AppDataBase.class, "database-messungen")
                .addMigrations(MIGRATION_1_2) // Fügen Sie die Migration hinzu
                .build();


        //Signalstärke Auslesen

        PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                List<CellSignalStrength> signalStrengths = signalStrength.getCellSignalStrengths();
                for (CellSignalStrength cellSignalStrength : signalStrengths) {
                    if (cellSignalStrength instanceof CellSignalStrengthCdma) {
                        latestSNR = ((CellSignalStrengthCdma) cellSignalStrength).getEvdoSnr();
                        messungOffline.setSnr(latestSNR);
                        lastestRSSI = ((CellSignalStrengthCdma) cellSignalStrength).getEvdoDbm();
                        messungOffline.setRssi(lastestRSSI);
                        return;
                    } else if (cellSignalStrength instanceof CellSignalStrengthGsm) {
                        latestSNR = ((CellSignalStrengthGsm) cellSignalStrength).getDbm();
                        messungOffline.setSnr(latestSNR);
                        lastestRSSI = ((CellSignalStrengthGsm) cellSignalStrength).getRssi();
                        messungOffline.setRssi(lastestRSSI);
                        return;
                    } else if (cellSignalStrength instanceof CellSignalStrengthLte) {
                        latestSNR = ((CellSignalStrengthLte) cellSignalStrength).getRssnr();
                        messungOffline.setSnr(latestSNR);
                        lastestRSSI = ((CellSignalStrengthLte) cellSignalStrength).getRssi();
                        messungOffline.setRssi(lastestRSSI);

                        return;
                    } else if (cellSignalStrength instanceof CellSignalStrengthNr) {
                        latestSNR = ((CellSignalStrengthNr) cellSignalStrength).getCsiSinr();
                        messungOffline.setSnr(latestSNR);
                        lastestRSSI = ((CellSignalStrengthNr) cellSignalStrength).getCsiRsrp();
                        messungOffline.setRssi(lastestRSSI);
                        return;
                    }
                }
            }
        };
        telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        Thread startUpdatingMetaData = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Handler handler = new Handler();

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            long TEST = sh.getInt("POLLINGINTERVAL", 0) * 60000;
                            if(TEST>=60000){
                                REFRESH_TIME = TEST;
                            }
                        } catch (NumberFormatException e) {
                            REFRESH_TIME = 60000; // Default-Wert, falls die Konvertierung fehlschlägt
                        }

                        startUpdatingNetworkOperator();
                        pingIpAddress(IP_Adress);
                        getUserLocation();
                        messungOffline.setSid(SPIELER_SID);
                        kartenSegmentUrl[0] = getResources().getString(R.string.base_url) + ":PORT" + "/karte/get-ksid-fuer-standort?Lat=" + latitude + "&Lon=" + longitude;
                        //Prüfung der Connection
                        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

                        /*
                        //DEBUG
                        Log.d(TAG, "RSSI: "+ lastestRSSI);
                        Log.d(TAG, "SNR: "+ latestSNR);
                        Log.d(TAG, "KSID: "+ ksid);
                        Log.d(TAG, "MSID: "+ msid);
                        Log.d(TAG, "MFID: "+ mfid);
                        Log.d(TAG, "RTT: "+ RTT);
                        Log.d(TAG,"LON: "+ longitude);
                        Log.d(TAG,"LAT: "+ latitude);
                        Log.d(TAG, "SID: "+SPIELER_SID);
                        Log.d(TAG, "StatusCode: "+statusCode);
                        Log.d(TAG, "URL :"+getResources().getString(R.string.base_url) + ":PORT" + "/messung?sid=" + SPIELER_SID + "&ksid=" + ksid + "&msid=" + 4 + "&mfid=" + mfid + "&rssi=" + lastestRSSI + "&rtt=" + RTT + "&snr=" + latestSNR);
                         */

                        //Falls Mobilfunk an ist
                        if(msid != IS_WLAN) {
                            //Falls conneted
                            if (isConnected) {
                                if ((latitude != 0) && (longitude != 0)) {
                                    queue.add(createPostRequestMessung(kartenSegmentUrl[0], null));
                                }
                                //Falls nicht Connected Messungzwischen speichern
                            } else if (!isConnected) {
                                if ((messungOffline.getSid() != 0) && (messungOffline.getMsid() != 0) && (messungOffline.getMfid() != 0) && (messungOffline.getRssi() != 0) && (messungOffline.getRtt() != 0) && (messungOffline.getLat() != 0) && (messungOffline.getLon() != 0)) {
                                    if ((messungOffline.getSnr() != NO_SIGNAL) && (messungOffline.getRssi() != NO_SIGNAL)) {
                                        db.messungDao().insert(messungOffline);
                                    }
                                }
                            }
                            //Falls mit dem Wlan verbunden
                        }else if (msid == IS_WLAN) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "WLAN Network: \nKeine Messung.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        //DEBUG
                        List<Messung> messungen = db.messungDao().getAll();
                        anzahlMessungen = messungen.size();
                         // Steuervariable als Klassenmitglied hinzufügen
                                if (!isOfflineThreadRunning && isConnected && anzahlMessungen >= 1) {
                                    isOfflineThreadRunning = true; // Markieren, dass der Thread läuft
                                    Thread offline = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            int i = 0;
                                            for (Messung messungOffline : messungen) {
                                                kartenSegmentUrl[0] = getResources().getString(R.string.base_url) + ":PORT" + "/karte/get-ksid-fuer-standort?Lat=" + messungOffline.getLat() + "&Lon=" + messungOffline.getLon();
                                                queue.add(createPostRequestMessung(kartenSegmentUrl[0], messungOffline));
                                                try {
                                                    Thread.sleep(OFFLINE_TIME_INTERVAL); // Verzögerung für jeden Durchlauf
                                                } catch (InterruptedException e) {
                                                    Thread.currentThread().interrupt();
                                                    e.printStackTrace();
                                                }
                                            }
                                            db.messungDao().deleteAll();
                                            isOfflineThreadRunning = false; // Markieren, dass der Thread nicht mehr läuft
                                        }
                                    });
                                    offline.start();
                                }
                                handler.postDelayed(this, REFRESH_TIME); // Planen Sie, dieses Runnable erneut auszuführen
                            }
                        };
                        handler.post(runnable); // Starten des Runnable
                        Looper.loop();
            }
        });
        startUpdatingMetaData.start();
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    private static int getNetworkTypeNo(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return 2;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return 3;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return 4;
            case TelephonyManager.NETWORK_TYPE_NR:
                return 5;
            case TelephonyManager.NETWORK_TYPE_GSM:
                return 2;
            default:
                return 1;
        }
    }
    //
    //
    //---------------------------------Network Mobile und WLAN--------------------------------------------------------
    //
    //
    private int getNetworkType(Context context) {
        connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connManager.getActiveNetworkInfo();
        int networkType = 0;
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return IS_WLAN;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                networkType = networkInfo.getSubtype();
            }
        }
        return getNetworkTypeNo(networkType);
    }
    //
    //
    //----------------------------------------RTT---------------------------------------------------
    //
    //
    private void pingIpAddress(final String ipAddress) {
        try {
            // Ping-Befehl
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 " + ipAddress);

            // Lesen des Outputs
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String rtt = "-1";
            while ((line = reader.readLine()) != null) {
                if (line.contains("avg")) {
                    // Finde den RTT-Wert im Output
                    int start = line.indexOf("mdev = ") + 7;
                    int end = line.indexOf(" ms", start);
                    rtt = line.substring(start, end).split("/")[1]; // Extrahiere den Durchschnitts-RTT
                    break;
                }
            }
            reader.close();
            finalRtt = Double.parseDouble(rtt);
            RTT = (int) finalRtt;
            messungOffline.setRtt(RTT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //
    //
    //-------------------------------------------Provider und Standard--------------------------------
    //
    //
    private void startUpdatingNetworkOperator() {
        String networkOperator = telephonyManager.getNetworkOperator();
        mfid = Integer.parseInt(networkOperator) - 26200;
        messungOffline.setMfid(mfid);
        msid = getNetworkType(MainActivity.this);
        messungOffline.setMsid(msid);
    }
    //
    //
    //---------------------------------Koordinaten---------------------------------------------------
    //
    //
    @SuppressLint("MissingPermission")
    private void getUserLocation() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    messungOffline.setLat(latitude);
                    longitude = location.getLongitude();
                    messungOffline.setLon(longitude);
                }
            }
        });
    }
    //
    //
    // ------------------------------------Messung-------------------------------------------------------------
    //
    //
    public JsonObjectRequest createPostRequestMessung(String urlKartenSegment, Messung messungOfflineForURL) {
        return new JsonObjectRequest(Request.Method.GET, urlKartenSegment, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                         String messungurl = null;
                         Log.d(TAG,"Response");
                        // Verarbeiten der Antwort von der kartenSegment-Anfrage...
                        try {
                            ksid = response.getInt("ksid");
                            if(messungOfflineForURL!=null) {
                                messungOfflineForURL.setKsid(ksid);
                            }
                            // Weiter mit der Erstellung der messung-Anfrage

                            //Falls die mitgegebeneMessung NULL ist
                            if (messungOfflineForURL==null) {
                                // URL für die Normalen Messungen
                                messungurl = getResources().getString(R.string.base_url) + ":PORT" + "/messung?sid=" + SPIELER_SID + "&ksid=" + ksid + "&msid=" + msid + "&mfid=" + mfid + "&rssi=" + lastestRSSI + "&rtt=" + RTT + "&snr=" + latestSNR;
                            //Falls die mitgegebene Messung nicht NULL ist
                            } else if((messungOfflineForURL!=null)&&(messungOfflineForURL.getKsid()!=0)){
                                // URL für die Offline Messungen
                                messungurl = getResources().getString(R.string.base_url) + ":PORT" + "/messung?sid=" + messungOfflineForURL.getSid() + "&ksid=" + messungOfflineForURL.getKsid() + "&msid=" + messungOfflineForURL.getMsid() + "&mfid=" + messungOfflineForURL.getMfid()
                                        + "&rssi=" + messungOfflineForURL.getRssi() + "&rtt=" + messungOfflineForURL.getRtt() + "&snr=" + messungOfflineForURL.getSnr();
                            }
                            JsonObjectRequest messung = new JsonObjectRequest(Request.Method.POST, messungurl, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                int finalPunkte = response.getInt("punkte");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplication(), "Du hast " + finalPunkte + " Punkte erhalten", Toast.LENGTH_LONG).show();
                                                        // Kartenansicht aktualisieren
                                                        updateKartenansicht();
                                                    }
                                                });
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // Fehlerbehandlung für die messung-Anfrage
                                            if (error.networkResponse != null)
                                                statusCode = error.networkResponse.statusCode;
                                            //Toast.makeText(getApplicationContext(), "Fehler bei der Messung: " + statusCode, Toast.LENGTH_LONG).show();
                                        }
                                    }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    // Setzen der Headers für die messung-Anfrage
                                    Map<String, String> headers = new HashMap<>();
                                    headers.put("Authorization", "Bearer " + AUTH_TOKEN); // TODO: Token aktualisieren!
                                    return headers;
                                }
                                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                    final int statusCode = response.statusCode;

                                    // Umleitung auf den Haupt-UI-Thread
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Anzeigen des Toasts auf dem Haupt-UI-Thread
                                            //Toast.makeText(getApplicationContext(), "Status Code: " + statusCode, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    // Rufen Sie die ursprüngliche parseNetworkResponse auf, um das JSONObject zu erhalten
                                    return super.parseNetworkResponse(response);
                                }
                            };
                            // Hinzufügen der messung-Anfrage zur Queue
                                //Falls keine Messung in der Datenbank ist
                                if (anzahlMessungen == 0) {
                                    if ((SPIELER_SID != 0) && (ksid != 0) && (msid != 0) && (mfid != 0) && (lastestRSSI != 0) && (finalRtt != 0)) {
                                        if ((lastestRSSI != NO_SIGNAL) && (latestSNR != NO_SIGNAL)) {
                                            queue.add(messung);
                                        }
                                    }
                                //Falls Messungen in der Datenbank vorhanden sind
                                } else if (anzahlMessungen >= 1) {
                                            queue.add(messung);
                                    }
                        } catch (JSONException e) {
                            // Fehlerbehandlung für die kartenSegment-Anfrage
                            Toast.makeText(context, "Fehler beim Parsen der Daten. \nBitte versuchen Sie es später erneut.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Fehlerbehandlung für die kartenSegment-Anfrage
                        if (error.networkResponse != null)
                            statusCode = error.networkResponse.statusCode;
                        //Toast.makeText(getApplicationContext(), "Fehler beim Laden der Kartensegment ID: \n" + statusCode, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Setzen der Headers für die kartenSegment-Anfrage
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + AUTH_TOKEN);
                return headers;
            }
        };
    }

    /*
     * Update der Kartenansicht triggern
     */
    public void updateKartenansicht() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        if (navHostFragment != null) {
            // aktuelles Fragment ermitteln
            List<Fragment> fragmentList = navHostFragment.getChildFragmentManager().getFragments();
            if (!fragmentList.isEmpty()) {
                Fragment currentFragment = fragmentList.get(0);
                if (currentFragment instanceof KartenansichtFragment) {
                    ((KartenansichtFragment) currentFragment).triggerMapUpdate();
                }
            }
        }
    }

    //
    //
    //-----------------------TODO Permission
    //
    //
    /*
        private void hasLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissions erteilt", Toast.LENGTH_LONG).show();
            getUserLocation();
        } else if ((ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) &&
                (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION))) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_OCATION_REQUEST);
        }
    }
    -----------------------------------------------------------------------------------------------------------------------------------------------------*/
    //----------------------------------------------------------------------------------------------------------------------------------------------
}
