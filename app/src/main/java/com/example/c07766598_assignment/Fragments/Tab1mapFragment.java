package com.example.c07766598_assignment.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.c07766598_assignment.ModelClasses.GetNearbyPlaceData;
import com.example.c07766598_assignment.ModelClasses.GetNearbyPlaceDataWithFavourites;
import com.example.c07766598_assignment.ModelClasses.Places;
import com.example.c07766598_assignment.R;
import com.example.c07766598_assignment.RoomDatabase.PlacesDB;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class Tab1mapFragment extends Fragment implements OnMapReadyCallback{

    private final int REQUEST_CODE = 1;



    GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;

    double latitude, longitude;
    double destLat, destLong;

    double latnew, longnew;
boolean check = false;
    final int RADIUS = 1500;
    static boolean directionRequest;
    List<Places> placesList;
    Button addPlace, visitPlace;

    private SupportMapFragment fragment;

    boolean checkVisit = false;
    Button nearby;




    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated( bundle );

        //code for nearbysearch

        nearby = getView().findViewById( R.id.nearbyMainMap );
        nearby.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v1) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( getActivity() );
                // builder.setTitle( "Edit Employee" );
                LayoutInflater inflater = LayoutInflater.from( getActivity() );
                View v = inflater.inflate( R.layout.select_nearbyplace_layout,null );
                builder.setView( v );
                final android.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();
                final Spinner spinnerDept = v.findViewById( R.id.spinnerPlacesNearby );
                Button buttonEdit = v.findViewById( R.id.button_shownearby );

                buttonEdit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Object[] dataTransfer;
                        // get the url from place api
                        String url = getUrl( latitude, longitude, spinnerDept.getSelectedItem().toString());
                        //  Log.i("MainActivity", url);
                        // setmarkers( url );
                        dataTransfer = new Object[7];
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;
                        dataTransfer[2] = new LatLng( destLat, destLong );
                        dataTransfer[3] = new LatLng( latitude, longitude );
                        dataTransfer[4] = getAddress( new LatLng( latitude, longitude ) );
                        dataTransfer[5] = placesList;
                        dataTransfer[6] = getContext();

                        GetNearbyPlaceDataWithFavourites getNearbyPlaceDataWithFavourites = new GetNearbyPlaceDataWithFavourites();
                        getNearbyPlaceDataWithFavourites.execute(dataTransfer);
                        Toast.makeText(v.getContext() , spinnerDept.getSelectedItem().toString(), Toast.LENGTH_SHORT ).show();


                        alertDialog.dismiss();



                    }
                } );


            }
        } );

        // code for visit button starts here

        visitPlace = getView().findViewById( R.id.buttonVisitPlace );
        visitPlace.setVisibility( View.GONE );

        visitPlace.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                checkVisit = false;
                LatLng latLng = new LatLng( latnew, longnew );

                if (placesList != null) {
                    placesList.clear();
                }

                PlacesDB placesDB = PlacesDB.getInstance( getContext() );
                placesList = placesDB.daoObjct().getDefault();

                placesDB.daoObjct().getUserDetails().observe( getActivity(), new Observer<List<Places>>() {
                    @Override
                    public void onChanged(@Nullable List<Places> placeslist) {

                        placesList = placeslist;

                    }
                } );


                for (int i = 0; i < placesList.size(); i++) {

                    if (placesList.get( i ).getLat() == latnew && placesList.get( i ).getLng() == longnew && placesList.get( i ).getVisited()) {


                        AlertDialog.Builder builder1 = new AlertDialog.Builder( getContext() );
                        builder1.setMessage( "Already visited this place" );
                        builder1.setCancelable( true );


                        builder1.setNegativeButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        visitPlace.setVisibility( View.GONE );

                                        // alert11.dismiss();

                                    }
                                } );

                        final AlertDialog alert11 = builder1.create();


                        alert11.show();

                        checkVisit = true;


                    }
                }

                //if (placesList.get( i ).getLat() != latnew && placesList.get( i ).getLng() != longnew) {

                if(!checkVisit) {


                    for (int i = 0; i < placesList.size(); i++) {

                        if (placesList.get( i ).getLat() == latnew && placesList.get( i ).getLng() == longnew) {

                            Places currplace = placesList.get( i );
                            currplace.setVisited( true );
                            placesDB.daoObjct().update( currplace );

                            visitPlace.setVisibility( View.GONE );
                            Toast.makeText( getContext(), "Visited this place", Toast.LENGTH_SHORT ).show();
                            break;
                            //addPlace.setVisibility( View.VISIBLE );

                        }
                    }

                }

            }

            // break;




        } );

        //code for add in favourite start here

        addPlace = getView().findViewById( R.id.buttonAddFavourite );
        addPlace.setVisibility( View.GONE );
        addPlace.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                check = false;
                LatLng latLng = new LatLng( latnew, longnew );

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "YYY MM dd hh:mm:ss" );
                String placeDate = simpleDateFormat.format( calendar.getTime() );


                if (placesList != null) {
                    placesList.clear();
                }

                PlacesDB placesDB = PlacesDB.getInstance( getContext() );
                placesList = placesDB.daoObjct().getDefault();

                placesDB.daoObjct().getUserDetails().observe( getActivity(), new Observer<List<Places>>() {
                    @Override
                    public void onChanged(@Nullable List<Places> placeslist) {

                        placesList = placeslist;

                    }
                } );


                for (int i = 0; i < placesList.size(); i++) {

                    if (placesList.get( i ).getLat() == latnew && placesList.get( i ).getLng() == longnew) {

                        AlertDialog.Builder builder1 = new AlertDialog.Builder( getContext() );
                        builder1.setMessage( "Already added in Favourites" );
                        builder1.setCancelable( true );


                        builder1.setNegativeButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        addPlace.setVisibility( View.GONE );

                                        // alert11.dismiss();

                                    }
                                } );

                        final AlertDialog alert11 = builder1.create();


                        alert11.show();

                        check = true;


                    }
                }

                //if (placesList.get( i ).getLat() != latnew && placesList.get( i ).getLng() != longnew) {

                    if(!check) {

                        Places places = new Places( latLng.latitude, latLng.longitude, placeDate );
                        //placesList.add( places );

                        //PlacesDB placesDB = PlacesDB.getInstance( getContext() );
                        placesDB.daoObjct().insert( places );
                        //setFavouriteMarkers();
                        addPlace.setVisibility( View.GONE );
                        Toast.makeText( getContext(), "Favourite Place Added", Toast.LENGTH_SHORT ).show();

                        //addPlace.setVisibility( View.VISIBLE );

                    }

                }

                   // break;
                    } );


        getUserLocation();

        if(!checkPermission())
        {
            requestPermission();
        }
        else
        {
            fusedLocationProviderClient.requestLocationUpdates( locationRequest, locationCallback, Looper.myLooper() );

        }





            FragmentManager fm = getChildFragmentManager();
            fragment = (SupportMapFragment) fm.findFragmentById( R.id.map );
            if (fragment == null) {
                fragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace( R.id.map, fragment ).commit();
            }

            fragment.getMapAsync( this );












       // setFavouriteMarkers();

                AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if(autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields( Arrays.asList( Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS,Place.Field.ADDRESS_COMPONENTS));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    LatLng latLng = place.getLatLng();
                    if(latLng!=null) {
                        //mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng)
                                .title(place.getName())
                                .snippet(place.getAddress())
                                .icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_AZURE ) ));
                        CameraPosition cameraPosition = CameraPosition.builder()
                                .target( latLng )
                                .zoom( 15 )
                                .bearing( 0 )
                                .tilt( 45 )
                                .build();
                        mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );


                    }

                    //Toast.makeText(getContext(), "" + temp, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull Status status) {

                    Toast.makeText(getContext(), "" + status, Toast.LENGTH_SHORT).show();
                }
            });
        }













    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

                getUserLocation();
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }

        }
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace)
    {
        StringBuilder placeUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?"  );
        placeUrl.append( "location=" + latitude + "," + longitude );
        placeUrl.append( "&radius=" + RADIUS );
        placeUrl.append( "&type=" + nearbyPlace );
        //placeUrl.append( "&keyword=cruise" );
        placeUrl.append( "&key=" + getString(R.string.api_key_class ));
        return placeUrl.toString();
    }
    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_one, container, false);

            //onMapReady( mMap );



        }




    //    public void initmap()
