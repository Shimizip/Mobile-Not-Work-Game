package de.thk.syp.mobilenotworkgame.ui.topten;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.thk.syp.mobilenotworkgame.R;
import de.thk.syp.mobilenotworkgame.databinding.FragmentScoreboardBinding;
import de.thk.syp.mobilenotworkgame.databinding.FragmentToptenBinding;

public class TopTenFragment extends Fragment {

    private FragmentToptenBinding binding;
    private TextView tvTopTenSpielperiodeZeitraum;
    private TextView tvTopTenUserScore;
    private ListView lvTopTenList;
    private static int SID = 0;
    private static String ACCESSTOKEN = "";
    SharedPreferences sh;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Layout für dieses Fragment inflaten
        binding = FragmentToptenBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sh = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());

        // SID und JWT auslesen
        try {
            SID = Integer.parseInt(sh.getString("SID", "0"));
        } catch (NumberFormatException e) {
            SID = 0; // Default-Wert, falls die Konvertierung fehlschlägt
        }
        ACCESSTOKEN = sh.getString("ACCESSTOKEN", null);

        // GUI-Elemente initialisieren
        tvTopTenSpielperiodeZeitraum = root.findViewById(R.id.tvTopTenUserScore);
        tvTopTenUserScore = root.findViewById(R.id.tvTopTenSpielperiodeZeitraum);
        lvTopTenList = root.findViewById(R.id.lvTopTenList);

        // Anfrage-Queue erstellen
        RequestQueue queue = Volley.newRequestQueue(this.getContext().getApplicationContext());

        // Datum letzter Spielperiode ermitteln
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -14);
        String dateStringMinus14Days = dateFormat.format(calendar.getTime());

        // REST-Querys ausführen und zur Queue hinzufügen
        String spielperiodeUrl = getResources().getString(R.string.base_url) + ":PORT" + "/spiel/spielperiode?date=" + dateStringMinus14Days;
        JsonArrayRequest jsonArrayRequestSpielperiode = new JsonArrayRequest(Request.Method.GET, spielperiodeUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int spid = updateSpielperiodeZeitraumTv(response);
                        String scoreboardQueryUrl = getResources().getString(R.string.base_url) + ":PORT" + "/spiel/scoreboard?spid=" + spid;
                        JsonArrayRequest jsonArrayRequestScoreboard = new JsonArrayRequest(Request.Method.GET, scoreboardQueryUrl, null,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        updateTopTenList(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getContext(), "Fehler beim Laden der TopTen. Bitte versuchen Sie es später erneut.", Toast.LENGTH_LONG).show();
                                        Log.e("error", error.toString());
                                    }
                                }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Authorization", "Bearer " + ACCESSTOKEN);
                                return headers;
                            }
                        };
                        queue.add(jsonArrayRequestScoreboard);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), "Fehler beim Laden der TopTen. Bitte versuchen Sie es später erneut.", Toast.LENGTH_LONG).show();
                        Log.e("error", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + ACCESSTOKEN); // TODO!!!
                return headers;
            }
        };
        queue.add(jsonArrayRequestSpielperiode);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private int updateSpielperiodeZeitraumTv(JSONArray jsonArray) {
        JSONObject jsonObject;
        SimpleDateFormat dateFormatDE = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date vonDatum, bisDatum;
        String anzeigeText = "";
        int spid = 0;

        try {
            jsonObject = jsonArray.getJSONObject(0);
            vonDatum = dateFormat.parse(jsonObject.getString("vondatum"));
            bisDatum = dateFormat.parse(jsonObject.getString("bisdatum"));
            spid = jsonObject.getInt("spid");
            anzeigeText = "Zeitraum: " + dateFormatDE.format(vonDatum) + " bis " + dateFormatDE.format(bisDatum);
        } catch (JSONException | ParseException e) {
            Toast.makeText(getContext(), "Fehler beim Parsen der Daten. Bitte versuchen Sie es später erneut.", Toast.LENGTH_LONG).show();
        }

        String finalAnzeigeText = anzeigeText;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTopTenSpielperiodeZeitraum = getView().findViewById(R.id.tvTopTenSpielperiodeZeitraum);
                tvTopTenSpielperiodeZeitraum.setText(finalAnzeigeText);
            }
        });
        return spid;
    }

    private void updateTopTenList(JSONArray jsonArray) {
        Map<String, Integer> scoreMap = new HashMap<>();

        int spielerScoreboardPos = 0;
        int spielerSummepunkte = 0;

        // JSON in HashMap parsen
        try {
            // Die ersten 10 Plätze anzeigen. Wenn es weniger als 10 gibt, dann alle aus dem Array
            for (int i = 0; i < Math.min(jsonArray.length(), 10); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String spielerName = "";
                // Beim 1. Platz soll eine Krone angezeigt werden
                if (i + 1 == 1) {
                    spielerName = (i + 1) + ". \uD83D\uDF32 " + jsonObject.getString("spielerName");
                } else {
                    spielerName = (i + 1) + ".    " + jsonObject.getString("spielerName");
                }

                int summepunkte = jsonObject.getInt("summepunkte");
                scoreMap.put(spielerName, summepunkte);

                // Für die Textview den Rang und die Punkte des Spielers ermitteln
                if (jsonObject.getInt("sid") == SID) {
                    spielerScoreboardPos = i + 1;
                    spielerSummepunkte = summepunkte;
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Fehler beim Parsen der Daten. Bitte versuchen Sie es später erneut.", Toast.LENGTH_LONG).show();
        }

        // Konvertiere die scoreMap in eine Liste von Einträgen
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(scoreMap.entrySet());

        // verwende einen Comparator, um die Liste nach Punkten zu sortieren
        Collections.sort(entryList, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // Liste in String List konvertieren
        List<String> scoreList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : entryList) {
            scoreList.add(entry.getKey() + "\n      " + entry.getValue() + " Punkte");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                scoreList
        );
        int finalSpielerSummepunkte = spielerSummepunkte;
        int finalSpielerScoreboardPos = spielerScoreboardPos;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTopTenUserScore = getView().findViewById(R.id.tvTopTenUserScore);

                lvTopTenList = getView().findViewById(R.id.lvTopTenList);
                if (scoreMap.isEmpty()) {
                    tvTopTenUserScore.setText("Es sind keine Daten für diese Spielperiode vorhanden");
                } else {
                    lvTopTenList.setAdapter(adapter);
                    if (finalSpielerScoreboardPos == 0) {
                        tvTopTenUserScore.setText("Du hast in der vergangenen Spielperiode keine Punkte erzielt.");
                    } else {
                        tvTopTenUserScore.setText("Du hast mit "+ finalSpielerSummepunkte + " Punkten Platz " + finalSpielerScoreboardPos + " von " + jsonArray.length() + " erreicht.");
                    }
                }
            }
        });
    }
}