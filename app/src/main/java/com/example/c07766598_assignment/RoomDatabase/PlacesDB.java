package com.example.c07766598_assignment.RoomDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.c07766598_assignment.ModelClasses.Places;

@Database(entities = Places.class , exportSchema = false , version = 2)

public abstract class PlacesDB extends RoomDatabase {


        public static final String DB_NAME = "user_db";

        private static  PlacesDB uInstance;


        public static PlacesDB getInstance(Context context)
        {
            if(uInstance == null)
            {
                uInstance = Room.databaseBuilder(context.getApplicationContext(),PlacesDB.class,PlacesDB.DB_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();
            }
            return uInstance;
        }


        public abstract PlacesDao daoObjct();
    }

