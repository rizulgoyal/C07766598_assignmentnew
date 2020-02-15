package com.example.c07766598_assignment.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.c07766598_assignment.Activities.PlaceDetailActivity;
import com.example.c07766598_assignment.Fragments.Tab1mapFragment;
import com.example.c07766598_assignment.MainActivity;
import com.example.c07766598_assignment.ModelClasses.Places;
import com.example.c07766598_assignment.OnSwipeTouchListener;
import com.example.c07766598_assignment.R;
import com.example.c07766598_assignment.RoomDatabase.PlacesDB;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder>{

   List<Places> placesList;
   Context context;

    public PlacesAdapter(Context context) {
        this.context = context;
    }

    public List<Places> getPlacesList() {
        return placesList;
    }

    public void setPlacesList(List<Places> placesList) {
        this.placesList = placesList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.places_cell_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        PlacesDB placesDB = PlacesDB.getInstance(getContext());
//        placesList = placesDB.daoObjct().getDefault();

        final Places mydata = placesList.get(position);

        //get address

        String address = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try
        {
            List<Address> addresses = geocoder.getFromLocation( mydata.getLat(), mydata.getLng(), 1 );
            if (addresses != null && addresses.size() > 0)
            {
                Log.i(TAG, "on Location Result:" + addresses.get(0));
//                if (addresses.get(0).getThoroughfare() != null)
//                {
//                    address += " " + addresses.get(0).getThoroughfare();
//
//                }
//                if (addresses.get(0).getAdminArea() != null)
//                {
//                    address += addresses.get(0).getAdminArea();
//                }

                if (addresses.get(0).getLocality() != null)
                {
                    address += " " + addresses.get(0).getLocality();

                }
                if (addresses.get(0).getCountryName() != null)
                {
                    address +=  " " + addresses.get(0).getCountryName();

                }
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


        holder.lat.setText("Lat: "+ mydata.getLat().toString());
        holder.lng.setText( "Lng: " + mydata.getLng().toString() );

        if(address.equalsIgnoreCase( "" ))
        {
            holder.address.setText("Address not found");
            holder.address.setTextColor( Color.RED );

        }
        else {
            holder.address.setText( "Address: " + address );
        }
        holder.date.setText("Date: "+mydata.getDate());

        if(mydata.getVisited())
        {
            holder.visitPlace.setVisibility( View.VISIBLE );

        }
        else
        {
            holder.visitPlace.setVisibility( View.GONE );
        }

        holder.mycardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myintent = new Intent(context, PlaceDetailActivity.class);
                myintent.putExtra("place", mydata);

                context.startActivity(myintent);
                //  Toast.makeText(context,"position = "+position,Toast.LENGTH_LONG).show();

            }
        });





    }

    public void updateData(List<Places> viewModels) {
        placesList.clear();
        placesList.addAll(viewModels);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return placesList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView lat, address, date, lng;
        CardView mycardview;
        ImageView visitPlace;

        public ViewHolder(@NonNull View itemView) {


            super(itemView);

            visitPlace = itemView.findViewById( R.id.visit_image );
            mycardview = itemView.findViewById(R.id.newcard);
            lat = itemView.findViewById( R.id.textView1);
            lng = itemView.findViewById( R.id.textView4 );
            address = itemView.findViewById(R.id.textView2);
            date = itemView.findViewById(R.id.textView3);




        }
    }

    public void deleteItem(int position) {

        Places places = placesList.get(position);
        PlacesDB userDatabase = PlacesDB.getInstance(getContext());
        userDatabase.daoObjct().delete(places);
        Toast.makeText(getContext(),"Deleted",Toast.LENGTH_SHORT).show();
        placesList.remove(position);
        notifyDataSetChanged();

        ((MainActivity)context).fire();
    }
}
