package org.codiecon.reportit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.codiecon.reportit.auth.SharedPrefManager;
import org.codiecon.reportit.constant.SystemConstant;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button signIn;
    private EditText Email, Password;
    private String email, password, getEmail, userid, Name;
    private TextView ForgotPass, Error, signUp;
    private WeakReference<Context> cReference, aReference;
    private ProgressDialog waitDialog;
    private RelativeLayout coordinatorLayout;
    private int id, guestid;
    RequestQueue requestQueue;
    ConnectivityManager connectivityManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        cReference = new WeakReference<Context>(LoginActivity.this);
        aReference = new WeakReference<Context>(getApplicationContext());
        requestQueue = Volley.newRequestQueue(cReference.get());
        if (SharedPrefManager.getInstance(cReference.get()).isLoggedIn()) {
            Log.d("some", "this clicked");
            finish();
            startActivity(new Intent(cReference.get(), NearbyIssueActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return;
        }


        signIn = findViewById(R.id.SignIn);
        signUp = findViewById(R.id.SignUp);
        Email = findViewById(R.id.userName);
        Password = findViewById(R.id.passWord);
        Error = findViewById(R.id.error);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
        waitDialog = new ProgressDialog(this);
        waitDialog.setCanceledOnTouchOutside(false);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.SignIn) {

            email = Email.getText().toString();
            if (email == null || email.isEmpty()) {
                Error.setText("Email cannot be empty");
                return;
            } else {
                Error.setText("");
            }

            /*if (!validateEmail(email)) {
                Error.setText("Invalid email addresss");
                return;
            } else {
                Error.setText("");
            }*/

            password = Password.getText().toString();
            if (password == null || password.isEmpty()) {
                Error.setText("Password cannot be empty");
                return;
            } else {
                Error.setText("");
            }


            if (haveNetwork()) {
                waitDialog.setMessage("Signing in...");
                waitDialog.show();
                userLogin();
            } else if (!haveNetwork()) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Internet not available", Snackbar.LENGTH_LONG);
                snackbar.setDuration(5000).show();
            }
        } else if (v.getId() == R.id.SignUp) {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }


    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private boolean haveNetwork() {
        boolean have_WIFI = false;
        boolean have_MOBILE_DATA = false;

        connectivityManager = (ConnectivityManager) cReference.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();


        for (NetworkInfo info : networkInfos) {
            if (info.getTypeName().equalsIgnoreCase("WIFI"))
                if (info.isConnected())
                    have_WIFI = true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE"))
                if (info.isConnected())
                    have_MOBILE_DATA = true;
        }
        return have_MOBILE_DATA || have_WIFI;
    }


    @Override
    protected void onDestroy() {
        requestQueue.stop();
        super.onDestroy();
    }

    private boolean validateEmail(String address) {
        return Patterns.EMAIL_ADDRESS.matcher(address).matches();
    }


    private void userLogin() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", email);
        params.put("password", password);

        JsonObjectRequest request_json = new JsonObjectRequest(SystemConstant.URI + "user/login", new JSONObject(params),
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        waitDialog.dismiss();
                        Log.d("login", response + "");
                        if (response.getString("userId") != null) {
                            userid = response.getString("userId");
                            SharedPrefManager.getInstance(cReference.get()).userID(userid);
                            startActivity(new Intent(cReference.get(), NearbyIssueActivity.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            Error.setText("Invalid Credentials");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.error.VolleyError error) {
                if (error.getMessage() == null) {
                    waitDialog.dismiss();
                    Error.setText("No Internet Connection Or Some Network Issue");
                } else {
                    waitDialog.dismiss();
                    Error.setText(error.getMessage());
                }
            }

        });
        //request_json.setTag(LoginActivity.class);
        requestQueue.add(request_json);
    }

}
