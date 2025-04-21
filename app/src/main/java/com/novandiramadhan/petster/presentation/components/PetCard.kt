package com.novandiramadhan.petster.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.placeholder
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.dummy.PetDummy
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.presentation.navigation.Destinations
import com.novandiramadhan.petster.presentation.ui.theme.Black
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.ui.theme.Pink

@Composable
fun PetCard(
    pet: Pet,
    onClick: (Destinations) -> Unit = {},
    onFavoriteClick: (isFavorite: Boolean) -> Unit = {},
    isFavoriteShow: Boolean = false
) {
    Card(
        modifier = Modifier
            .width(250.dp)
            .height(300.dp)
            .clip(RoundedCornerShape(20.dp)),
        onClick = {
            if (pet.id != null) onClick(Destinations.PetDetail(pet.id))
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        if (pet.isAdopted) {
                            scaleX = 1.05f
                            scaleY = 1.05f
                            alpha = 0.9f
                        }
                    },
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pet.image?.imageCoverUrl)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_error)
                    .listener(
                        onStart = {

                        },
                        onError = { request, result ->
                            Log.e("PetCard Coil", "Error loading ${request.data}: ${result.throwable}")
                        },
                        onSuccess = { request, result ->
                            Log.d("PetCard Coil", "Success loading ${request.data} from ${result.dataSource}")
                        }
                    )
                    .build(),
                contentDescription = pet.name,
                contentScale = ContentScale.Crop,
            )

            if (pet.isAdopted) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                )

                Icon(
                    modifier = Modifier.align(Alignment.Center)
                        .size(64.dp),
                    imageVector = Icons.Rounded.Pets,
                    contentDescription = stringResource(R.string.adopted),
                    tint = LimeGreen
                )
            }

            if (isFavoriteShow) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(48.dp),
                    onClick = {
                        onFavoriteClick(!pet.isFavorite)
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Pink,
                        contentColor = Black
                    )
                ) {
                    Icon(
                        imageVector = if (pet.isFavorite)
                            Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        color = LimeGreen,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp)
                    .align(Alignment.BottomStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = pet.name ?: "",
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 8.sp,
                        color = Black,
                    )
                    Text(
                        text = "${pet.age} ${pet.ageUnit}, ${pet.gender}",
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 8.sp,
                        color = Black
                    )
                }

                if (pet.viewCount != null && pet.viewCount > 0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Visibility,
                            contentDescription = null,
                            tint = Black
                        )
                        Text(
                            text = pet.viewCount.toString(),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Black,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PetCardPreview() {
    PetsterTheme {
        PetCard(
            pet = PetDummy(context = LocalContext.current).pets[0]
        )
    }
}