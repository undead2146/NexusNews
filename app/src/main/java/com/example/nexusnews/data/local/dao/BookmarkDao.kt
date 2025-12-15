package com.example.nexusnews.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.nexusnews.data.local.entity.ArticleEntity
import com.example.nexusnews.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for bookmark operations.
 * Handles bookmarking, favorites, and related queries.
 */
@Dao
interface BookmarkDao {
    /**
     * Gets all bookmarked articles with their full data.
     * Uses JOIN to fetch article details.
     */
    @Transaction
    @Query(
        """
        SELECT a.* FROM articles a
        INNER JOIN bookmarks b ON a.id = b.article_id
        ORDER BY b.bookmarked_at DESC
    """,
    )
    fun getAllBookmarks(): Flow<List<ArticleEntity>>

    /**
     * Gets only favorite articles.
     */
    @Transaction
    @Query(
        """
        SELECT a.* FROM articles a
        INNER JOIN bookmarks b ON a.id = b.article_id
        WHERE b.is_favorite = 1
        ORDER BY b.bookmarked_at DESC
    """,
    )
    fun getFavorites(): Flow<List<ArticleEntity>>

    /**
     * Checks if an article is bookmarked.
     * Returns Flow for reactive UI updates.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE article_id = :articleId)")
    fun isBookmarked(articleId: String): Flow<Boolean>

    /**
     * Gets bookmark entity for an article.
     */
    @Query("SELECT * FROM bookmarks WHERE article_id = :articleId")
    suspend fun getBookmark(articleId: String): BookmarkEntity?

    /**
     * Adds a bookmark with the article.
     * Transaction ensures both article and bookmark are inserted.
     */
    @Transaction
    suspend fun addBookmark(article: ArticleEntity) {
        insertArticle(article)
        insertBookmark(BookmarkEntity(articleId = article.id))
    }

    /**
     * Inserts or replaces an article.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    /**
     * Inserts a bookmark (ignores if already exists).
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    /**
     * Removes a bookmark by article ID.
     */
    @Query("DELETE FROM bookmarks WHERE article_id = :articleId")
    suspend fun deleteBookmark(articleId: String)

    /**
     * Updates the favorite status of a bookmark.
     */
    @Query("UPDATE bookmarks SET is_favorite = :isFavorite WHERE article_id = :articleId")
    suspend fun updateFavoriteStatus(
        articleId: String,
        isFavorite: Boolean,
    )

    /**
     * Clears all bookmarks.
     */
    @Query("DELETE FROM bookmarks")
    suspend fun clearAllBookmarks()
}
