package eu.kanade.presentation.browse

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import eu.kanade.presentation.components.EmptyScreen
import eu.kanade.presentation.components.LazyColumn
import eu.kanade.presentation.components.LoadingScreen
import eu.kanade.presentation.components.PreferenceRow
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.browse.extension.ExtensionFilterPresenter
import eu.kanade.tachiyomi.util.system.LocaleHelper
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ExtensionFilterScreen(
    nestedScrollInterop: NestedScrollConnection,
    presenter: ExtensionFilterPresenter,
) {
    val context = LocalContext.current
    when {
        presenter.isLoading -> LoadingScreen()
        presenter.isEmpty -> EmptyScreen(textResource = R.string.empty_screen)
        else -> {
            SourceFilterContent(
                nestedScrollInterop = nestedScrollInterop,
                state = presenter,
                onClickLang = {
                    presenter.toggleLanguage(it)
                },
            )
        }
    }
    LaunchedEffect(Unit) {
        presenter.events.collectLatest {
            when (it) {
                ExtensionFilterPresenter.Event.FailedFetchingLanguages -> {
                    context.toast(R.string.internal_error)
                }
            }
        }
    }
}

@Composable
fun SourceFilterContent(
    nestedScrollInterop: NestedScrollConnection,
    state: ExtensionFilterState,
    onClickLang: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.nestedScroll(nestedScrollInterop),
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
    ) {
        items(
            items = state.items,
        ) { model ->
            ExtensionFilterItem(
                modifier = Modifier.animateItemPlacement(),
                lang = model.lang,
                enabled = model.enabled,
                onClickItem = onClickLang,
            )
        }
    }
}

@Composable
fun ExtensionFilterItem(
    modifier: Modifier,
    lang: String,
    enabled: Boolean,
    onClickItem: (String) -> Unit,
) {
    PreferenceRow(
        modifier = modifier,
        title = LocaleHelper.getSourceDisplayName(lang, LocalContext.current),
        action = {
            Switch(checked = enabled, onCheckedChange = null)
        },
        onClick = { onClickItem(lang) },
    )
}