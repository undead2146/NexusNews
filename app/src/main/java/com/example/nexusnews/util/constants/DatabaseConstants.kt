package com.example.nexusnews.util.constants

/**
 * Database-related constants for Room database configuration.
 */
object DatabaseConstants {
    const val DATABASE_NAME = "nexus_news.db"
    const val DATABASE_VERSION = 2

    // Table names
    const val ARTICLES_TABLE = "articles"
    const val BOOKMARKS_TABLE = "bookmarks"
    const val ARTICLE_SUMMARIES_TABLE = "article_summaries"
    const val AI_USAGE_TABLE = "ai_usage"

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
    const val COLUMN_ARTICLE_ID = "article_id"
    const val COLUMN_SUMMARY = "summary"
    const val COLUMN_MODEL_USED = "model_used"
    const val COLUMN_PROMPT_TOKENS = "prompt_tokens"
    const val COLUMN_COMPLETION_TOKENS = "completion_tokens"
    const val COLUMN_TOTAL_TOKENS = "total_tokens"
    const val COLUMN_GENERATED_AT = "generated_at"
    const val COLUMN_LANGUAGE = "language"
    const val COLUMN_REQUEST_TYPE = "request_type"
    const val COLUMN_REQUEST_COUNT = "request_count"
    const val COLUMN_LATENCY_MS = "latency_ms"
}
