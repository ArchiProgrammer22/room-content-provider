package com.example.contentprovider.presentation.activity

import android.content.ContentValues
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.contentprovider.R
import com.example.contentprovider.data.entities.Person
import com.example.contentprovider.presentation.provider.PersonContentProvider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val contentResolver = contentResolver
        addPerson(Person("Dmitry", "Protasevich"))
        addPerson(Person("Test", "Test"))
        val cursor = contentResolver.query(
            PersonContentProvider.URI_PERSON_TABLE,
            null,
            null,
            null,
            null
        )
        Toast.makeText(this, cursor?.count?.toString(), Toast.LENGTH_SHORT).show()
        cursor?.close()
    }

    private fun addPerson(person: Person) {
        val contentValues = ContentValues()
        contentValues.put("name", person.name)
        contentValues.put("surname", person.surname)
        contentResolver.insert(PersonContentProvider.URI_PERSON_TABLE, contentValues)
        Toast.makeText(this, "Added new person", Toast.LENGTH_SHORT).show()
    }
}