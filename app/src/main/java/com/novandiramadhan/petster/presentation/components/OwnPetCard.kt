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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun OwnPetCard(
    pet: Pet,
    onClick: (Destinations) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(20.dp)),
        onClick = {
            if (pet.id != null) onClick(Destinations.PetDetail(pet.id))
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
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
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        color = LimeGreen,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(12.dp)
                    .align(Alignment.BottomStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = pet.name ?: "",
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 8.sp
                    )
                    Text(
                        text = "${pet.age} ${pet.ageUnit}, ${pet.gender}",
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 8.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun OwnPetCardPreview() {
    PetsterTheme {
        OwnPetCard(
            pet = PetDummy(context = LocalContext.current).pets[0]
        )
    }
}