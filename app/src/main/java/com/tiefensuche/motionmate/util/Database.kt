/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.motionmate.util

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.tiefensuche.motionmate.BuildConfig
import java.util.*

/**
 * Database to store step data when a day is over.
 *
 * Created by tiefensuche on 19.10.16.
 */
internal class Database private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    internal val firstEntry: Long
        get() {
            val cursor = readableDatabase.query(TABLE_NAME, arrayOf("min(timestamp)"), null, null, null, null, null)
            cursor.moveToFirst()
            val result = cursor.getLong(0)
            cursor.close()
            return result
        }

    internal val lastEntry: Long
        get() {
            val cursor = readableDatabase.query(TABLE_NAME, arrayOf("max(timestamp)"), null, null, null, null, null)
            cursor.moveToFirst()
            val result = cursor.getLong(0)
            cursor.close()
            return result
        }

    internal val avgSteps: Float
        get() {
            val cursor = readableDatabase.query(TABLE_NAME, arrayOf("avg(steps)"), null, null, null, null, null)
            cursor.moveToFirst()
            val result = cursor.getFloat(0)
            cursor.close()
            return result
        }

    internal val sumSteps: Int
        get() = getSumSteps(0)

    init {
        if (BuildConfig.DEBUG) {
            context.deleteDatabase(DATABASE_NAME)
            generateEntries()
        }
    }

    internal fun addEntry(timestamp: Long, steps: Int) {
        LogHelper.d(TAG, "add entry to database: $timestamp, $steps")
        val values = ContentValues()
        values.put("timestamp", timestamp)
        values.put("steps", steps)
        try {
            writableDatabase.insertOrThrow(TABLE_NAME, null, values)
        } catch (e: SQLException) {
            LogHelper.e(TAG, e, "Could not add data to database!")
        }

    }

    internal fun getEntries(minDate: Long, maxDate: Long): List<Entry> {
        val entries = ArrayList<Entry>()
        val cursor = readableDatabase.query(TABLE_NAME, null, "timestamp >= ? AND timestamp <= ?", arrayOf(java.lang.Long.toString(minDate), java.lang.Long.toString(maxDate)), null, null, null)
        while (cursor.moveToNext()) {
            val cal = Util.calendar
            cal.timeInMillis = cursor.getLong(0)
            entries.add(Entry(cal.timeInMillis, cursor.getInt(1)))
        }
        cursor.close()
        return entries
    }

    internal fun getSumSteps(minDate: Long): Int {
        val cursor = readableDatabase.query(TABLE_NAME, arrayOf("sum(steps)"), "timestamp > ?", arrayOf(java.lang.Long.toString(minDate)), null, null, null)
        cursor.moveToFirst()
        val result = cursor.getInt(0)
        cursor.close()
        return result
    }

    private fun generateEntries() {
        val random = Random()
        val cal = Util.calendar
        cal.add(Calendar.DAY_OF_MONTH, -101)
        for (i in 0..98) {
            cal.add(Calendar.DAY_OF_MONTH, 1)
            addEntry(cal.timeInMillis, random.nextInt(8000))
        }
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

    }

    internal inner class Entry internal constructor(val timestamp: Long, val steps: Int)

    companion object {

        private val TAG = LogHelper.makeLogTag(Database::class.java)
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
