package tachiyomi.data.author

import tachiyomi.domain.author.model.Author

object AuthorMapper {
    fun mapperAuthor(
        _id: Long,
        name: String,
        nameCn: String?,
        aliases: List<String>?,
        gender: String?,
        careers: List<String>?,
        thumbnailUrl: String?,
        description: String?,
        sourceAuthorId: Long?,
        source: String?,
        dateAdded: Long?,
        lastUpdate: Long?,
    ): Author = Author(
        id = _id,
        name = name,
        nameCn = nameCn,
        aliases = aliases,
        gender = gender,
        careers = careers,
        thumbnailUrl = thumbnailUrl,
        description = description,
        sourceAuthorId = sourceAuthorId,
        source = source,
        dateAdded = dateAdded,
        lastUpdate = lastUpdate,
    )
}
