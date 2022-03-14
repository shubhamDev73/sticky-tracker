package org.smoke.sticky.tracker.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import org.smoke.sticky.tracker.R

enum class Tag(
    @StringRes val label: Int,
    @ColorRes val color: Int,
    @DrawableRes val icon: Int,
) {
    CIGARETTE(R.string.cigarette, R.color.orange, R.drawable.ic_cigarette),
    JOINT(R.string.joint, R.color.purple_200, R.drawable.ic_joint)
    ;

    companion object {
        operator fun get(ordinal: Int): Tag {
            return values()[ordinal]
        }
    }

}