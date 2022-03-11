package org.smoke.sticky.tracker.model

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.smoke.sticky.tracker.utils.TimeUtils

@Entity(tableName = "sticky")
data class Sticky(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var amount: Float,
    val timeMillis: Long,
    var tag: Tag,
)

@BindingAdapter("android:time")
fun displayTimeAsDate(view: TextView, timeMillis: Long) {
    view.text = TimeUtils.getTimeString(timeMillis)
}
