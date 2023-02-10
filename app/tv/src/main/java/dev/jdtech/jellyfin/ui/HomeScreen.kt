package dev.jdtech.jellyfin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.jdtech.jellyfin.R
import dev.jdtech.jellyfin.api.JellyfinApi
import dev.jdtech.jellyfin.models.HomeItem
import dev.jdtech.jellyfin.viewmodels.HomeViewModel
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.ImageType

@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        homeViewModel.loadData(includeLibraries = true)
    }

    val api = JellyfinApi.getInstance(context)

    val delegatedUiState by homeViewModel.uiState.collectAsState()
    when (val uiState = delegatedUiState) {
        is HomeViewModel.UiState.Loading -> {
            Text(text = "LOADING")
        }
        is HomeViewModel.UiState.Normal -> {
            TvLazyColumn(
                contentPadding = PaddingValues(bottom = 32.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Header()
                }
                items(uiState.homeItems, key = { it.id }) { homeItem ->
                    when (homeItem) {
                        is HomeItem.Libraries -> {
                            Text(
                                text = homeItem.section.name,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(start = 32.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            TvLazyRow(
                                horizontalArrangement = Arrangement.spacedBy(24.dp),
                                contentPadding = PaddingValues(horizontal = 32.dp)
                            ) {
                                items(homeItem.section.items) { library ->
                                    Column(
                                        modifier = Modifier
                                            .width(240.dp)
                                            .clickable { }
                                    ) {
                                        ItemPoster(
                                            item = library,
                                            api = api,
                                            direction = Direction.HORIZONTAL
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = library.name.orEmpty(),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        is HomeItem.Section -> {
                            Text(
                                text = homeItem.homeSection.name,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(start = 32.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            TvLazyRow(
                                horizontalArrangement = Arrangement.spacedBy(24.dp),
                                contentPadding = PaddingValues(horizontal = 32.dp)
                            ) {
                                items(homeItem.homeSection.items) { item ->
                                    Column(
                                        modifier = Modifier
                                            .width(180.dp)
                                            .clickable { }
                                    ) {
                                        Box {
                                            ItemPoster(
                                                item = item,
                                                api = api,
                                                direction = Direction.HORIZONTAL
                                            )
                                            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                                                Row {
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .height(4.dp)
                                                            .width(item.userData?.playedPercentage?.times(1.64)?.dp ?: 0.dp)
                                                            .clip(MaterialTheme.shapes.extraSmall)
                                                            .background(MaterialTheme.colorScheme.primary)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }

                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = item.name.orEmpty(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = item.seriesName.orEmpty(),
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        is HomeItem.ViewItem -> {
                            Text(
                                text = homeItem.view.name.orEmpty(),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(start = 32.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            TvLazyRow(
                                horizontalArrangement = Arrangement.spacedBy(24.dp),
                                contentPadding = PaddingValues(horizontal = 32.dp)
                            ) {
                                items(homeItem.view.items.orEmpty().count()) { i ->
                                    val item = homeItem.view.items.orEmpty()[i]
                                    Column(
                                        modifier = Modifier
                                            .width(120.dp)
                                            .clickable { }
                                    ) {
                                        ItemPoster(
                                            item = item,
                                            api = api,
                                            direction = Direction.VERTICAL
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = item.name.orEmpty(),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

        }
        is HomeViewModel.UiState.Error -> {
            Text(text = uiState.error.toString())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Header() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 32.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_banner),
            contentDescription = null,
            modifier = Modifier.height(40.dp)
        )
    }
}

fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

fun <K, V> Map<out K, V>?.isNotNullOrEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

enum class Direction {
    HORIZONTAL, VERTICAL
}

@Composable
fun ItemPoster(item: BaseItemDto, api: JellyfinApi, direction: Direction) {
    var itemId = item.id
    var imageType = ImageType.PRIMARY

    if (direction == Direction.HORIZONTAL) {
        if (item.imageTags.isNotNullOrEmpty()) { // TODO: Downloadmetadata currently does not store imagetags, so it always uses the backdrop
            when (item.type) {
                BaseItemKind.MOVIE -> {
                    if (item.backdropImageTags.isNotNullOrEmpty()) {
                        imageType = ImageType.BACKDROP
                    }
                }
                else -> {
                    if (!item.imageTags!!.keys.contains(ImageType.PRIMARY)) {
                        imageType = ImageType.BACKDROP
                    }
                }
            }
        } else {
            if (item.type == BaseItemKind.EPISODE) {
                itemId = item.seriesId!!
                imageType = ImageType.BACKDROP
            }
        }
    } else {
        itemId =
            if (item.type == BaseItemKind.EPISODE || item.type == BaseItemKind.SEASON && item.imageTags.isNullOrEmpty()) item.seriesId
                ?: item.id else item.id
    }


    AsyncImage(
        model = "${api.api.baseUrl}/items/$itemId/Images/$imageType",
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(if (direction == Direction.HORIZONTAL) 1.77f else 0.66f)
            .clip(MaterialTheme.shapes.large)
            .background(
                MaterialTheme.colorScheme.surface
            )
    )
}