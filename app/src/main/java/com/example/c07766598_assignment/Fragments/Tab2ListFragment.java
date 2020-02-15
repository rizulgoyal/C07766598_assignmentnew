package com.example.c07766598_assignment.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.c07766598_assignment.Adapters.PlacesAdapter;
import com.example.c07766598_assignment.ModelClasses.Places;
import com.example.c07766598_assignment.R;
import com.example.c07766598_assignment.RoomDatabase.PlacesDB;
import com.example.c07766598_assignment.SwipeToDeleteCallbackForPlaces;

import java.util.List;

public class Tab2ListFragment extends Fragment{



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate( R.layout.fragment_two, container, false);


        }

    @Override
    public void onResume() {
        super.onResume();


//        RecyclerView recyclerView = getView().findViewById(R.id.recyclerPlaces);
//        final PlacesAdapter placesAdapter = new PlacesAdapter(getContext());
//        PlacesDB placesDB = PlacesDB.getInstance(getContext());
//        List<Places> places = placesDB.daoObjct().getDefault();
//        placesAdapter.updateData( places );


    }


    @Override
    public void onStart() {
        super.onStart();

//
//        final PlacesAdapter placesAdapter = new PlacesAdapter(getContext());
//        PlacesDB placesDB = PlacesDB.getInstance(getContext());
//        List<Places> places = placesDB.daoObjct().getDefault();
//        placesAdapter.updateData( places );


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );

        RecyclerView recyclerView = view.findViewById(R.id.recyclerPlaces);
        final PlacesAdapter placesAdapter = new PlacesAdapter(getActivity());
        PlacesDB placesDB = PlacesDB.getInstance(getActivity());
        placesAdapter.setPlacesList(placesDB.daoObjct().getDefault());
        recyclerView.setAdapter(placesAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);



        placesDB.daoObjct().getUserDetails().observe(this, new Observer<List<Places>>() {
            @Override
            public void onChanged( @Nullable List<Places> places) {


                placesAdapter.setPlacesList(places);
                placesAdapter.notifyDataSetChanged();
                Tab1mapFragment tab1mapFragment = new Tab1mapFragment();
                tab1mapFragment.placesList = places;
            }
        });

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallbackForPlaces(placesAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }
}
