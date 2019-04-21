/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.motionmate.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.text.format.DateUtils
import android.util.SparseArray
import android.widget.Toast
import com.tiefensuche.motionmate.R
import com.tiefensuche.motionmate.ui.MainActivity
import com.tiefensuche.motionmate.util.Database
import com.tiefensuche.motionmate.util.LogHelper
import com.tiefensuche.motionmate.util.Util
import java.util.*

/**
 * The background service of the step counter app. It binds the sensors and counts in the background.
 * The [MainActivity] starts the service and connects to it to receive updates.
 *
 *
 * Created by tiefensuche on 06.11.16.
 */
internal class MotionService : Service() {
    private lateinit var sharedPreferences: SharedPreferences
    // steps at the current day
    private var mTodaysSteps: Int = 0
    // steps reported from sensor
    private var mCurrentSteps: Int = 0
    // steps reported from sensor STEP_COUNTER in previous event
    private var mLastSteps = -1
    // current date of counting
    private var mCurrentDate: Long = 0
    private var receiver: ResultReceiver? = null
    private lateinit var simpleStepDetector: StepDetector
    private lateinit var mListener: SensorEventListener
    private lateinit var mNotificationManager: NotificationManager
    private lateinit var mBuilder: NotificationCompat.Builder
    private var motionActivities: SparseArray<MotionActivity> = SparseArray()
    private var motionActivityId = 0

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        LogHelper.d(TAG, "Creating MotionService")

        startService()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // get last saved date
        mCurrentDate = sharedPreferences.getLong(KEY_DATE, Util.calendar.timeInMillis)
        // get last steps
        mTodaysSteps = sharedPreferences.getInt(KEY_STEPS, 0)

        val manager = packageManager

        // connect sensor
        val mSensorManager = getSystemService(Context.SENSOR_SERVICE) as? SensorManager ?: throw RuntimeException()
        var mStepSensor: Sensor? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
            // androids built in step counter
            LogHelper.d(TAG, "using sensor step counter")
            mStepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            mListener = object : SensorEventListener {

                override fun onSensorChanged(event: SensorEvent) {
                    handleEvent(event.values[0].toInt())
                }

                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

                }
            }
        } else if (manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
            // fallback sensor
            LogHelper.d(TAG, "using fallback sensor accelerometer")
            mStepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            simpleStepDetector = StepDetector(object : StepDetector.StepListener {
                override fun step(timeNs: Long) {
                    handleEvent(mCurrentSteps + 1)
                }
            })
            mListener = object : SensorEventListener {

                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        simpleStepDetector.updateAccel(
                                event.timestamp, event.values[0], event.values[1], event.values[2])
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

                }
            }
        }

        if (mStepSensor != null) {
            mSensorManager.registerListener(mListener, mStepSensor,
                    SensorManager.SENSOR_DELAY_FASTEST)
        } else {
            Toast.makeText(this, "Sorry, needed sensor is not present on your device", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleEvent(value: Int) {
        mCurrentSteps = value
        if (mLastSteps == -1) {
            mLastSteps = value
        }
        mTodaysSteps += value - mLastSteps
        mLastSteps = value
        handleEvent()
    }

    private fun handleEvent() {
        // Check if new day started
        if (!DateUtils.isToday(mCurrentDate)) {

            // Add record for the day to the database
            Database.getInstance(this).addEntry(mCurrentDate, mTodaysSteps)

            // Start counting for the new day
            mTodaysSteps = 0
            mCurrentDate = Util.calendar.timeInMillis
            sharedPreferences.edit().putLong(KEY_DATE, mCurrentDate).apply()
        }
        sharedPreferences.edit().putInt(KEY_STEPS, mTodaysSteps).apply()
        for (i in 0 until motionActivities.size()) {
            motionActivities.valueAt(i).update(mCurrentSteps)
        }
        sendUpdate()
    }

    private fun sendUpdate() {
        mBuilder.setContentText(String.format(Locale.getDefault(), getString(R.string.steps_format), Util.stepsToMeters(mTodaysSteps), mTodaysSteps))
        mNotificationManager.notify(FOREGROUND_ID, mBuilder.build())
        receiver?.let {
            val bundle = Bundle()
            bundle.putInt(KEY_STEPS, mTodaysSteps)
            val activities = ArrayList<Bundle>()
            for (i in 0 until motionActivities.size()) {
                val motionActivity = motionActivities.valueAt(i)
                val activityBundle = Bundle()
                activityBundle.putInt(KEY_ID, motionActivity.id)
                activityBundle.putInt(KEY_STEPS, motionActivity.steps)
                activityBundle.putBoolean(KEY_ACTIVE, motionActivity.active)
                activities.add(activityBundle)
            }
            bundle.putParcelableArrayList(KEY_ACTIVITIES, activities)
            it.send(0, bundle)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogHelper.d(TAG, "Received start id $startId: $intent")

        if (intent != null) {
            when {
                ACTION_SUBSCRIBE == intent.action -> receiver = intent.getParcelableExtra(MainActivity.RECEIVER_TAG)
                ACTION_START_ACTIVITY == intent.action -> {
                    val id = motionActivityId++
                    motionActivities.put(id, MotionActivity(id, mCurrentSteps))
                }
                ACTION_STOP_ACTIVITY == intent.action -> motionActivities.remove(intent.getIntExtra(KEY_ID, 0))
                ACTION_TOGGLE_ACTIVITY == intent.action -> motionActivities.get(intent.getIntExtra(KEY_ID, 0)).toggle()
            }
            sendUpdate()
        }

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return Service.START_STICKY
    }

    private fun startService() {
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: throw RuntimeException()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        // Notification channels are only supported on Android O+.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.app_name))
                .setContentIntent(pendingIntent)
        startForeground(FOREGROUND_ID, mBuilder.build())
    }

    /**
     * Creates Notification Channel. This is required in Android O+ to display notifications.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_NONE)

            notificationChannel.description = getString(R.string.steps)

            mNotificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {

        internal const val ACTION_SUBSCRIBE = "ACTION_SUBSCRIBE"
        internal const val ACTION_START_ACTIVITY = "ACTION_START_ACTIVITY"
        internal const val ACTION_STOP_ACTIVITY = "ACTION_STOP_ACTIVITY"
        internal const val ACTION_TOGGLE_ACTIVITY = "ACTION_TOGGLE_ACTIVITY"
        internal const val KEY_ID = "ID"
        internal const val KEY_STEPS = "STEPS"
        internal const val KEY_ACTIVE = "ACTIVE"
        internal const val KEY_ACTIVITIES = "ACTIVITIES"
        internal const val KEY_DATE = "DATE"
        private val TAG = LogHelper.makeLogTag(MotionService::class.java)
        private const val FOREGROUND_ID = 3843
        private const val CHANNEL_ID = "com.tiefensuche.motionmate.CHANNEL_ID"
    }
}
