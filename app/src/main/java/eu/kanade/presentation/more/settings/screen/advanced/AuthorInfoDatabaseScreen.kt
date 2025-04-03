package eu.kanade.presentation.more.settings.screen.advanced

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.util.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import tachiyomi.core.common.util.lang.launchIO
import tachiyomi.data.Database
import tachiyomi.domain.author.model.Author
import tachiyomi.domain.author.repository.AuthorRepository
import tachiyomi.presentation.core.components.LazyColumnWithAction
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.screens.EmptyScreen
import tachiyomi.presentation.core.screens.LoadingScreen
import tachiyomi.presentation.core.util.selectedBackground
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class AuthorInfoDatabaseScreen : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val model = rememberScreenModel { AuthorInfoDatabaseScreenModel() }
        val state by model.state.collectAsState()
        val scope = rememberCoroutineScope()

        when (val s = state) {
            is AuthorInfoDatabaseScreenModel.State.Loading -> LoadingScreen()
            is AuthorInfoDatabaseScreenModel.State.Ready -> {

                Scaffold(
                    topBar = { scrollBehavior ->
                        AppBar(
                            title = "作者列表",
                            navigateUp = navigator::pop,
                            scrollBehavior = scrollBehavior,
                        )
                    },
                ) { contentPadding ->
                    {

                    }
                    if (s.items.isEmpty()) {
                        EmptyScreen(
                            message = "暫無作者信息",
                            modifier = Modifier.padding(contentPadding),
                        )
                    } else {
                        LazyColumnWithAction(
                            contentPadding = contentPadding,
                            actionLabel = "添加",
                            actionEnabled = true,
                            onClickAction = {},
                        ) {
                            items(s.items) { author ->
                                AuthorInfoItem(
                                    author = author,
                                    isSelected = s.selection.contains(author.id),
                                    onClickSelect = {model.toggleSelection(author)},
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AuthorInfoItem(
        author: Author,
        onClickSelect: () -> Unit,
        isSelected: Boolean,
    ) {
        Row(
            modifier = Modifier
                .selectedBackground(isSelected)
                .clickable(onClick = onClickSelect)
                .padding(horizontal = 8.dp)
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
            ){
                Text(
                    text = author.id.toString(),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
            ) {
                Text(
                    text = author.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (!author.aliases.isNullOrEmpty()){
                    Text(
                        text = "别名: ${author.aliases?.joinToString(",")}",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private class AuthorInfoDatabaseScreenModel : StateScreenModel<AuthorInfoDatabaseScreenModel.State>(State.Loading) {
    private val authorRepository: AuthorRepository = Injekt.get()
    private val database: Database = Injekt.get()

    init {
        screenModelScope.launchIO {
            authorRepository.getAllAuthors()
                .collectLatest { list ->
                    mutableState.update { old ->
                        val items = list.sortedBy { it.name }
                        when (old) {
                            State.Loading -> State.Ready(items)
                            is State.Ready -> old.copy(items = items)
                        }
                    }
                }
        }
    }

    fun toggleSelection(author: Author) = mutableState.update { state ->
        if (state !is State.Ready) return@update state
        val mutableList = state.selection.toMutableList()
        if (mutableList.contains(author.id)) {
            mutableList.remove(author.id)
        } else {
            mutableList.add(author.id)
        }
        state.copy(selection = mutableList)
    }

    sealed interface State {
        @Immutable
        data object Loading : State

        @Immutable
        data class Ready(
            val items: List<Author>,
            val selection: List<Long> = emptyList(),
        ) : State
    }
}
