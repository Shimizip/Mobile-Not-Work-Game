package de.thk.syp.mobilenotworkgame;

import static android.app.PendingIntent.getActivity;
import de.thk.syp.mobilenotworkgame.database.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MetaData extends Activity {
    protected TelephonyManager telephonyManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    String IP_Adress = "8.8.8.8";
    TextView SNRTextView;
    TextView RTTTextView;
    TextView RSSITextView;
    TextView latTextView;
    TextView lonTextView;
    TextView kartenSegmentTextView;
    private double latitude = 0;
    private double longitude = 0;
    private static final int SPIELER_SID = 1;
    private int ksid = 0;
    private int mfid = 0;
    private int msid = 0;
    private int lastestRSSI = 0;
    private int RTT = 0;
    private int latestSNR = 0;
    String finalUpdatedOperator;
    String networkTypeString;
    String UUID;
    double finalRtt;
    private  RequestQueue queue;
    final private long REFRESH_TIME = 10000; // 1000 = 1 second;
    private static final String AUTH_TOKEN = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoidGVzdGlkIiwiZXhwIjoxNzA1OTIzNzQxLCJpYXQiOjE3MDU4MzczNDEsImF1dGhvcml0aWVzIjpbIlNQSUVMRVIiXX0.WSSlIhVagzN3xqYhY12UeM7h7tf9yuFpTTZNEkgCGnB0oS4Dda4AF9Ez2StNE88BiJu1TdoZXBeyWiV8xXLeCdfz_GyeRH-6GNXKRn1h451UC97A-45eauh3v0hBDDEhHEVSb9Z4GL_7C93JyHdHAIyPaGBkchBkv1glrcx8eElWDmQu1FKSVXZ-K1qkx2N27BJbHPabMoMfGw5jL15vVmRmQDTjMHipdFLQ6m7RKC4cF2vVqTSvg5ejDqjXkSkB1qInXhG1w9QAJi6paspHlghKTaJIx80yIFF47qyNh3LRPb5yHGZ1jEfhE3IjQtqdKu-9xobn7qPr9toz7UUqhQ";
    int statusCode;
    String TAG = "MyActivity";

    Messung messung = new Messung();
    private static int hitCount = 0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metadata);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        queue = Volley.newRequestQueue(this);
        final String[] messungURL = new String[1];
        final String[] kartenSegmentUrl = new String[1];
        SNRTextView = findViewById(R.id.SNRTextView);
        RSSITextView = findViewById(R.id.RSSITextView);
        RTTTextView = findViewById(R.id.RTTTextView);
        latTextView = findViewById(R.id.latTextView);
        lonTextView = findViewById(R.id.lonTextView);
        kartenSegmentTextView = findViewById(R.id.kartenSegmentTextView);
        AppDataBase db = Room.databaseBuilder(getApplicationContext(),
                AppDataBase.class, "database-messungen").build();

        //
        //
        //---------------------------------------UUID-----------------------------------------------
        //
        //
        TextView UUIDTextView = findViewById(R.id.UUIDTextView);
        UUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        UUIDTextView.setText(UUID);
        //
        //
        //----------------------------------------SNR und RSSI--------------------------------------
        //
        //
        PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                List<CellSignalStrength> signalStrengths = signalStrength.getCellSignalStrengths();
                for (CellSignalStrength cellSignalStrength : signalStrengths) {
                    if (cellSignalStrength instanceof CellSignalStrengthCdma) {
                        latestSNR = ((CellSignalStrengthCdma) cellSignalStrength).getEvdoSnr();
                        messung.snr = latestSNR;
                        lastestRSSI = ((CellSignalStrengthCdma) cellSignalStrength).getEvdoDbm();
                        messung.rssi = lastestRSSI;
                        updateSignalStrengthView();
                        return;
                    } else if (cellSignalStrength instanceof CellSignalStrengthGsm) {
                        latestSNR = ((CellSignalStrengthGsm) cellSignalStrength).getDbm();
                        messung.snr = latestSNR;
                        lastestRSSI = ((CellSignalStrengthGsm) cellSignalStrength).getRssi();
                        messung.rssi = lastestRSSI;
                        updateSignalStrengthView();
                        return;
                    } else if (cellSignalStrength instanceof CellSignalStrengthLte) {
                        latestSNR = ((CellSignalStrengthLte) cellSignalStrength).getRssnr();
                        messung.snr = latestSNR;
                        lastestRSSI = ((CellSignalStrengthLte) cellSignalStrength).getRssi();
                        messung.rssi = lastestRSSI;
                        updateSignalStrengthView();
                        return;
                    } else if (cellSignalStrength instanceof CellSignalStrengthNr) {
                        latestSNR = ((CellSignalStrengthNr) cellSignalStrength).getCsiSinr();
                        messung.snr = latestSNR;
                        lastestRSSI = ((CellSignalStrengthNr) cellSignalStrength).getCsiRsrp();
                        messung.rssi = lastestRSSI;
                        updateSignalStrengthView();
                        return;
                    }
                }
            }
        };
        telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        //kartenSegmentUrl[0] = getResources().getString(R.string.base_url) + ":PORT" + "/karte/get-ksid-fuer-standort?Lat=" + latitude + "&Lon=" + longitude;
        //queue.add(createPostRequestMessung(kartenSegmentUrl[0]));


        //
        //
        //-------------------------------------------------Eigener Thread für die Metadaten----------------------------------------------------------------------------------
        //
        //
        Thread startUpdatingMetaData = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Handler handler = new Handler();

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        startUpdatingNetworkOperator();
                        pingIpAddress(IP_Adress);
                        getUserLocation();
                        updateSignalStrengthView();
                        messung.sid = SPIELER_SID;

                        kartenSegmentUrl[0] = getResources().getString(R.string.base_url) + ":PORT" + "/karte/get-ksid-fuer-standort?Lat=" + latitude + "&Lon=" + longitude;
                        if((latitude != 0)&&(longitude!=0)) {
                            queue.add(createPostRequestMessung(kartenSegmentUrl[0]));
                        }
                        if((messung.sid != 0)&&(messung.ksid!=0)&&(messung.msid!=0)&&(messung.mfid!=0)&&(messung.rssi!=0)&&(messung.rtt!=0)&&(messung.snr!=0)) {
                            db.messungDao().insert(messung);
                        }
                        //DEBUG
                        messungURL[0] = getResources().getString(R.string.base_url) + ":PORT" + "/messung?sid=" + SPIELER_SID + "&ksid=" + ksid + "&msid=" + 4 + "&mfid=" + mfid + "&rssi=" + lastestRSSI + "&rtt=" + RTT + "&snr=" + latestSNR;
                        Log.d(TAG, "RSSI: "+ lastestRSSI);
                        Log.d(TAG, "SNR: "+ latestSNR);
                        Log.d(TAG, "KSID: "+ ksid);
                        Log.d(TAG, "MSID: "+ msid);
                        Log.d(TAG, "MFID: "+ mfid);
                        Log.d(TAG, "RTT: "+ RTT);
                        Log.d(TAG, "StatusCode: "+statusCode);
                        Log.d(TAG, messungURL[0]);
                        handler.postDelayed(this, REFRESH_TIME);
                        hitCount++;
                        //DEBUG
                        if(hitCount == 5){
                            int i = 0;
                            List<Messung> messungen = db.messungDao().getAll();

                            for(Messung  messung :messungen){
                                i++;
                                Log.d(TAG, "SID: "+ messung.sid);
                                Log.d(TAG, "RSSI: "+ messung.rssi);
                                Log.d(TAG, "SNR: "+ messung.snr);
                                Log.d(TAG, "KSID: "+ messung.ksid);
                                Log.d(TAG, "MSID: "+ messung.msid);
                                Log.d(TAG, "MFID: "+ messung.mfid);
                                Log.d(TAG, "RTT: "+ messung.rtt);
                                Log.d(TAG,"Messung No1: "+i);
                                Log.d(TAG, "MessunID: "+messung.id);
                                Log.d(TAG, "------------------------");
                                messungURL[0] = getResources().getString(R.string.base_url) + ":PORT" + "/messung?sid=" + messung.sid + "&ksid=" + messung.ksid + "&msid=" + 4 + "&mfid=" + messung.mfid + "&rssi=" + messung.rssi + "&rtt=" + messung.rtt + "&snr=" + messung.snr;
                                Log.d(TAG, "MessungURL:  "+messungURL[0]);
                                queue.add(PostRequestMessung(messungURL[0]));
                                Log.d(TAG, "------------------------");
                            }
                            db.messungDao().deleteAll();
                        }

                    }
                };
                handler.post(runnable);
                Looper.loop();
            }
        });
        startUpdatingMetaData.start();
    }
    //
    //
    //---------------------------------------------------Mapping von der Network No----------------------------
    //
    //
    private static int getNetworkTypeNo(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return 2;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return 3;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return 1;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return 1;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return 1;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return 4;
            case TelephonyManager.NETWORK_TYPE_NR:
                return 5;
            case TelephonyManager.NETWORK_TYPE_IWLAN:
                return 1;
            case TelephonyManager.NETWORK_TYPE_GSM:
                return 1;
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
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return 99;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                int networkType = networkInfo.getSubtype();
                //networkTypeString = getNetworkTypeName(networkType);
                msid = getNetworkTypeNo(networkType);
                messung.msid = msid;
                return msid;
            }
        }
        return 99;
    }

    private void updateSignalStrengthView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SNRTextView.setText(String.valueOf(latestSNR));
                RSSITextView.setText((String.valueOf(lastestRSSI)));
            }
        });
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
            String rtt = "";
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
            messung.rtt = RTT;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RTTTextView.setText("RTT: " + RTT + " ms");
                }
            });

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
        final TextView MobilfunkanbieterTextView = findViewById(R.id.MobilfunkanbieterTextView);
        final TextView MobilfunkStandardTextView = findViewById(R.id.MobilfunkStandardTextView);
        final Handler handler = new Handler(Looper.getMainLooper());
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = telephonyManager.getNetworkOperator();
        mfid = Integer.parseInt(networkOperator) - 26200;
        messung.mfid = mfid;
        String updatedOperator = "Unbekannt"; // Standardwert, wenn keine Übereinstimmung gefunden wird

        switch (networkOperator) {
            case "26201":
                updatedOperator = "Telekom";
                break;
            case "26202":
                updatedOperator = "Vodafone";
                break;
            case "26203":
                updatedOperator = "Telefonica";
                break;
        }
        finalUpdatedOperator = updatedOperator;
        msid = getNetworkType(MetaData.this);
        messung.msid = msid;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MobilfunkStandardTextView.setText(String.valueOf(msid));
                MobilfunkanbieterTextView.setText(finalUpdatedOperator);
            }
        });
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
                    longitude = location.getLongitude();
                    latTextView.setText(String.valueOf(latitude));
                    lonTextView.setText(String.valueOf(longitude));
                }
            }
        });
    }

    //
    //
    // ------------------------------------Messung-------------------------------------------------------------
    //
    //
    public JsonObjectRequest createPostRequestMessung(String urlKartenSegment) {
        return new JsonObjectRequest(Request.Method.GET, urlKartenSegment, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Verarbeiten der Antwort von der kartenSegment-Anfrage...
                        try {
                            ksid = response.getInt("ksid");
                            messung.ksid = ksid;
                            // Weiter mit der Erstellung der messung-Anfrage
                            String messungURL = getResources().getString(R.string.base_url) + ":PORT" + "/messung?sid=" + SPIELER_SID + "&ksid=" + ksid + "&msid=" + msid + "&mfid=" + mfid + "&rssi=" + lastestRSSI + "&rtt=" + RTT + "&snr=" + latestSNR;

                            /*JsonObjectRequest messung = new JsonObjectRequest(Request.Method.POST, messungURL, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            // Verarbeiten der Antwort von der messung-Anfrage...
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // Fehlerbehandlung für die messung-Anfrage
                                            if (error.networkResponse != null)
                                                statusCode = error.networkResponse.statusCode;
                                            Toast.makeText(getApplicationContext(), "Fehler bei der Messung: " + statusCode, Toast.LENGTH_LONG).show();
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
                                            Toast.makeText(getApplicationContext(), "Status Code: " + statusCode, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    // Rufen Sie die ursprüngliche parseNetworkResponse auf, um das JSONObject zu erhalten
                                    return super.parseNetworkResponse(response);
                                }
                            };
                            // Hinzufügen der messung-Anfrage zur Queue

                            if((SPIELER_SID != 0)&&(ksid!=0)&&(msid!=0)&&(mfid!=0)&&(lastestRSSI!=0)&&(finalRtt!=0)&&(latestSNR!=0)) {
                                queue.add(messung);
                            }*/
                        } catch (JSONException e) {
                            // Fehlerbehandlung für die kartenSegment-Anfrage
                            Toast.makeText(getApplication(), "Fehler beim Parsen der Daten. Bitte versuchen Sie es später erneut.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Fehlerbehandlung für die kartenSegment-Anfrage
                        if (error.networkResponse != null)
                            statusCode = error.networkResponse.statusCode;
                        Toast.makeText(getApplicationContext(), "Fehler beim Kartensegment: " + statusCode, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Setzen der Headers für die kartenSegment-Anfrage
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + AUTH_TOKEN); // TODO: Token aktualisieren!
                return headers;
            }
        };
    }


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
        private void updateKartenSegmentTextView(JSONObject response){
        int kartenSegment = 0;
        try {
            kartenSegment = response.getInt("ksid");
        }catch (JSONException e) {
            Toast.makeText(this, "Fehler beim Parsen der Daten. Bitte versuchen Sie es später erneut.", Toast.LENGTH_LONG).show();
        }

        ksid = kartenSegment;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                kartenSegmentTextView.setText(String.valueOf(ksid));            }
        });
    }
    //----------------------------------------------------------------------------------------------------------------------------------------------
    public JsonObjectRequest PostRequestMessung(String url) {
        return new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int punkte = response.getInt("punkte");
                            Toast.makeText(getApplication(), "Punkte: "+punkte, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null)
                            statusCode = error.networkResponse.statusCode;

                        Toast.makeText(getApplicationContext(), "Fehler :" + statusCode, Toast.LENGTH_LONG).show();
                    }

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + AUTH_TOKEN); // TODO!!!
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                final int statusCode = response.statusCode;

                // Umleitung auf den Haupt-UI-Thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // Anzeigen des Toasts auf dem Haupt-UI-Thread
                        Toast.makeText(getApplicationContext(), "Status Code: " + statusCode, Toast.LENGTH_SHORT).show();
                    }
                });

                // Rufen Sie die ursprüngliche parseNetworkResponse auf, um das JSONObject zu erhalten
                return super.parseNetworkResponse(response);
            }
        };
    }/*

    private static String getNetworkTypeName(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G";
            case TelephonyManager.NETWORK_TYPE_IWLAN:
                return "IWLAN";
            case TelephonyManager.NETWORK_TYPE_GSM:
                return "2G";
            default:
                return "Unbekannt";
        }
    }*/

}
