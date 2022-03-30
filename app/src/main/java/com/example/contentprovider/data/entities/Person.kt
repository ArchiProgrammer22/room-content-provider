package com.example.contentprovider.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Person(
    @ColumnInfo val name: String,
    @ColumnInfo val surname: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}