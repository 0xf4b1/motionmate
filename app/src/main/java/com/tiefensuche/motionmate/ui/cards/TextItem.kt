/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.motionmate.ui.cards

import android.view.View

import com.tiefensuche.motionmate.ui.TextItemAdapter

/**
 * Basic TextItem that is contained in the [TextItemAdapter].
 * It holds description and content texts and can display a button.
 */
internal open class TextItem(val description: String, private var content: String?) {
    private var icon: Int = 0
    internal var buttonClickListener: View.OnClickListener? = null
    private var updateListener: UpdateListener? = null

    internal open val isSwipeable: Boolean
        get() = false

    internal fun getContent(): String? {
        return content
    }

    internal open fun setContent(content: String) {
        this.content = content
        updateListener?.update(content)
    }

    internal fun setIcon(icon: Int) {
        this.icon = icon
        updateListener?.updateIcon(icon)
    }

    internal fun setUpdateListener(updateListener: UpdateListener) {
        this.updateListener = updateListener
        updateListener.update(content)
        updateListener.updateIcon(icon)
    }

    interface UpdateListener {
        fun update(text: String?)

        fun updateIcon(id: Int)
    }
}
