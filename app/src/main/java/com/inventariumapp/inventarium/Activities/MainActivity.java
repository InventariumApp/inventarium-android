package com.inventariumapp.inventarium.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.client.android.CaptureActivity;
import com.inventariumapp.inventarium.Fragments.ManualInputDialog;
import com.inventariumapp.inventarium.Fragments.Pantry;
import com.inventariumapp.inventarium.Fragments.ShoppingList;
import com.inventariumapp.inventarium.R;
import android.Manifest;
import com.android.volley.*;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // FireBase
    private FirebaseDatabase database;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;

    private String user;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private TabLayout tabLayout;
    private Pantry pantry;
    private ShoppingList shoppingList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hides the title bar
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("testingDB");
        mFirebaseAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LogInActivity.class));
                    finish();
                }
            }
        };

        // Set Variables
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //mDrawerList = (ListView) findViewById(R.id.left_drawer);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        pantry = new Pantry();
        shoppingList = new ShoppingList();
        ArrayList<String> test = new ArrayList<String>();
        test.add("signOut");

//        // Set the adapter for the list view
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
//                R.layout.drawer_list_item, test));
//        // Set the list's click listener
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        setTabs();
        setupTabLayout();

//        Intent intent = new Intent(this, ItemDetailCard.class);
//        intent.putExtra("message", "doritos");
//        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mFirebaseAuth.removeAuthStateListener(authListener);
        }
    }

    private void setTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition)
        {
            case 0 :
                replaceFragment(pantry);
                break;
            case 1 :
                replaceFragment(shoppingList);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Pantry"),true);
        tabLayout.addTab(tabLayout.newTab().setText("Shopping List"));

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        switch (position) {
            case 0: signOut();
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void signOut() {
        mFirebaseAuth.signOut();
    }

    public void onManualClick(View v) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = ManualInputDialog.newInstance(tabLayout.getSelectedTabPosition());
        dialog.show(getSupportFragmentManager(), "ManualInputDialog");

//        Intent intent = new Intent(this, ManualInputTest.class);
//        intent.putExtra("list", Integer.toString(tabLayout.getSelectedTabPosition()));
//        startActivity(intent);
    }

    public void onMenuClick(View v) {
        mDrawerLayout.openDrawer(GravityCompat.START, true);
    }

    public void onBarcodeClick(View v) {
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            startScan();
        } else {
            requestCameraPermission();
        }
    }

    public void onImageClick(View v) {
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, 2);
            }
        } else {
            requestCameraPermission();
        }

    }

    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        ActivityCompat.requestPermissions(this, permissions, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode != 1) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // we have permission, so create the camerasource
            startScan();
            return;
        }
        // Else permission not granted
        Toast.makeText(this, "Cannot use barcode scan without granting camera permission", Toast.LENGTH_SHORT).show();
    }

    private void startScan() {
        Intent intent = new Intent(getApplicationContext(),CaptureActivity.class);
        intent.setAction("com.google.zxing.client.android.SCAN");
        intent.putExtra("SAVE_HISTORY", false);
        startActivityForResult(intent, 0);
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(this);
                String url ="http://159.203.166.121:8080/product_name?barcode=" + contents;

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("HTTP Response: ", response);
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    String productName = obj.get("clean_nm").toString();
                                    addProduct(productName);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("HTTP Get Err: ", error.toString());
                    }
                });
                queue.add(stringRequest);
            } else if (resultCode == RESULT_CANCELED) {
            // Handle cancel
            }
        }
        if (requestCode == 2) {
            encodeBitmapAndSaveToFirebase(data);
            // HTTP Get request
            // .get(159.203.166.121:8080/image_data/{file_name}" ) {
            // expecting: {'clean_nm': productName}
            //}
            //
        }
    }

    public void addProduct(String productName) {
        Intent intent = new Intent(this, ManualInput.class);
        intent.putExtra("list", "0");
        intent.putExtra("message", productName);
        startActivity(intent);
    }

    public void encodeBitmapAndSaveToFirebase(Intent data) {
        //saves the pic locally
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] dataBAOS = baos.toByteArray();

        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("image");
        UploadTask uploadTask = imagesRef.putBytes(dataBAOS);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            }
        });
    }

    public void purchaseItem(View v){

        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
        intent.putExtra("url", "https://www.amazon.com/s/ref=nb_sb_noss_2?url=search-alias%3Daps&field-keywords=doritos");
        startActivity(intent);
    }

    public void shareList() {
        // HTTP Post request
        // .post(159.203.166.121:8080/share_list", {user_email {email}, recipient_phone_number {1 + number} ) {
        // expecting: {'clean_nm': productName}
        //}
        //
    }

}
