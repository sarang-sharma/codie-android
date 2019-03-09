package org.codiecon.reportit;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.request.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;

import org.codiecon.reportit.adapters.ReportedIssueAdapter;
import org.codiecon.reportit.auth.SharedPrefManager;
import org.codiecon.reportit.helper.FileUtils;
import org.codiecon.reportit.models.ReportedIssue;
import org.codiecon.reportit.models.response.ReportedIssueResponse;
import org.codiecon.reportit.models.response.Wrapper;
import org.codiecon.reportit.outbound.IssueService;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;

public class UploadIssueActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {


    private ConnectivityManager connectivityManager;
    private WeakReference<Context> cReference;
    private Toolbar toolbar;
    private EditText Title,Descreption,Tag;
    private Button Save;
    private String title,descreption,tag;
    private TextView Error;
    private LocationManager locationManager;
    private double latitute,logitude;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private String locationUpdate;
    ArrayList<String> fileNames = new ArrayList<>();







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_feed_ui);
        toolbar = findViewById(R.id.toolbar);
        Title = findViewById(R.id.title);
        Descreption = findViewById(R.id.description);
        Save= (Button) findViewById(R.id.save);
        Error = (TextView) findViewById(R.id.error);
        Tag = findViewById(R.id.url);
        setSupportActionBar(toolbar);
      //  fileNames = getIntent().getStringArrayListExtra("images");
        //onBackPressed();
        locationUpdate = SharedPrefManager.getInstance(this).getUserLocation();
        if(locationUpdate == null) {
            requestStoragePermission();
            // getLocation();
        }else {
            getLocation();
        }

        getSupportActionBar().setTitle("All Issues");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(this, R.drawable.ic_back_arrow);
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

    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.save) {
            title = Title.getText().toString();
            descreption = Descreption.getText().toString();
            tag = Tag.getText().toString();
//
//            if (title == null || title.isEmpty()) {
//                Error.setText(Title.getHint() + " cannot be empty");
//                return;
//            } else {
//                Error.setText("");
//            }
//
//            if (descreption == null || descreption.isEmpty()) {
//                Error.setText(Descreption.getHint() + " cannot be empty");
//                return;
//            } else {
//                Error.setText("");
//            }
            if(locationUpdate == null) {
                requestStoragePermission();
                // getLocation();
            }else {
                getLocation();
            }

            Log.d("think","oe");

        }

    }





    private void requestStoragePermission() {
        Log.d("radhe","method");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UploadIssueActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("radhe1", "method");
            return;
        }
        if ((ActivityCompat.shouldShowRequestPermissionRationale(UploadIssueActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) && (ActivityCompat.shouldShowRequestPermissionRationale(UploadIssueActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            Log.d("radhe2","method");
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
                //Displaying a toast
                Toast.makeText(this, "Permission granted now access the location", Toast.LENGTH_LONG).show();
                String lPermission = "location_approved";
                SharedPrefManager.getInstance(cReference.get())
                        .userLocation(
                                lPermission);

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
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onLocationChanged(Location location) {

        latitute = location.getLatitude();
        logitude = location.getLongitude();

        Log.d("layi",latitute+"");
        Log.d("log",logitude+"");

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
