package de.thk.syp.mobilenotworkgame.ui.scoreboard;

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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.thk.syp.mobilenotworkgame.R;
import de.thk.syp.mobilenotworkgame.databinding.FragmentScoreboardBinding;

public class ScoreboardFragment extends Fragment {

    private TextView tvScoreboardUserScore;
    private TextView tvScoreboardDaysRemaining;
    private ListView lvScoreboardTopTenList;
    private FragmentScoreboardBinding binding;
    private static int SID = 0;
    private static String ACCESSTOKEN = "";
    SharedPreferences sh;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Layout für dieses Fragment inflaten
        binding = FragmentScoreboardBinding.inflate(inflater, container, false);
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
        tvScoreboardUserScore = root.findViewById(R.id.tvScoreboardUserScore);
        tvScoreboardDaysRemaining = root.findViewById(R.id.tvScoreboardDaysRemaining);
        lvScoreboardTopTenList = root.findViewById(R.id.lvScoreboardTopTenList);

        // Anfrage-Queue erstellen
        RequestQueue queue = Volley.newRequestQueue(this.getContext().getApplicationContext());

        // aktuelles Datum auslesen


        // REST-Querys ausführen und zur Queue hinzufügen
        String spielperiodeUrl = getResources().getString(R.string.base_url) + ":PORT" + "/spiel/spielperiode?date=" + dateFormat.format(new Date());
        JsonArrayRequest jsonArrayRequestSpielperiode = new JsonArrayRequest(Request.Method.GET, spielperiodeUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        updateTimeRemainingTv(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), "Fehler beim Laden des Scoreboards. Bitte versuchen Sie es später erneut.", Toast.LENGTH_LONG).show();
                        Log.e("error", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + ACCESSTOKEN);
                return headers;
            }

        };
        queue.add(jsonArrayRequestSpielperiode);

        String scoreboardQueryUrl = getResources().getString(R.string.base_url) + ":PORT" + "/spiel/scoreboard";
        JsonArrayRequest jsonArrayRequestScoreboard = new JsonArrayRequest(Request.Method.GET, scoreboardQueryUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        updateScoreboard(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Fehler beim Laden des Scoreboards. Bitte versuchen Sie es später erneut.", Toast.LENGTH_LONG).show();
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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateTimeRemainingTv(JSONArray jsonArray) {
        JSONObject jsonObject;
        Date bisDatum = null;

        try {
            jsonObject = jsonArray.getJSONObject(0);
            bisDatum = dateFormat.parse(jsonObject.getString("bisdatum"));
            Log.e("Datum", "Datum: " + bisDatum);
        } catch (JSONException | ParseException | NullPointerException e) {
            Toast.makeText(getContext(), "Fehler beim Parsen der Daten. Bitte versuchen Sie es später erneut.", Toast.LENGTH_LONG).show();
        }

        // Verbleibende Zeit berechnen
        Date dateNow = new Date();
        long zeitDiff = bisDatum.getTime() - dateNow.getTime();

        long sekunden = zeitDiff / 1000;
        long stunden = sekunden / 3600;
        long tage = stunden / 24;
        stunden = stunden % 24;

        String anzeigeText = "Verbleibende Zeit in der Spielperiode: " + tage + " Tage und " + stunden + " Stunden";
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvScoreboardDaysRemaining = getView().findViewById(R.id.tvScoreboardDaysRemaining);
                tvScoreboardDaysRemaining.setText(anzeigeText);
                if (tage == 0) {
                    tvScoreboardDaysRemaining.setTextColor(Color.RED);
                }
            }
        });
    }

    private void updateScoreboard(JSONArray jsonArray) {
        Map<String, Integer> scoreMap = new HashMap<>();

        int spielerScoreboardPos = 0;
        int spielerSummepunkte = 0;
        // JSON in HashMap parsen
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String spielerName = "";
                // Beim 1. Platz soll eine Krone angezeigt werden
                if (i+1 == 1) {
                    spielerName = (i+1) + ". \uD83D\uDF32 " + jsonObject.getString("spielerName");
                } else {
                    spielerName = (i+1) + ".    " + jsonObject.getString("spielerName");
                }

                int summepunkte = jsonObject.getInt("summepunkte");
                scoreMap.put(spielerName, summepunkte);

                // Für die Textview den Rang und die Punkte des Spielers ermitteln
                if (jsonObject.getInt("sid") == SID) {
                    spielerScoreboardPos = i+1;
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
                lvScoreboardTopTenList = getView().findViewById(R.id.lvScoreboardTopTenList);
                lvScoreboardTopTenList.setAdapter(adapter);
                tvScoreboardUserScore = getView().findViewById(R.id.tvScoreboardUserScore);
                if (finalSpielerScoreboardPos == 0) {
                    tvScoreboardUserScore.setText("Du hast in dieser Spielperiode noch keine Punkte erzielt.");
                } else {
                    tvScoreboardUserScore.setText("Du befindest dich mit "+ finalSpielerSummepunkte + " Punkten auf Platz " + finalSpielerScoreboardPos + " von " + jsonArray.length());
                }

            }
        });
    }
}