package org.smoke.sticky.tracker.ui.timeline

interface ScrollEndListener {
    fun onScrollNext(): Boolean
    fun onScrollPrevious(): Boolean
}