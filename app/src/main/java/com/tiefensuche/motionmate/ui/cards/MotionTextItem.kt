/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.motionmate.ui.cards

import android.content.Context
import com.tiefensuche.motionmate.R
import com.tiefensuche.motionmate.util.Util
import java.util.*

/**
 * A specialized [TextItem] that is used to display step information
 */
internal open class MotionTextItem(context: Context, description: Int) : TextItem(context, description) {

    private val format: String = context.getString(R.string.steps_format)

    internal open fun updateSteps(steps: Int) {
        setContent(steps)
    }

    internal fun setContent(steps: Number) {
        setContent(String.format(Locale.getDefault(), format, Util.stepsToMeters(steps), steps.toInt()))
    }
}
