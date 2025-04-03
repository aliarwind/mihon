package tachiyomi.domain.author.model

import androidx.compose.runtime.Immutable
import java.io.Serializable

@Immutable
class Author (
    val id: Long,
    val name: String,
    val nameCn: String?,
    val aliases: List<String>?,
    val gender: String?,
    val careers: List<String>?,
    val thumbnailUrl: String?,
    val description: String?,
    val sourceAuthorId: Long?,
    val source: String?,
    val dateAdded: Long?,
    val lastUpdate: Long?
): Serializable {

}
