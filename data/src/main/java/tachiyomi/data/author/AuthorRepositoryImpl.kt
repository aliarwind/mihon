package tachiyomi.data.author

import kotlinx.coroutines.flow.Flow
import tachiyomi.data.DatabaseHandler
import tachiyomi.domain.author.model.Author
import tachiyomi.domain.author.repository.AuthorRepository

class AuthorRepositoryImpl (
    private val handler: DatabaseHandler,
): AuthorRepository {
    override suspend fun fuzzySearchByName(name: String): Flow<List<Author>> {
        return handler.subscribeToList{authorsQueries.fuzzySearchByName(name, AuthorMapper::mapperAuthor)}
    }

    override suspend fun insert(author: Author): Long? {
        return handler.awaitOneOrNullExecutable(inTransaction = true) {
            authorsQueries.insert(
                name = author.name,
                nameCn = author.nameCn,
                aliases = author.aliases,
                gender = author.gender,
                careers = author.careers,
                thumbnailUrl = author.thumbnailUrl,
                description = author.description,
                sourceAuthorId = author.sourceAuthorId,
                source = author.source,
                dateAdd = author.dateAdded,
                lastUpdate = author.lastUpdate
            )
            authorsQueries.selectLastInsertedRowId()
        }
    }

    override suspend fun getAllAuthors(): Flow<List<Author>> {
        return handler.subscribeToList { authorsQueries.getAllAuthors(AuthorMapper::mapperAuthor) }
    }

    suspend fun count():Flow<Long> {
        return handler.subscribeToOne { authorsQueries.count() }
    }
}
