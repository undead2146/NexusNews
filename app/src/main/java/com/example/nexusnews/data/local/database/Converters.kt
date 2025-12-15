package com.example.nexusnews.data.local.database

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Type converters for Room database.
 * Handles conversion of complex types that Room doesn't support natively.
 */
class Converters {
    /**
     * Converts a timestamp (Long) to LocalDateTime.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? =
        value?.let {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(it),
                ZoneId.systemDefault(),
            )
        }

    /**
     * Converts LocalDateTime to timestamp (Long).
     */
    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? =
        date
            ?.atZone(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli()

    /**
     * Converts a comma-separated string to List<String>.
     */
    @TypeConverter
    fun fromStringList(value: String?): List<String> = value?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

    /**
     * Converts List<String> to comma-separated string.
     */
    @TypeConverter
    fun toStringList(list: List<String>?): String? = list?.joinToString(",")
}
