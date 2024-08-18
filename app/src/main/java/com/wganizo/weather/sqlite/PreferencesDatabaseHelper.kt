package com.wganizo.weather.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DATABASE_NAME = "preferences.db"
private const val DATABASE_VERSION = 1
private const val TABLE_UNITS = "units"
private const val COLUMN_METRIC_IMPERIAL = "metric_imperial"
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
                // No need to insert a default row here
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

                var unitPreference = "metric" // Default value
                if (cursor.moveToFirst()) {
                        unitPreference = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_METRIC_IMPERIAL))
                } else {
                        // Insert default row if table is empty
                        val values = ContentValues().apply {
                                put(COLUMN_METRIC_IMPERIAL, unitPreference)
                        }
                        db.insert(TABLE_UNITS, null, values)
                }
                cursor.close()
                return unitPreference
        }

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
