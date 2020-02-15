package com.example.c07766598_assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;

import com.example.c07766598_assignment.Adapters.TabAdapter;
import com.example.c07766598_assignment.Fragments.Tab1mapFragment;
import com.example.c07766598_assignment.Fragments.Tab2ListFragment;
import com.example.c07766598_assignment.Fragments.Tab3DirtectionFragment;
import com.example.c07766598_assignment.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private  TabAdapter adapter;
    private  TabLayout tabLayout;
    private  ViewPager viewPager;

    private final int REQUEST_CODE = 1;

    static boolean fire = false;

    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

        if(!checkPermission())
        {
            requestPermission();
        }




            viewPager = (ViewPager) findViewById( R.id.viewPager );
            tabLayout = (TabLayout) findViewById( R.id.tabLayout );
            adapter = new TabAdapter( getSupportFragmentManager() );
            adapter.addFragment( new Tab1mapFragment(), "Map View" );
            adapter.addFragment( new Tab2ListFragment(), "Favourites" );
            adapter.addFragment( new Tab3DirtectionFragment(), "Navigation" );
            viewPager.setAdapter( adapter );
            tabLayout.setupWithViewPager( viewPager );




        String apiKey = getString(R.string.api_key);





        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);




    }



    private Boolean checkPermission()
    {
        int permissionState = ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION );
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE );

    }


    public  void fire()
    {
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1mapFragment(), "Map View");
        adapter.addFragment(new Tab2ListFragment(), "Favourites");
        adapter.addFragment(new Tab3DirtectionFragment(), "Navigation");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem( 1 );

    }
}
