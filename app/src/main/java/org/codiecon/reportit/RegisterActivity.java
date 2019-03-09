package org.codiecon.reportit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;

import org.codiecon.reportit.auth.SharedPrefManager;
import org.codiecon.reportit.constant.SystemConstant;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
    private Button SignUp;
    private EditText Username, Email, Password, Phone;
    private String username, email, password, phone, refreshedToken;
    private TextView Login, Error;
    private WeakReference<Context> cReference;
    private ProgressDialog waitDialog;
    private RelativeLayout coordinatorLayout;
    RequestQueue requestQueue;
    private String locationUpdate, LocalityName;
    private Double latitude, longitude;
    private LocationManager locationManager;
    private static final int STORAGE_PERMISSION_CODE = 123;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        cReference = new WeakReference<Context>(RegisterActivity.this);
        requestQueue = Volley.newRequestQueue(cReference.get());
        locationUpdate = SharedPrefManager.getInstance(cReference.get()).getUserLocation();
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (locationUpdate == null) {
            requestStoragePermission();
            // getLocation();
        } else {
            getLocation();
        }
        SignUp = findViewById(R.id.SignIn);
        Username = findViewById(R.id.userName);
        Password = findViewById(R.id.passWord);
        Phone = findViewById(R.id.phone);
        Email = findViewById(R.id.email);
        Login = findViewById(R.id.login);
        Error = findViewById(R.id.error);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        SignUp.setOnClickListener(this);
        Login.setOnClickListener(this);
        waitDialog = new ProgressDialog(this);
        waitDialog.setCanceledOnTouchOutside(false);


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.SignIn) {

            username = Username.getText().toString();
            if (username == null || username.isEmpty()) {
                Error.setText("Username cannot be empty");
                return;
            } else {
                Error.setText("");
            }


            email = Email.getText().toString();
            if (email == null || email.isEmpty()) {
                Error.setText("email cannot be empty");
                return;
            } else {
                Error.setText("");
            }

            if (!validateEmail(email)) {
                Error.setText("invalid email addresss");
                return;
            } else {
                Error.setText("");
            }

            password = Password.getText().toString();
            if (password == null || password.isEmpty()) {
                Error.setText("Password cannot be empty");
                return;
            } else {
                Error.setText("");
            }


            phone = Phone.getText().toString();

            if (phone == null || phone.isEmpty()) {
                Error.setText("Mobile Number cannot be empty");
                return;
            } else {
                Error.setText("");
            }

            if (phone.length() != 10) {
                Error.setText("Mobile Number invalid");
                return;
            } else {
                Error.setText("");
            }


            if (haveNetwork()) {

                if (refreshedToken == null) {
                    refreshedToken = FirebaseInstanceId.getInstance().getToken();
                } else {
                    Log.d("refresh", refreshedToken);

                }
                waitDialog.setMessage("Signing up...");
                waitDialog.show();
                RegisterUser();
            } else if (!haveNetwork()) {
                Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Internet not available", Snackbar.LENGTH_LONG);
                snackbar.setDuration(5000).show();
                //Toast.makeText(getApplicationContext(),"Internet not available",Toast.LENGTH_LONG).show();
            }

        } else if (v.getId() == R.id.login) {
            Intent intent = new Intent(this, LoginActivity.class);
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

    private boolean validateEmail(String address) {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(address).find();
    }


    private void RegisterUser() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("email", email);
        params.put("username", username);
        params.put("password", password);
        params.put("phoneNo", phone);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("location", LocalityName);
        params.put("fcm_token", refreshedToken);

        JSONObject s = new JSONObject(params);
        Log.d("ss", s + "");
        JsonObjectRequest request_json = new JsonObjectRequest(SystemConstant.URI + "user/signup", new JSONObject(params),
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        waitDialog.dismiss();
                        Log.d("response", response + "");
                        if (response.getString("email") != null) {
                            Intent intent1 = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent1);
                        } else {
                            Error.setText("username or password wrong");
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
                    // Toast.makeText(getApplicationContext(), "Server down try after some time", Toast.LENGTH_LONG).show();
                    Error.setText("No Internet Connection Or Some Network Issue");
                } else {
                    waitDialog.dismiss();
                    // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    Error.setText("No Internet Connection Or Some Network Issue");
                }
            }

        });
        requestQueue.add(request_json);
    }


    private boolean haveNetwork() {
        boolean have_WIFI = false;
        boolean have_MOBILE_DATA = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
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


    private void requestStoragePermission() {
        Log.d("radhe", "method");

        if (ContextCompat.checkSelfPermission(cReference.get(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("radhe1", "method");
            return;
        }
        if ((ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) && (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            Log.d("radhe2", "method");
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, STORAGE_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(cReference.get(), "Permission granted now access the location", Toast.LENGTH_LONG).show();
                String lPermission = "location_approved";
                SharedPrefManager.getInstance(cReference.get())
                    .userLocation(lPermission);
                getLocation();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
                //   UploadPhoto.setVisibility(View.INVISIBLE);
            }
        }
    }


    private void getLocation() {
        try {
            locationManager = (LocationManager) cReference.get().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        requestQueue.stop();
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(cReference.get(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                LocalityName = addresses.get(0).getAddressLine(0);
                latitude = addresses.get(0).getLatitude();
                longitude = addresses.get(0).getLongitude();
                Log.d("locality name", addresses.get(0).getAddressLine(0) + "");
            } else {
                Log.d("locality", "not found");
            }
        } catch (Exception e) {
            Log.d("locality", e.getMessage());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

