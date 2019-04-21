/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.motionmate.ui.cards

import android.view.View

/**
 * Further specialized [TextItem] that shows step information as activity that can be
 * started and stopped.
 */
internal class MotionActivityTextItem(description: String, content: String, val id: Int, listener: View.OnClickListener) : MotionTextItem(description, content) {
    private var active = true

    override val isSwipeable: Boolean
        get() = !active

    init {
        buttonClickListener = View.OnClickListener { view ->
            setActive(!active)
            listener.onClick(view)
        }
    }

    internal fun setActive(active: Boolean) {
        this.active = active
        setIcon(if (active) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play)
    }
}
