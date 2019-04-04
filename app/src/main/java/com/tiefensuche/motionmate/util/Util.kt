/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.motionmate.util

import java.util.Calendar

object Util {

    /**
     * Get calendar with zeroed time (hours, minutes, seconds)
     *
     * @return Calendar with start of the day
     */
    val calendar: Calendar
        get() {
            val calendar = Calendar.getInstance()
            calendar.clear(Calendar.HOUR)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            return calendar
        }


    /**
     * Formula to convert steps to kilometers
     *
     * @param steps steps
     * @return kilometers
     */
    fun stepsToMeters(steps: Int): Double {
        return steps * 0.762 / 1000
    }

}
