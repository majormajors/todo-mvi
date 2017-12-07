package com.mattmayers.todo.db

import android.arch.persistence.room.TypeConverter
import java.util.*


object DateTypeConverter {
    @TypeConverter @JvmStatic
    fun toDate(value: Long?): Date? = value?.let { Date(value) }
    @TypeConverter @JvmStatic
    fun toLong(value: Date?): Long? = value?.let { value.time }
}