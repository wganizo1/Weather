package com.wganizo.weather.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Database constants
private const val DATABASE_NAME = "preferences.db"
private const val DATABASE_VERSION = 1

// Table and column names
private const val TABLE_UNITS = "units"
private const val COLUMN_METRIC_IMPERIAL = "metric_imperial"

// SQL statement to create the units table
private const val SQL_CREATE_TABLE_UNITS = """
    CREATE TABLE $TABLE_UNITS (
        $COLUMN_METRIC_IMPERIAL TEXT DEFAULT 'metric'
    )
"""

class PreferencesDatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
                // Create the units table
                db.execSQL(SQL_CREATE_TABLE_UNITS)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
                // Handle database upgrade as needed
                db.execSQL("DROP TABLE IF EXISTS $TABLE_UNITS")
                onCreate(db)
        }

        // Function to get the value of COLUMN_METRIC_IMPERIAL
        fun getUnitPreference(): String {
                val db = this.readableDatabase
                val cursor: Cursor = db.query(
                        TABLE_UNITS,
                        arrayOf(COLUMN_METRIC_IMPERIAL),
                        null,
                        null,
                        null,
                        null,
                        null
                )

                var unitPreference = "imperial" // Default value
                if (cursor.moveToFirst()) {
                        unitPreference = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_METRIC_IMPERIAL))
                }
                cursor.close()
                return unitPreference
        }

        // Function to update the value of COLUMN_METRIC_IMPERIAL
        fun updateUnitPreference(newUnit: String): Int {
                val db = this.writableDatabase
                val values = ContentValues().apply {
                        put(COLUMN_METRIC_IMPERIAL, newUnit)
                }
                return db.update(
                        TABLE_UNITS,
                        values,
                        null,
                        null
                )
        }
}
