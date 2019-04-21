/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.motionmate.ui.cards

import com.tiefensuche.motionmate.util.Util
import java.util.*

/**
 * A specialized [TextItem] that is used to display step information
 */
internal open class MotionTextItem(description: String, content: String) : TextItem(description, content) {

    internal open fun updateSteps(steps: Int) {
        setContent(steps)
    }

    internal fun setContent(steps: Int) {
        setContent(String.format(Locale.getDefault(), "%.2f km or %d steps", Util.stepsToMeters(steps), steps))
    }
}
