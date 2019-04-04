/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.motionmate.ui.cards

/**
 * Further specialized [TextItem] that is used to show statistics. It is initialized with
 * data from database and updated with data from step sensor.
 */
internal class MotionStatisticsTextItem(description: String, private val initialSteps: Int) : MotionTextItem(description, "") {

    override fun updateSteps(steps: Int) {
        setContent(steps + this.initialSteps)
    }
}
