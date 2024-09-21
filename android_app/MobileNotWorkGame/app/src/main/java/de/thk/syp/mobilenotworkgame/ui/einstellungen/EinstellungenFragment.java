package de.thk.syp.mobilenotworkgame.ui.einstellungen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import de.thk.syp.mobilenotworkgame.AdminOberflaecheActivity;
import de.thk.syp.mobilenotworkgame.R;
import de.thk.syp.mobilenotworkgame.RegistrierungActivity;
import de.thk.syp.mobilenotworkgame.databinding.FragmentEinstellungenBinding;


public class EinstellungenFragment extends Fragment {

    private FragmentEinstellungenBinding binding;
    static private int zeitInMinuten;
    static private String userName;
    static private TextView userNameTextView;
    static private boolean abspeichernErfolgreich = false;
    private RequestQueue queue;
    public static final String TAG = "Einstellungen";
    private static String SID = "";
    private static String ACCESSTOKEN = "";
    SharedPreferences sh;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sh = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
        /**
         * read sid and accesstoken
         */
        SID = sh.getString("SID", null);
        ACCESSTOKEN = sh.getString("ACCESSTOKEN", null);
        queue = Volley.newRequestQueue(getActivity());


        /**
         * HTTP get fuer Username (/spieler/benutzername)
         */
        Log.v("HTTP", "starting HTTP get for Username");
        String url = getResources().getString(R.string.base_url)+":PORT/spieler/benutzername/" + SID;
        Log.v("HTTP", "URL: "+url);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        userName = response.toString();
                        userNameTextView.setText(userName);
                        Log.v("HTTP", "Received Name: "+userName);

                    }
                },
                error -> {
                    Toast.makeText(getContext(), "An error occured", Toast.LENGTH_LONG).show();
                    Log.v("HTTP", "Error :" +error.toString());
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + ACCESSTOKEN);
                return headers;
            }
        };

        stringRequest.setTag(TAG);
        queue.add(stringRequest);

        /**
         * Als Leiste umsetzen in 1 min Interval
         * */
        binding = FragmentEinstellungenBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SeekBar schiebeRegler = binding.schieberegler;
        schiebeRegler.setProgress((sh.getInt("POLLINGINTERVAL", -1)));
        TextView wertInSekundenTextView = binding.wertInSekunden;
        Button abgespeichertButton = binding.einstellungBtnApply;
        abgespeichertButton.setOnClickListener(new buttonAbspeichernListener());
        zeitInMinuten = sh.getInt("POLLINGINTERVAL",-1);
        userNameTextView = binding.einstellungenUsernametextview;
        userNameTextView.setText("Aktueller Name: "+userName);
        wertInSekundenTextView.setText("Minuten: " +zeitInMinuten);
        schiebeRegler.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                zeitInMinuten = progress;
                wertInSekundenTextView.setText("Minuten: " + zeitInMinuten);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Wird aufgerufen, wenn der Benutzer den Schieberegler berührt
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Wird aufgerufen, wenn der Benutzer den Schieberegler loslässt
            }
        });

        // Admin Login Button
        Button adminLoginButton = binding.einstellungBtnAdmin;
        adminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent erstellen, um die RegistrierungActivity zu starten
                Intent intent = new Intent(getActivity(), AdminOberflaecheActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }


    public class buttonAbspeichernListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SharedPreferences.Editor myEdit = sh.edit();
            myEdit.putInt("POLLINGINTERVAL",zeitInMinuten);
            myEdit.commit();
            Toast.makeText(getContext(), "Polling Intervall gesetzt", Toast.LENGTH_LONG).show();
            EditText userNameEditText = binding.einstellungenUsernameedittext;
            String neuerName = userNameEditText.getText().toString();

            if(!neuerName.equals(userName) && !neuerName.isEmpty()) {
                /**
                 * HTTP get fuer Username aendern (/spieler/benutzername-aendern)
                 */
                Log.v("HTTP", "starting HTTP get for Username");
                String url = getResources().getString(R.string.base_url) + ":PORT/spieler/benutzername-aendern/" + SID;
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.toString().equals(neuerName)) {
                                    userName = neuerName;
                                    userNameTextView.setText("Aktueller Name: " + userName);
                                    Toast.makeText(getContext(),
                                            "Abspeichern war erfolgreich",
                                            Toast.LENGTH_LONG).show();
                                }
                                Log.v("HTTP", " -----> Received new Name: " + response.toString());

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(),
                                        "Abspeichern ist fehlgeschlagen",
                                        Toast.LENGTH_LONG).show();
                                Log.v("HTTP", " -----> Error: " + error.toString());
                            }
                        }
                ) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("neuerBenutzername", neuerName);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + ACCESSTOKEN);
                        return headers;
                    }
                };
                queue.add(stringRequest);
            }
        }
    }
}
