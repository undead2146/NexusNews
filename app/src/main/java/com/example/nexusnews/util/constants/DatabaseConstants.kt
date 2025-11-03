package com.example.nexusnews.util.constants

/**
 * Database-related constants for Room database configuration.
 */
object DatabaseConstants {
    const val DATABASE_NAME = "nexus_news.db"
    const val DATABASE_VERSION = 1

    // Table names
    const val ARTICLES_TABLE = "articles"
    const val BOOKMARKS_TABLE = "bookmarks"

    // Column names
    const val COLUMN_ID = "id"
    const val COLUMN_TITLE = "title"
    const val COLUMN_DESCRIPTION = "description"
    const val COLUMN_CONTENT = "content"
    const val COLUMN_URL = "url"
    const val COLUMN_IMAGE_URL = "image_url"
    const val COLUMN_AUTHOR = "author"
    const val COLUMN_SOURCE = "source"
    const val COLUMN_PUBLISHED_AT = "published_at"
    const val COLUMN_CATEGORY = "category"
    const val COLUMN_BOOKMARKED_AT = "bookmarked_at"
}