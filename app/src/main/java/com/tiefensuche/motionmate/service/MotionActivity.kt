/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.motionmate.service

/**
 * This class represents a motion activity started by the user. It counts all the steps from the time
 * the activity was started.
 */
class MotionActivity(val id: Int, private var previousSteps: Int) {
    var steps: Int = 0
        private set
    var active: Boolean = false
        private set

    init {
        this.active = true
    }

    fun update(currentSteps: Int) {
        if (active) {
            steps += currentSteps - previousSteps
        }
        previousSteps = currentSteps
    }

    fun toggle() {
        active = !active
    }
}
