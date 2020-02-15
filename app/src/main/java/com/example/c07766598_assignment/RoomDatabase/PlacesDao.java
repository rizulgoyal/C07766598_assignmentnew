package com.example.c07766598_assignment.RoomDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.c07766598_assignment.ModelClasses.Places;

import java.util.List;
@Dao
public interface PlacesDao {



        @Insert
        void insert(Places places);

        @Delete
        void delete(Places places);

        @Update
        void update(Places places);

        @Query("Select count(id) from places")
        Integer count();

        @Query("Select * from places")
        LiveData<List<Places>> getUserDetails();

        @Query("Select * from places")
        List<Places> getDefault();

//        @Query("Select * from employee where id = :id")
//        LiveData<Employee> getCurrentUserDetails(Integer id);
    }

