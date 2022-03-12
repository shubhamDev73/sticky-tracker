package org.smoke.sticky.tracker.ui.dialogs

import androidx.lifecycle.ViewModel
import org.smoke.sticky.tracker.model.Tag
import java.util.*

class StickyDialogViewModel: ViewModel() {

    lateinit var tag: Tag
    lateinit var calendar: Calendar

}