//    {
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById( R.id.map );
//        mapFragment.getMapAsync(this );
//
//    }

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
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target( userLocation )
                            .zoom( 15 )
                            .bearing( 0 )
                            .tilt( 45 )
                            .build();


                    mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );
                    mMap.addMarker( new MarkerOptions().position( userLocation )
                            .title( "Your Location" ));

                    //setFavouriteMarkers();




                    // .icon( BitmapDescriptorFactory.fromResource( R.drawable.icon ) ));


                }
            }
        };
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL );

        setFavouriteMarkers();



        mMap.setOnMapLongClickListener( new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {



                mMap.addMarker( new MarkerOptions().position( latLng )
                        .title( getAddress( latLng ))
                        .draggable( true )
                        .snippet( "you are going there" )
                        .icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_AZURE ) ) );

//                //calendar
//                Calendar calendar = Calendar.getInstance();
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "YYY MM dd hh:mm:ss" );
//                String placeDate = simpleDateFormat.format( calendar.getTime() );
//
//                Places places = new Places( latLng.latitude, latLng.longitude,  placeDate);
//                //placesList.add( places );
//                PlacesDB placesDB = PlacesDB.getInstance(getContext());
//                placesDB.daoObjct().insert(places  );
//
//                setFavouriteMarkers();





            }
        } );


        mMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //addPlace.setVisibility( View.VISIBLE );


                latnew = marker.getPosition().latitude;
                longnew = marker.getPosition().longitude;
                addPlace.setVisibility( View.VISIBLE );
                visitPlace.setVisibility( View.VISIBLE );

