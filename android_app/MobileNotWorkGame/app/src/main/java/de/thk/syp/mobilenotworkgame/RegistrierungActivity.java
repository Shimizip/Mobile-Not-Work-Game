package de.thk.syp.mobilenotworkgame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrierungActivity extends Activity {

    private static CheckBox privacyPolicyCheckBox;
    private static CheckBox nutzungsbedinungenCheckBox;
    private static TextView privacyPolicyTv;
    private static TextView nutzungsbedingungenTv;
    private RequestQueue queue;
    public static final String TAG = "Einstellungen";
    private static String SID = "";
    private static String ACCESSTOKEN = "";
    private static String GERAETEID = "";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrierung);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.getBoolean("firstStart", false)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstStart", Boolean.TRUE);
            editor.putInt("POLLINGINTERVAL", 5);
            editor.commit();
        }
        queue = Volley.newRequestQueue(this);

        privacyPolicyCheckBox = findViewById(R.id.privacy_policy_checkbox);
        nutzungsbedinungenCheckBox = findViewById(R.id.terms_checkbox);
        privacyPolicyTv = findViewById(R.id.privacy_policy_link);
        nutzungsbedingungenTv = findViewById(R.id.terms_link);

        // Links in TextViews anzeigbar machen
        privacyPolicyTv.setMovementMethod(LinkMovementMethod.getInstance());
        nutzungsbedingungenTv.setMovementMethod(LinkMovementMethod.getInstance());

        Button registrierungAbeschlossen = findViewById(R.id.continue_button);
        Button adminOberfläche = findViewById(R.id.admin_login_button);
        Button exit_button = findViewById(R.id.exit_button);

        registrierungAbeschlossen.setOnClickListener(new switchToMainListener());
        adminOberfläche.setOnClickListener(new switchToAdminLoginListener());
        exit_button.setOnClickListener(new closeAppListener());



        GERAETEID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        /**
         * HTTP post fuer Login (/spieler/login)
         */
        Log.v("HTTP", "starting HTTP post for Login");
        String url = getResources().getString(R.string.base_url)+":PORT/spieler/login?geraeteID="+GERAETEID;
        Log.v("HTTP", "URL: "+url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            SID = response.getString("id");
                            ACCESSTOKEN = response.getString("token");
                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            myEdit.putString("ACCESSTOKEN", ACCESSTOKEN);
                            myEdit.putString("SID", SID);
                            myEdit.commit();

                            Intent registrierungsIntent = new Intent(RegistrierungActivity.this, MainActivity.class);
                            startActivity(registrierungsIntent);
                            finish();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        switch (error.networkResponse.statusCode) {
                            case 403: {
                                Toast.makeText(this, "GeraeteID ist blockiert", Toast.LENGTH_LONG).show();
                            }
                            break;
                            default: {
                                Toast.makeText(this, "Login nicht erfolgreich", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(this, "Netzwerkfehler oder Server antwortet nicht", Toast.LENGTH_LONG).show();
                    }
                }
        ){

        };

        jsonObjectRequest.setTag(TAG);
        queue.add(jsonObjectRequest);
    }


    class switchToMainListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (privacyPolicyCheckBox.isChecked() && nutzungsbedinungenCheckBox.isChecked()) {

                /**
                 * HTTP post fuer Registrierung (/spieler/register)
                 */
                Log.v("HTTP", "starting HTTP post for Registration");
                String url = getResources().getString(R.string.base_url)+":PORT/spieler/register?geraeteID="+GERAETEID+"&einwilligungAGB=1";
                Log.v("HTTP", "URL: "+url);

                StringRequest stringRequestRegister = new StringRequest(
                        Request.Method.POST,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                {
                                    /**
                                     * HTTP post fuer Login (/spieler/benutzername)
                                     */
                                    Log.v("HTTP", "starting HTTP post for Username");
                                    String url = getResources().getString(R.string.base_url)+":PORT/spieler/login?geraeteID="+GERAETEID;
                                    Log.v("HTTP", "URL: "+url);


                                    JsonObjectRequest jsonObjectRequestLogin = new JsonObjectRequest(
                                            Request.Method.POST,
                                            url,
                                            null,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try {
                                                        SID = response.getString("id");
                                                        ACCESSTOKEN = response.getString("token");
                                                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                                        myEdit.putString("ACCESSTOKEN", ACCESSTOKEN);
                                                        myEdit.putString("SID", SID);
                                                        myEdit.commit();

                                                        Intent registrierungsIntent = new Intent(v.getContext(), MainActivity.class);
                                                        v.getContext().startActivity(registrierungsIntent);
                                                        finish();
                                                    } catch (JSONException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }
                                            },
                                            error -> {
                                                switch (error.networkResponse.statusCode){
                                                    case 403:{
                                                        Toast.makeText(getApplicationContext(), "GeraeteID ist blockiert", Toast.LENGTH_LONG).show();
                                                    }break;
                                                    default:{
                                                        Toast.makeText(getApplicationContext(), "Login nicht erfolgreich", Toast.LENGTH_LONG).show();
                                                    }
                                                };

                                                Log.v("HTTP", "Error :" + error.toString());
                                            }
                                    );
                                    jsonObjectRequestLogin.setTag(TAG);
                                    queue.add(jsonObjectRequestLogin);
                                }
                            }
                        },
                        error -> {
                            Toast.makeText(getApplicationContext(), "Registrierung Fehlgeschlagen", Toast.LENGTH_LONG).show();
                            Log.v("HTTP", "Error :" + error.toString());
                        }
                );

                stringRequestRegister.setTag(TAG);
                queue.add(stringRequestRegister);
            } else{
                Toast.makeText(RegistrierungActivity.this,
                        "Sie müssen die Datenschutzbestimmungen und die Nutzungsbedingungen akzeptieren um fortzufahren.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
    class switchToAdminLoginListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent adminLoginIntent = new Intent(v.getContext(), AdminOberflaecheActivity.class);
            v.getContext().startActivity(adminLoginIntent);
            finish();
        }
    }
    class closeAppListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
           finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }
}
