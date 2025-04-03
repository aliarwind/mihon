package tachiyomi.domain.author.repository

import kotlinx.coroutines.flow.Flow
import tachiyomi.domain.author.model.Author

interface AuthorRepository {
    suspend fun fuzzySearchByName(name:String): Flow<List<Author>>
    suspend fun insert(author: Author): Long?
    suspend fun getAllAuthors():Flow<List<Author>>
}
