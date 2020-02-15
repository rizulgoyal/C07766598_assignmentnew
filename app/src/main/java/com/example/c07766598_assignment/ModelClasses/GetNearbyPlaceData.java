package com.example.c07766598_assignment.ModelClasses;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class GetNearbyPlaceData extends AsyncTask<Object, String, String> {

    GoogleMap googleMap;
    String url;
    String placeData;
    String address;
    LatLng latLng;

    LatLng latLngUser;

    @Override

    protected String doInBackground(Object... objects) {
        googleMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLngUser = (LatLng) objects[2];
        latLng = (LatLng) objects[3];
        address = (String)objects[4];


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



        showNearByPlaces( nearByPlaceList );
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
