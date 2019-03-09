package org.codiecon.reportit;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;

import org.codiecon.reportit.adapters.ReportedIssueAdapter;
import org.codiecon.reportit.auth.SharedPrefManager;
import org.codiecon.reportit.helper.FileUtils;
import org.codiecon.reportit.models.ReportedIssue;
import org.codiecon.reportit.models.response.ReportedIssueResponse;
import org.codiecon.reportit.models.response.Wrapper;
import org.codiecon.reportit.outbound.IssueService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyIssueActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;
    private MenuItem menuItem;
    private ConnectivityManager connectivityManager;
    private WeakReference<Context> cReference;
    private LinearLayout linearLayout;
    private static final int STORAGE_PERMISSION_CODE = 456;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    private String imagePath = "";
    private String path, filesize, mediaUpdate;
    private int FileSizeCheck;
    String imageEncoded;
    List<String> imagesEncodedList;



    /*private List<ReportedIssue> populate(){
        List<ReportedIssue> issues = new ArrayList<>();
        for(int i=0; i<10; i++){
            ReportedIssue issue = new ReportedIssue();
            issue.setTitle("Tree Fallen on road");
            issue.setImages(Arrays.asList(R.drawable.tree_fallen_1, R.drawable.tree_fallen_2, R.drawable.tree_fallen_3));
            issue.setTags(Arrays.asList("road", "block", "traffic"));
            issue.setLocation("HSR Sector " + i);
            issues.add(issue);
        }
        return issues;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_issue_listing);
        requestStoragePermission();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //onBackPressed();

        getSupportActionBar().setTitle("Feed");
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
        cReference = new WeakReference<Context>(NearbyIssueActivity.this);
        mediaUpdate = SharedPrefManager.getInstance(cReference.get()).getUserMedia();
        recyclerView = findViewById(R.id.parent_recycler_view);
        linearLayout = findViewById(R.id.main_layout);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ConnectionManager.instance()
            .create(IssueService.class)
            .getAll(0, 10).enqueue(new Callback<Wrapper<List<ReportedIssueResponse>>>(){

            @Override
            public void onResponse(Call<Wrapper<List<ReportedIssueResponse>>> call, Response<Wrapper<List<ReportedIssueResponse>>> response) {
                List<ReportedIssue> issues = new ArrayList<>();
                if(response != null && response.body() != null){
                    for(ReportedIssueResponse item : response.body().getContent()){
                        ReportedIssue issue = new ReportedIssue();
                        issue.setId(item.getId());
                        issue.setTitle(item.getTitle());
                        issue.setDescription(item.getDescription());
                        issue.setLocation(item.getAddress());
                        issue.setImages(new ArrayList<>(item.getImages()));
                        issue.setReporter(item.getCreatedBy());
                        issue.setVotes(item.getVotes());
                        issues.add(issue);
                    }
                }
                adapter = new ReportedIssueAdapter(NearbyIssueActivity.this, issues);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<Wrapper<List<ReportedIssueResponse>>> call, Throwable t) {
                Log.e( "jkfsadkjf", t.toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menuItem = menu.findItem(R.id.action_image_upload);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_image_upload) {
            // Toast.makeText(getApplicationContext(),"some",Toast.LENGTH_LONG).show();
            if(!requestStoragePermission()) {
                Toast.makeText(this, "Permission is not granted !!!",
                        Toast.LENGTH_LONG).show();
                return true;

            }

            if(haveNetwork()) {
                showFileChooser();

            }else if(!haveNetwork()) {
                Snackbar snackbar = Snackbar
                        .make(linearLayout, "Internet not available", Snackbar.LENGTH_LONG);
                snackbar.setDuration(5000).show();
                //  Toast.makeText(getApplicationContext(),"Internet not available",Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();
                    imagePath=FileUtils.getPath(this,mImageUri);
                    Log.d("single",imagePath);

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded  = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        for (int i=0;i <= mArrayUri.size();i++)
                        {
                            imagePath+=FileUtils.getPath(this,mArrayUri.get(i));
                            Log.d("single1",imagePath);
                            imagePath = imagePath+",";
                        }
                        Log.d("multiple",imagePath);
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {

            requestStoragePermission();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }






    private boolean haveNetwork(){
        boolean have_WIFI=false;
        boolean have_MOBILE_DATA= false;

        connectivityManager = (ConnectivityManager) cReference.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos= connectivityManager.getAllNetworkInfo();


        for(NetworkInfo info:networkInfos)
        {
            if(info.getTypeName().equalsIgnoreCase("WIFI"))
                if(info.isConnected())
                    have_WIFI = true;
            if(info.getTypeName().equalsIgnoreCase("MOBILE"))
                if(info.isConnected())
                    have_MOBILE_DATA = true;
        }
        return have_MOBILE_DATA || have_WIFI;
    }


    private boolean requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("radhe1","method");
            return true;
        }

        /*if (ActivityCompat.shouldShowRequestPermissionRationale(NearbyIssueActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            Log.d("radhe2", "method");
            return false;

        }*/
        //And finally ask for the permission
        ActivityCompat.requestPermissions(NearbyIssueActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("radhe1","method");
            return true;
        }
        return false;
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "permission granted", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}
