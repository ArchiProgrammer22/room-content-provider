package com.example.contentprovider.presentation.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import com.example.contentprovider.data.PersonDatabase
import com.example.contentprovider.data.dao.PersonDao
import com.example.contentprovider.data.entities.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class PersonContentProvider : ContentProvider() {

    private lateinit var dbDao: PersonDao

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when (MATCHER.match(uri)) {
            DIR_CODE -> throw IllegalArgumentException("Cannot update with id")
            ITEM_CODE -> {
                val deferredCount = CoroutineScope(Dispatchers.IO).async {
                    dbDao.deleteById(ContentUris.parseId(uri))
                }
                context?.contentResolver?.notifyChange(uri, null)
                val count: Int
                runBlocking { count = deferredCount.await() }
                return count
            }
            else -> throw IllegalArgumentException("Invalid URI $uri")
        }
    }

    override fun getType(uri: Uri): String {
        return when (MATCHER.match(uri)) {
            DIR_CODE -> "vnd.android.cursor.dir/$AUTHORITY.person"
            ITEM_CODE -> "vnd.android.cursor.item/$AUTHORITY.person"
            else -> throw IllegalArgumentException("Invalid URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        return when (MATCHER.match(uri)) {
            DIR_CODE -> {

                val deferredId = CoroutineScope(Dispatchers.IO).async {
                    dbDao.insertPerson(
                        Person(
                            values?.getAsString("name")!!,
                            values.getAsString("surname")
                        )
                    )
                }
                val id: Long
                runBlocking { id = deferredId.await() }
                context?.contentResolver?.notifyChange(uri, null)
                ContentUris.withAppendedId(uri, id)
            }
            ITEM_CODE -> throw IllegalArgumentException("Invalid ID")
            else -> throw IllegalArgumentException("Invalid URI $uri")
        }
    }

    override fun onCreate(): Boolean {
        val db = Room.databaseBuilder(context!!, PersonDatabase::class.java, "person-db")
            .build()
        dbDao = db.getPersonDao()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor {
        val deferredCursor = CoroutineScope(Dispatchers.IO).async {
            when (MATCHER.match(uri)) {
                DIR_CODE -> dbDao.getAll()
                ITEM_CODE -> dbDao.getById(ContentUris.parseId(uri))
                else -> throw IllegalArgumentException("Invalid uri: $uri")
            }
        }
        val cursor: Cursor
        runBlocking { cursor = deferredCursor.await() }
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return when (MATCHER.match(uri)) {
            DIR_CODE -> throw IllegalArgumentException("Cannot update without ID")
            ITEM_CODE -> {
                val deferredCount = CoroutineScope(Dispatchers.IO).async {
                    dbDao.updatePerson(
                        Person(
                            values?.getAsString("name")!!,
                            values.getAsString("surname")
                        )
                    )
                }
                val count: Int
                runBlocking {
                    count = deferredCount.await()
                }
                context?.contentResolver?.notifyChange(uri, null)
                count
            }
            else -> throw IllegalArgumentException("Invalid URI $uri")
        }
    }

    companion object {
        const val AUTHORITY = "com.example.contentprovider.questions"
        val URI_PERSON_TABLE: Uri = Uri.parse("content://person")
        private const val DIR_CODE = 1
        private const val ITEM_CODE = 2
        private val MATCHER: UriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "person", DIR_CODE)
            addURI(AUTHORITY, "person", ITEM_CODE)
        }
    }
}