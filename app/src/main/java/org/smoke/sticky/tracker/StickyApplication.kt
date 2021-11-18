package org.smoke.sticky.tracker

import android.app.Application

class StickyApplication : Application(){
    val database: StickyDatabase by lazy { StickyDatabase.getDatabase(this) }
}
