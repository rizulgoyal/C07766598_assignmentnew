package com.example.c07766598_assignment.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.c07766598_assignment.ModelClasses.GetDirectionData;
import com.example.c07766598_assignment.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.w3c.dom.Text;

import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class Tab3DirtectionFragment extends Fragment implements OnMapReadyCallback {


    GoogleMap mMap;
    private final int REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    private SupportMapFragment fragment;

    CardView cardViewDirection, cardViewClearMap;

    Double latSource, longSource;
    Double latDest, longDest;
    TextView distance, duration;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated( savedInstanceState );



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById( R.id.mapDirection);


        fragment.getMapAsync( this );


            getUserLocation();
            if(!checkPermission())
            {
                requestPermission();
            }
            else
            {
                fusedLocationProviderClient.requestLocationUpdates( locationRequest, locationCallback, Looper.myLooper() );
            }

       // search places



        final AutocompleteSupportFragment autocompleteFragment1 = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment1);
        if(autocompleteFragment1 != null) {
            autocompleteFragment1.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS,Place.Field.ADDRESS_COMPONENTS));

            autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    LatLng latLng = place.getLatLng();
                    if(latLng!=null) {
                        //mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng)
                                .title(place.getName())
                                .snippet(place.getAddress())
                                .icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_RED ) ));

                        latSource = latLng.latitude;
                        longSource = latLng.longitude;
//                        CameraPosition cameraPosition = CameraPosition.builder()
//                                .target( latLng )
//                                .zoom( 15 )
//                                .bearing( 0 )
//                                .tilt( 45 )
//                                .build();
//                        mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );

                    }

                    //Toast.makeText(getContext(), "" + temp, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull Status status) {

                    Toast.makeText(getContext(), "" + status, Toast.LENGTH_SHORT).show();
                }
            });
        }


        AutocompleteSupportFragment autocompleteFragment2 = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment2);
        if(autocompleteFragment2 != null) {
            autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS,Place.Field.ADDRESS_COMPONENTS));

            autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    LatLng latLng = place.getLatLng();
                    if(latLng!=null) {
                        //mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng)
                                .title(place.getName())
                                .snippet(place.getAddress())
                                .icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_AZURE ) ));

                        latDest = latLng.latitude;
                        longDest = latLng.longitude;
//                        CameraPosition cameraPosition = CameraPosition.builder()
//                                .target( latLng )
//                                .zoom( 15 )
//                                .bearing( 0 )
//                                .tilt( 45 )
//                                .build();
//                        mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );

                    }

                    //Toast.makeText(getContext(), "" + temp, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull Status status) {

                    Toast.makeText(getContext(), "" + status, Toast.LENGTH_SHORT).show();
                }
            });
        }

        distance = view.findViewById( R.id.distance_text_navigation );
        duration = view.findViewById( R.id.duration_text_navigation );
        final LinearLayout layoutNavigate = view.findViewById( R.id.layoutToHideNavigation );


        cardViewDirection = view.findViewById( R.id.card_direction );
        cardViewDirection.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Object[] dataTransfer;

                String direction_url = getDirectionUrl( latSource, longSource, latDest, longDest );
                Log.i("MainActivity", direction_url);
                dataTransfer = new Object[6];
                dataTransfer[0] = mMap;
                dataTransfer[1] = direction_url;

                dataTransfer[2] = new LatLng( latDest, longDest );
                dataTransfer[3] = new LatLng( latSource, longSource );
                dataTransfer[4] = distance;
                dataTransfer[5] = duration;
                layoutNavigate.setVisibility( View.VISIBLE );
                cardViewDirection.setVisibility( View.GONE );

                GetDirectionData getDirectionData = new GetDirectionData();
                getDirectionData.execute( dataTransfer );


            }
        } );

        cardViewClearMap = view.findViewById( R.id.card_clear_map );
        cardViewClearMap.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                cardViewDirection.setVisibility( View.VISIBLE );
                layoutNavigate.setVisibility( View.GONE );
            }
        } );






    }

    private String getDirectionUrl(double latitude, double longitude, double destlatitude, double destlongitude)
    {
        StringBuilder placeUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?"  );
        placeUrl.append( "origin=" + latitude + "," + longitude );
        placeUrl.append( "&destination=" + destlatitude + "," + destlongitude );

        //placeUrl.append( "&keyword=cruise" );
        placeUrl.append( "&key=" + getString(R.string.api_key_class ));
        return placeUrl.toString();
    }



    private Boolean checkPermission()
    {
        int permissionState = ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION );
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions( getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE );

    }

    private void getUserLocation()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( getActivity() );
        locationRequest = new LocationRequest();
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        locationRequest.setInterval( 5000 );
        locationRequest.setFastestInterval( 3000 );
        locationRequest.setSmallestDisplacement( 10 );
         setHomeMarker();
        //setFavouriteMarkers();


    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//        mMap = googleMap;
//
//    }


    private void setHomeMarker(){
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations())
                {
                    //new
                    LatLng userLocation = new LatLng( location.getLatitude(), location.getLongitude());
//                    latitude = location.getLatitude();
//                    longitude = location.getLongitude();

                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target( userLocation )
                            .zoom( 15 )
                            .bearing( 0 )
                            .tilt( 45 )
                            .build();
                    mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );
                    mMap.addMarker( new MarkerOptions().position( userLocation )
                            .title( "Your Location" ));




                    // .icon( BitmapDescriptorFactory.fromResource( R.drawable.icon ) ));


                }
            }
        };
    }


    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate( R.layout.fragment_three, container, false);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType( GoogleMap.MAP_TYPE_NORMAL );
        mMap.setMyLocationEnabled(true);

    }
}
