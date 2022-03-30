package com.example.contentprovider.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.contentprovider.data.dao.PersonDao
import com.example.contentprovider.data.entities.Person

@Database(entities = [Person::class], version = 1)
abstract class PersonDatabase : RoomDatabase() {
    abstract fun getPersonDao(): PersonDao
}