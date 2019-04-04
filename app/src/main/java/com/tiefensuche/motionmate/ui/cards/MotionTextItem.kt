/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.motionmate.ui.cards

import com.tiefensuche.motionmate.util.Util

import java.util.Locale

/**
 * A specialized [TextItem] that is used to display step information
 */
open class MotionTextItem(description: String, content: String) : TextItem(description, content) {

    open fun updateSteps(steps: Int) {
        setContent(steps)
    }

    fun setContent(steps: Int) {
        setContent(String.format(Locale.getDefault(), "%.2f km or %d steps", Util.stepsToMeters(steps), steps))
    }
}
