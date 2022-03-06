package org.smoke.sticky.tracker.utils

import android.content.SharedPreferences
import org.smoke.sticky.tracker.model.Tag

class PreferenceUtils {

    companion object {
        private lateinit var preferences: SharedPreferences

        val KEY_TAGS = "tags"

        fun init(preferences: SharedPreferences) {
            this.preferences = preferences
        }

        fun getTags(): List<Tag> {
            val names = preferences.getStringSet(KEY_TAGS, setOf())
            val tags = names?.map { Tag.valueOf(it) }
            return tags ?: listOf()
        }

        fun setTags(tags: List<Tag>) {
            preferences.edit().putStringSet(KEY_TAGS, tags.map { it.name }.toSet()).apply()
        }
    }
}