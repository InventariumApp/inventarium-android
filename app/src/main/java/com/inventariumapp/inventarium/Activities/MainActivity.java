package com.inventariumapp.inventarium.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.client.android.CaptureActivity;
import com.inventariumapp.inventarium.Fragments.Pantry;
import com.inventariumapp.inventarium.Fragments.ShoppingList;
import com.inventariumapp.inventarium.R;
import android.Manifest;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    // Tabs/Menu
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private TabLayout tabLayout;
    private Pantry pantry;
    private ShoppingList shoppingList;

    // FireBase
    private FirebaseDatabase database;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hides the title bar
        setContentView(R.layout.activity_main);

        //get firebase auth instance
        mFirebaseAuth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("testingDB");


        // Set Variables
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        pantry = new Pantry();
        shoppingList = new ShoppingList();
        ArrayList<String> test = new ArrayList<String>();
        test.add("signOut");

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, test));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        setTabs();
        setupTabLayout();
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("Main Activity", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
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

    private void setCurrentTabFragment(int tabPosition)
    {
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

    public void onManualClick(View v) {
        Intent intent = new Intent(this, ManualInput.class);
        intent.putExtra("message", Integer.toString(tabLayout.getSelectedTabPosition()));
        startActivity(intent);
    }

    public void onMenuClick(View v) {
        Log.i("onMenuClick", "Clicked!!!!!!!!!!!!!!!!!");
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
            } else if (resultCode == RESULT_CANCELED) {
// Handle cancel
            }
        }
    }

}
