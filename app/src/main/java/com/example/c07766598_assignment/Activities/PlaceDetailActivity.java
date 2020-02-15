package com.example.c07766598_assignment.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.c07766598_assignment.ModelClasses.GetDirectionData;
import com.example.c07766598_assignment.ModelClasses.GetNearbyPlaceData;
import com.example.c07766598_assignment.ModelClasses.Places;
import com.example.c07766598_assignment.R;
import com.example.c07766598_assignment.RoomDatabase.PlacesDB;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class PlaceDetailActivity extends AppCompatActivity implements OnMapReadyCallback{
    GoogleMap mMap;
    LinearLayout linearLayoutText;

    TextView distance, duration;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    private final int REQUEST_CODE = 1;

    String mapType;

    double latitude, longitude;
    double destLat, destLong;
    String addressDest;
    final int RADIUS = 1500;
    public static boolean directionRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_place_detail );
        linearLayoutText = findViewById( R.id.layoutToHide );

        distance = findViewById( R.id.distance_text );
        duration = findViewById( R.id.duration_text );

        initmap();
        getUserLocation();
        if(!checkPermission())
        {
            requestPermission();
        }
        else
        {
            fusedLocationProviderClient.requestLocationUpdates( locationRequest, locationCallback, Looper.myLooper() );
        }




    }

        public void initmap()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.mapDetail );
        mapFragment.getMapAsync(this );

    }


    private Boolean checkPermission()
    {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION );
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE );

    }

    private void getUserLocation()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this );
        locationRequest = new LocationRequest();
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        locationRequest.setInterval( 5000 );
        locationRequest.setFastestInterval( 3000 );
        locationRequest.setSmallestDisplacement( 10 );
        setHomeMarker();
        //setFavouriteMarkers();


    }

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


