package com.inventariumapp.inventarium;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("testingDB");
    private Pantry pantry;
    private ShoppingList shoppingList;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Main Activity", "onCreate");
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hides the title bar
        setContentView(R.layout.activity_main);

        // Adds the pantry and shopping list tabs
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setTabs();
        setupTabLayout();
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
}