//                if (placesList != null) {
//                    placesList.clear();
//                }
//
//                PlacesDB placesDB = PlacesDB.getInstance( getContext() );
//                placesList = placesDB.daoObjct().getDefault();
//
//                placesDB.daoObjct().getUserDetails().observe(getActivity(), new Observer<List<Places>>() {
//                    @Override
//                    public void onChanged( @Nullable List<Places> placeslist) {
//
//                        placesList= placeslist;
//
//                    }
//                });



//                for (int i = 0; i < placesList.size(); i++) {
//
//                    if(placesList.get( i ).getLat() ==  marker.getPosition().latitude  && placesList.get( i ).getLng() == marker.getPosition().longitude ) {
//
//                    addPlace.setVisibility( View.GONE );
//                    //break;
//                    }
//                    else
//                    {
//                        addPlace.setVisibility( View.VISIBLE );
//                        //break;
//                    }
//
//                    }


                return false;
            }
        } );

        mMap.setOnMapClickListener( new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addPlace.setVisibility( View.GONE );
                visitPlace.setVisibility( View.GONE );
            }
        } );

        mMap.setInfoWindowAdapter( new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View v = getLayoutInflater().inflate(R.layout.annotation_window, null);

                // Getting the position from the marker
                LatLng latLng = marker.getPosition();


                String address = getAddress( latLng );

                // Getting reference to the TextView to set latitude
                TextView addressTV = (TextView) v.findViewById(R.id.annotationText1);
                addressTV.setText( address );

                TextView latlngText = (TextView) v.findViewById( R.id.annotationText2 );
                latlngText.setText( "One of your Selected Location" );



                return v;
            }
        } );
    }




    private void setFavouriteMarkers() {

        if (placesList != null) {
            placesList.clear();
        }
        PlacesDB placesDB = PlacesDB.getInstance( getContext() );
        placesList = placesDB.daoObjct().getDefault();

        placesDB.daoObjct().getUserDetails().observe(this, new Observer<List<Places>>() {
            @Override
            public void onChanged( @Nullable List<Places> placeslist) {

                placesList= placeslist;

            }
        });



        for (int i = 0; i < placesList.size(); i++) {




            LatLng latLng = new LatLng( placesList.get( i ).getLat(), placesList.get( i ).getLng() );
            CameraPosition cameraPosition = CameraPosition.builder()
                    .target( latLng )
                    .zoom( 15 )
                    .bearing( 0 )
                    .tilt( 45 )
                    .build();
            //mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );

            String address = getAddress( latLng );

            //get address


            mMap.addMarker( new MarkerOptions().position( latLng )
                    .title( address)
                    .draggable( true )
                    .snippet( "you are going there" )
                    .icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_AZURE ) ) );

        }
    }

    private String getAddress(LatLng latLng)
    {
        String address = "";

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try
        {
            List<Address> addresses = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 );
            if (addresses != null && addresses.size() > 0)
            {
                Log.i(TAG, "on Location Result:" + addresses.get(0));
                if (addresses.get(0).getThoroughfare() != null)
                {
                    address += " " + addresses.get(0).getThoroughfare();

                }


                if (addresses.get(0).getLocality() != null)
                {
                    address += " " + addresses.get(0).getLocality();

                }

                if (addresses.get(0).getAdminArea() != null)
                {
                    address += " " + addresses.get(0).getAdminArea();
                }

//                if (addresses.get(0).getCountryName() != null)
//                {
//                    address +=  " " + addresses.get(0).getCountryName();
//
//                }
//                if (addresses.get(0).getPostalCode() != null)
//                {
//                    address += " " + addresses.get(0).getPostalCode();
//
//                }

                //Toast.makeText(getContext(), address, Toast.LENGTH_SHORT).show();


            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
return address;

    }

    public void fire() {

        setFavouriteMarkers();

        System.out.println(">>>>>>>>>>>");
    }
}
