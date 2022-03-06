package org.smoke.sticky.tracker.model

import org.smoke.sticky.tracker.R

enum class Tag(val label: Int, val color: Int, val icon: Int) {
    CIGARETTE(R.string.cigarette, R.color.orange, R.drawable.ic_cigarette),
    JOINT(R.string.joint, R.color.purple_200, R.drawable.ic_joint)
    ;

    companion object {
        operator fun get(ordinal: Int): Tag {
            return values()[ordinal]
        }
    }

}