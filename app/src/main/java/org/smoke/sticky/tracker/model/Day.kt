package org.smoke.sticky.tracker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Day(
    val startTime: Long,
    val label: String,
    val today: Boolean,
    val valid: Boolean = true,
) : Parcelable