//                    CameraPosition cameraPosition = CameraPosition.builder()
//                            .target( userLocation )
//                            .zoom( 15 )
//                            .bearing( 0 )
//                            .tilt( 45 )
//                            .build();
//                    mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );
                    mMap.addMarker( new MarkerOptions().position( userLocation )
                            .title( "Your Location" ));




                    // .icon( BitmapDescriptorFactory.fromResource( R.drawable.icon ) ));


                }
            }
        };
    }


    public void setMarker()
    {
        Places myPlace = getIntent().getParcelableExtra("place");

        String address = "";


        destLat = myPlace.getLat();
        destLong = myPlace.getLng();
        LatLng latLng = new LatLng( myPlace.getLat(), myPlace.getLng() );
        CameraPosition cameraPosition = CameraPosition.builder()
                .target( latLng )
                .zoom( 15 )
                .bearing( 0 )
                .tilt( 45 )
                .build();
        mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );

        //get address

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
                if (addresses.get(0).getAdminArea() != null)
                {
                    address += addresses.get(0).getAdminArea();
                }
                if (addresses.get(0).getCountryName() != null)
                {
                    address +=  " " + addresses.get(0).getCountryName();

                }
                if (addresses.get(0).getLocality() != null)
                {
                    address += " " + addresses.get(0).getLocality();

                }
                if (addresses.get(0).getPostalCode() != null)
                {
                    address += " " + addresses.get(0).getPostalCode();

                }

                addressDest = address;

                Toast.makeText(this, address, Toast.LENGTH_SHORT).show();


            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        Toast.makeText( this, myPlace.getLat().toString(), Toast.LENGTH_SHORT ).show();

        mMap.addMarker( new MarkerOptions().position( latLng )
                .title( address)
                .draggable( true )
                .snippet( "you are going there" )
                .icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_AZURE ) ) );
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType( GoogleMap.MAP_TYPE_NORMAL );

        setMarker();

        mMap.setOnMarkerDragListener( new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {




            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                destLat = marker.getPosition().latitude;
                destLong = marker.getPosition().longitude;


                Places myPlace = getIntent().getParcelableExtra("place");

                myPlace.setLat( destLat );
                myPlace.setLng( destLong );


                PlacesDB placesDB = PlacesDB.getInstance(getBaseContext());
                placesDB.daoObjct().update( myPlace );



            }
        } );

        mMap.setOnMapClickListener( new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText( getBaseContext(), String.valueOf(  latLng.latitude) , Toast.LENGTH_SHORT);


            }
        } );


        mMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Toast.makeText( getBaseContext(), "marker clicked" , Toast.LENGTH_SHORT);

                return false;
            }
        } );




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


    private String getDirectionUrl(double latitude, double longitude, double destlatitude, double destlongitude)
    {
        StringBuilder placeUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?"  );
        placeUrl.append( "origin=" + latitude + "," + longitude );
        placeUrl.append( "&destination=" + destlatitude + "," + destlongitude );

        //placeUrl.append( "&keyword=cruise" );
        placeUrl.append( "&key=" + getString(R.string.api_key_class ));
        return placeUrl.toString();
    }


    public void btnClick(View view)
    {



        switch (view.getId())
        {
            case R.id.btn_restaurant:


                linearLayoutText.setVisibility( View.GONE );


                // new view inflate for selecting nearby places option

                AlertDialog.Builder builder = new AlertDialog.Builder( this );
                // builder.setTitle( "Edit Employee" );
                LayoutInflater inflater = LayoutInflater.from( this );
                View v = inflater.inflate( R.layout.select_nearbyplace_layout,null );
                builder.setView( v );
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                final Spinner spinnerDept = v.findViewById( R.id.spinnerPlacesNearby );
                Button buttonEdit = v.findViewById( R.id.button_shownearby );

                buttonEdit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Object[] dataTransfer;
                        // get the url from place api
                        String url = getUrl( destLat, destLong, spinnerDept.getSelectedItem().toString());
                        //  Log.i("MainActivity", url);
                        // setmarkers( url );
                        dataTransfer = new Object[5];
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;
                        dataTransfer[2] = new LatLng( destLat, destLong );
                        dataTransfer[3] = new LatLng( latitude, longitude );
                        dataTransfer[4] = addressDest;

                        GetNearbyPlaceData getNearbyPlaceData = new GetNearbyPlaceData();
                        getNearbyPlaceData.execute(dataTransfer);
                        Toast.makeText(v.getContext() , spinnerDept.getSelectedItem().toString(), Toast.LENGTH_SHORT ).show();


                        alertDialog.dismiss();



                    }
                } );



                               break;
            case R.id.btn_distance:

                Object[] dataTransfer;

                String direction_url = getDirectionUrl( latitude, longitude, destLat, destLong );
                Log.i("MainActivity", direction_url);
                dataTransfer = new Object[6];
                dataTransfer[0] = mMap;
                dataTransfer[1] = direction_url;

                dataTransfer[2] = new LatLng( destLat, destLong );
                dataTransfer[3] = new LatLng( latitude, longitude );
                dataTransfer[4] = distance;
                dataTransfer[5] = duration;

                GetDirectionData getDirectionData = new GetDirectionData();
                getDirectionData.execute( dataTransfer );
                linearLayoutText.setVisibility( View.VISIBLE );
                Toast.makeText( this, "Distance", Toast.LENGTH_SHORT ).show();

                break;

            case R.id.btn_mapType:

                linearLayoutText.setVisibility( View.GONE );


                AlertDialog.Builder builder1 = new AlertDialog.Builder( this );
                // builder.setTitle( "Edit Employee" );
                LayoutInflater inflater1 = LayoutInflater.from( this );

                View v1 = inflater1.inflate( R.layout.select_maptype_layout,null );
                builder1.setView( v1 );
                final AlertDialog alertDialog1 = builder1.create();
                alertDialog1.show();
                final Spinner spinnerMap = v1.findViewById( R.id.spinnerMapTypes );
                Button buttonChangeMap = v1.findViewById( R.id.button_showmapType );

                buttonChangeMap.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mapType = spinnerMap.getSelectedItem().toString();
        if (mapType.equalsIgnoreCase( "Normal" ))
        {
            mMap.setMapType( GoogleMap.MAP_TYPE_NORMAL );

        }
        else if (mapType.equalsIgnoreCase( "Satellite" ))
        {
            mMap.setMapType( GoogleMap.MAP_TYPE_SATELLITE );

        }
        else if (mapType.equalsIgnoreCase( "Terrain" ))
        {
            mMap.setMapType( GoogleMap.MAP_TYPE_TERRAIN );

        }
        else {

            mMap.setMapType( GoogleMap.MAP_TYPE_HYBRID );
        }

                        alertDialog1.dismiss();



                    }
                } );


                break;

        }
    }


}
