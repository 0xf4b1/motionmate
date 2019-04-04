/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.motionmate.service

/**
 * This class represents a motion activity started by the user. It counts all the steps from the time
 * the activity was started.
 */
internal class MotionActivity(val id: Int, private var previousSteps: Int) {
    internal var steps: Int = 0
        private set
    internal var active: Boolean = false
        private set

    init {
        this.active = true
    }

    internal fun update(currentSteps: Int) {
        if (active) {
            steps += currentSteps - previousSteps
        }
        previousSteps = currentSteps
    }

    internal fun toggle() {
        active = !active
    }
}
