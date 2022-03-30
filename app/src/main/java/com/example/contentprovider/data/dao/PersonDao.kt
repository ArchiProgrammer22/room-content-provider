package com.example.contentprovider.data.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.contentprovider.data.entities.Person

@Dao
interface PersonDao {
    @Query("SELECT COUNT(*) FROM Person")
    fun personCount(): Int

    @Query("SELECT * FROM Person")
    fun getAll(): Cursor

    @Query("SELECT * FROM Person WHERE id = :id")
    fun getById(id: Long): Cursor

    @Query("DELETE FROM Person WHERE id = :id")
    fun deleteById(id: Long): Int

    @Insert
    fun insertPerson(person: Person): Long

    @Update
    fun updatePerson(person: Person): Int
}