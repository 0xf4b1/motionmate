/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.motionmate.util

import android.content.ContentValues
import android.content.Context
import android.database.Cursor.*
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.*

/**
 * Database to store step data when a day is over.
 *
 * Created by tiefensuche on 19.10.16.
 */
internal class Database private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private fun query(columns: Array<String>, selection: String? = null, selectionArgs: Array<String>? = null): Number {
        val cursor = readableDatabase.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null)
        cursor.moveToFirst()
        val result: Number = when (cursor.getType(0)) {
            FIELD_TYPE_INTEGER -> cursor.getLong(0)
            FIELD_TYPE_FLOAT -> cursor.getFloat(0)
            FIELD_TYPE_NULL -> 0
            else -> throw IllegalStateException("unexpected type")
        }
        cursor.close()
        return result
    }

    internal val firstEntry: Long
        get() {
            return query(arrayOf("min(timestamp)")).toLong()
        }


    internal val lastEntry: Long
        get() {
            return query(arrayOf("max(timestamp)")).toLong()
        }

    internal val avgSteps: Float
        get() {
            return query(arrayOf("avg(steps)")).toFloat()
        }

    internal fun getSumSteps(minDate: Long) = query(arrayOf("sum(steps)"), "timestamp > ?", arrayOf(minDate.toString())).toInt()

    internal fun addEntry(timestamp: Long, steps: Int) {
        Log.d(TAG, "add entry to database: $timestamp, $steps")
        val values = ContentValues()
        values.put("timestamp", timestamp)
        values.put("steps", steps)
        try {
            writableDatabase.insertOrThrow(TABLE_NAME, null, values)
        } catch (e: SQLException) {
            Log.e(TAG, "Could not add data to database!", e)
        }
    }

    internal fun getEntries(minDate: Long, maxDate: Long): List<Entry> {
        val entries = ArrayList<Entry>()
        val cursor = readableDatabase.query(TABLE_NAME, null, "timestamp >= ? AND timestamp <= ?", arrayOf(minDate.toString(), maxDate.toString()), null, null, null)
        while (cursor.moveToNext()) {
            val cal = Util.calendar
            cal.timeInMillis = cursor.getLong(0)
            entries.add(Entry(cal.timeInMillis, cursor.getInt(1)))
        }
        cursor.close()
        return entries
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        // no-op
    }

    internal inner class Entry internal constructor(val timestamp: Long, val steps: Int)

    companion object {

        private val TAG = Database::class.java.simpleName
        private const val DATABASE_NAME = "MotionMate"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "History"
        private const val DATABASE_CREATE = "create table if not exists $TABLE_NAME (timestamp long primary key, steps int not null);"

        private var instance: Database? = null

        internal fun getInstance(context: Context): Database {
            var instance = instance
            if (instance == null) {
                instance = Database(context)
                this.instance = instance
            }
            return instance
        }
    }
}
