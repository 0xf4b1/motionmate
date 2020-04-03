/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.motionmate.ui.cards

import android.content.Context

/**
 * Further specialized [TextItem] that is used to show statistics. It is initialized with
 * data from database and updated with data from step sensor.
 */
internal class MotionStatisticsTextItem(context: Context, description: Int, private val initialSteps: Int) : MotionTextItem(context, description) {

    override fun updateSteps(steps: Int) {
        setContent(steps + this.initialSteps)
    }
}