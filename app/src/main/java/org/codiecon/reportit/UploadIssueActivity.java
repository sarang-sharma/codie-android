package org.codiecon.reportit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.codiecon.reportit.auth.SharedPrefManager;
import org.codiecon.reportit.constant.SystemConstant;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class UploadIssueActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private ConnectivityManager connectivityManager;
    private WeakReference<Context> cReference;
    private Button Save;
    private TextView Error;
    private Toolbar toolbar;
    private EditText Title, Descreption, Tag;
    private String title, description, tag, address;
    private LocationManager locationManager;
    private double latitude, longitude;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private String locationUpdate;
    RequestQueue requestQueue;
    private ProgressDialog waitDialog;

    ArrayList<String> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_feed_ui);
        toolbar = findViewById(R.id.toolbar);
        Title = findViewById(R.id.title);
        Descreption = findViewById(R.id.description);
        Save = (Button) findViewById(R.id.save);
        Error = (TextView) findViewById(R.id.error);
        Tag = findViewById(R.id.url);
        setSupportActionBar(toolbar);
        images = getIntent().getStringArrayListExtra("images");
        //onBackPressed();
        locationUpdate = SharedPrefManager.getInstance(this).getUserLocation();
        if (locationUpdate == null) {
            requestStoragePermission();
            // getLocation();
        } else {
            getLocation();
        }

        getSupportActionBar().setTitle("Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_back_arrow);
        assert upArrow != null;
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cReference = new WeakReference<Context>(UploadIssueActivity.this);
        Save.setOnClickListener(this);
        waitDialog = new ProgressDialog(this);
        waitDialog.setCanceledOnTouchOutside(false);
        requestQueue = Volley.newRequestQueue(cReference.get());
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.save) {
            title = Title.getText().toString();
            description = Descreption.getText().toString();
            tag = Tag.getText().toString();

            if (title == null || title.isEmpty()) {
                Error.setText(Title.getHint() + " cannot be empty");
                return;
            } else {
                Error.setText("");
            }

            if (description == null || description.isEmpty()) {
                Error.setText(Descreption.getHint() + " cannot be empty");
                return;
            } else {
                Error.setText("");
            }

            if (locationUpdate == null) {
                requestStoragePermission();
                // getLocation();
            } else {
                getLocation();
            }
            Log.d("think", "oe");
            registerUser();
        }
    }

    private void registerUser() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("userId", "");
        params.put("title", title);
        params.put("category", tag);
        params.put("description", description);
        params.put("status", "PUBLISHED");
        params.put("address", address);
        params.put("images", images);
        params.put("longitude", longitude);
        params.put("latitude", latitude);

        JSONObject s = new JSONObject(params);
        JsonObjectRequest request_json = new JsonObjectRequest(SystemConstant.URI + "issue/save", new JSONObject(params),
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        waitDialog.dismiss();
                        if (response != null && response.getBoolean("status") == true) {
                            Intent intent = new Intent(UploadIssueActivity.this, NearbyIssueActivity.class);
                            startActivity(intent);
                        } else {
                            Error.setText(response.getString("message"));
                        }
                    } catch (Exception e) {
                        Log.e("issue", e.toString());
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


    private void requestStoragePermission() {
        Log.d("radhe", "method");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UploadIssueActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("radhe1", "method");
            return;
        }
        if ((ActivityCompat.shouldShowRequestPermissionRationale(UploadIssueActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) && (ActivityCompat.shouldShowRequestPermissionRationale(UploadIssueActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            Log.d("radhe2", "method");
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(UploadIssueActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, STORAGE_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted now access the location", Toast.LENGTH_LONG).show();
                String lPermission = "location_approved";
                SharedPrefManager
                    .getInstance(cReference.get())
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
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(cReference.get(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0);
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
