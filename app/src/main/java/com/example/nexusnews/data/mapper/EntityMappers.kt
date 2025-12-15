package com.example.nexusnews.data.mapper

import com.example.nexusnews.data.local.entity.ArticleEntity
import com.example.nexusnews.domain.model.Article

/**
 * Extension function to convert ArticleEntity to domain Article.
 */
fun ArticleEntity.toDomain(): Article =
    Article(
        id = id,
        title = title,
        description = description,
        content = content,
        url = url,
        imageUrl = imageUrl,
        author = author,
        source = source,
        publishedAt = publishedAt,
        category = category,
        tags = tags,
    )

/**
 * Extension function to convert domain Article to ArticleEntity.
 */
fun Article.toEntity(): ArticleEntity =
    ArticleEntity(
        id = id,
        title = title,
        description = description,
        content = content,
        url = url,
        imageUrl = imageUrl,
        author = author,
        source = source,
        publishedAt = publishedAt,
        category = category,
        tags = tags,
    )

/**
 * Converts a list of ArticleEntity to domain Articles.
 */
fun List<ArticleEntity>.toDomainList(): List<Article> = map { it.toDomain() }

/**
 * Converts a list of domain Articles to ArticleEntities.
 */
fun List<Article>.toEntityList(): List<ArticleEntity> = map { it.toEntity() }
