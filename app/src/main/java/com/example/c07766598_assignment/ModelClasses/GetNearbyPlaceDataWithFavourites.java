package com.example.c07766598_assignment.ModelClasses;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.example.c07766598_assignment.RoomDatabase.PlacesDB;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class GetNearbyPlaceDataWithFavourites extends AsyncTask<Object, String, String> {

    GoogleMap googleMap;
    String url;
    String placeData;
    String address;
    LatLng latLng;
    Context context;

    List<Places> placesList;


    LatLng latLngUser;

    @Override

    protected String doInBackground(Object... objects) {
        googleMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLngUser = (LatLng) objects[2];
        latLng = (LatLng) objects[3];
        address = (String)objects[4];
        placesList = (List<Places>) objects[5];
        context = (Context) objects[6];


        //placeData = (String) objects[2];
        //FetchURL fetchURL = new FetchURL();
        try {
            placeData = FetchURL.readURL( url );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return placeData;
    }


    @Override
    protected void onPostExecute(String s) {
        googleMap.clear();
        List<HashMap<String, String>> nearByPlaceList = null;
        DataParser parser = new DataParser();
        nearByPlaceList = parser.parse( s );

        MarkerOptions options = new MarkerOptions().position( latLng )
                .title( "your location");


        googleMap.addMarker( options );

        googleMap.addMarker( new MarkerOptions().position( latLngUser )
                .title( address)
                .draggable( true )
                .snippet( "you are going there" )
                .icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_AZURE ) ) );

setFavouriteMarkers();

        showNearByPlaces( nearByPlaceList );
    }

    private void setFavouriteMarkers() {
//
//        if (placesList != null) {
//            placesList.clear();
//        }
//        PlacesDB placesDB = PlacesDB.getInstance();
//        placesList = placesDB.daoObjct().getDefault();
//
//        placesDB.daoObjct().getUserDetails().observe(this, new Observer<List<Places>>() {
//            @Override
//            public void onChanged( @Nullable List<Places> placeslist) {
//
//                placesList= placeslist;
//
//            }
//        });



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


            googleMap.addMarker( new MarkerOptions().position( latLng )
                    .title( address)
                    .draggable( true )
                    .snippet( "you are going there" )
                    .icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_AZURE ) ) );

        }
    }

    private String getAddress(LatLng latLng)
    {
        String address = "";

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
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

                Toast.makeText(context, address, Toast.LENGTH_SHORT).show();


            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return address;

    }







    private void  showNearByPlaces(List<HashMap<String, String>> nearbyList){
        for(int i = 0; i <nearbyList.size();i++)
        {
            //Log.i("MainActivity", String.valueOf( nearbyList.size()));
            HashMap<String, String> place = nearbyList.get( i );

            String placeName = place.get( "placeName" );
            String vicinity = place.get( "vicinity" );
            double lat = Double.parseDouble( place.get( "lat" ) );
            double lng = Double.parseDouble( place.get( "lng" ) );
            String reference = place.get( "reference" );

            LatLng location = new LatLng( lat, lng );
            MarkerOptions marker = new MarkerOptions().position( location )
                    .title( placeName )

                    .icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE) );
            googleMap.addMarker( marker );

//            CameraPosition cameraPosition = CameraPosition.builder()
//                    .target( location )
//                    .zoom( 15 )
//                    .bearing( 0 )
//                    .tilt( 45 )
//                    .build();
//            googleMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );


        }
    }
}
