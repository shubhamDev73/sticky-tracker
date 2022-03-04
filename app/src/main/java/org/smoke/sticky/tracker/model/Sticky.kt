package org.smoke.sticky.tracker.model

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "sticky")
data class Sticky(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Float,
    val timeMillis: Long,
)

@BindingAdapter("android:time")
fun displayTimeAsDate(view: TextView, timeMillis: Long) {
    view.text = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.getDefault()).format(timeMillis)
}
