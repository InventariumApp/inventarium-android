package com.inventariumapp.inventarium.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    FirebaseDatabase database;
    DatabaseReference myRef;
    private Pantry pantry;
    private ShoppingList shoppingList;
    private TabLayout tabLayout;

    // FireBase Auth
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Main Activity", "onCreate");
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hides the title bar
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("testingDB");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
//        if (mFirebaseUser == null) {
//            // Not signed in, launch the Sign In activity
//            startActivity(new Intent(this, SignInActivity.class));
//            finish();
//            return;
//        } else {
            // Adds the pantry and shopping list tabs
            tabLayout = (TabLayout) findViewById(R.id.tabs);
            setTabs();
            setupTabLayout();
        //}
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
        pantry = new Pantry();
        shoppingList = new ShoppingList();

        tabLayout.addTab(tabLayout.newTab().setText("Pantry"),true);
        tabLayout.addTab(tabLayout.newTab().setText("Shopping List"));
    }

    public void onManualClick(View v) {
        Intent intent = new Intent(this, ManualInput.class);
        intent.putExtra("message", Integer.toString(tabLayout.getSelectedTabPosition()));
        startActivity(intent);
    }

    public void onBarcodeClick(View v) {
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
            } else if (resultCode == RESULT_CANCELED) {
// Handle cancel
            }
        }
    }

}
