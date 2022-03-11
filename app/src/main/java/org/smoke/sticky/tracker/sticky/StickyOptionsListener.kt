package org.smoke.sticky.tracker.sticky

import org.smoke.sticky.tracker.model.Sticky

interface StickyOptionsListener {

    fun onDelete(sticky: Sticky)
    fun onEdit(sticky: Sticky)

}