package com.novandiramadhan.petster.presentation.components

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.mr0xf00.easycrop.AspectRatio
import com.mr0xf00.easycrop.CropError
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.CropperStyle
import com.mr0xf00.easycrop.ImageCropper
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.states.PetPhotoModalState
import com.novandiramadhan.petster.common.states.PetPhotoState
import com.novandiramadhan.petster.common.states.PetUriState
import com.novandiramadhan.petster.common.utils.FileUtil
import com.novandiramadhan.petster.domain.model.PetImage
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import kotlinx.coroutines.launch

@Composable
fun PetPhotosForm(
    petImage: PetImage? = null,
    photoUris: List<PetUriState>,
    modal: PetPhotoModalState,
    imageCropper: ImageCropper,
    onAddPhoto: (base64Data: String, uri: String) -> Unit,
    onUpdatePhoto: (index: Int, uri: String, base64Data: String) -> Unit,
    onUpdateUrlPhoto: (urlIndex: Int, uri: String, base64Data: String) -> Unit,
    onRemovePhoto: (index: Int) -> Unit,
    onRemoveUrlPhoto: (urlIndex: Int) -> Unit,
    onSetCoverPhoto: (index: Int) -> Unit,
    onSetUrlPhotoAsCover: (index: Int) -> Unit,
    onToggleModal: (isShowed: Boolean) -> Unit,
    onSetPhotoModal: (index: Int, isCover: Boolean) -> Unit,
    onSetModalState: (state: PetPhotoState) -> Unit,
    onResetPhotoModal: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imageCropperState = imageCropper.cropState
    val isUpdatingUrlPhoto = remember { mutableStateOf(false) }
    val currentPhotoIndex = remember { mutableIntStateOf(-1) }

    data class CombinedPhoto(
        val uri: String,
        val isCover: Boolean,
        val isUrl: Boolean,
        val index: Int
    )

    val combinedPhotos = mutableListOf<CombinedPhoto>().apply {
        petImage?.imageUrls?.forEachIndexed { index, url ->
            val isCover = url == petImage.imageCoverUrl
            add(CombinedPhoto(uri = url, isCover = isCover, isUrl = true, index = index))
        }

        photoUris.forEachIndexed { index, uriState ->
            add(CombinedPhoto(uri = uriState.uri, isCover = uriState.isCover, isUrl = false, index = index))
        }
    }
    Log.d("PetPhotosForm: combinedPhotos", combinedPhotos.toString())

    val totalPhotos = combinedPhotos.size
    val canAddMore = totalPhotos < 5

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            scope.launch {
                val croppedUri = imageCropper.crop(uri, context)
                when (croppedUri) {
                    CropResult.Cancelled -> {
                        onResetPhotoModal()
                    }
                    is CropError -> {
                        onResetPhotoModal()
                    }
                    is CropResult.Success -> {
                        val croppedImageBase64 = FileUtil.imageBitmapToBase64(croppedUri.bitmap)
                        when (modal.state) {
                            PetPhotoState.ADD -> {
                                onAddPhoto(croppedImageBase64, uri.toString())
                            }
                            PetPhotoState.UPDATE -> {
                                if (isUpdatingUrlPhoto.value) {
                                    onUpdateUrlPhoto(currentPhotoIndex.intValue, uri.toString(), croppedImageBase64)
                                } else {
                                    onUpdatePhoto(currentPhotoIndex.intValue, uri.toString(), croppedImageBase64)
                                }
                                onResetPhotoModal()
                            }
                        }
                    }
                }
            }
        }
    }

    when {
        modal.isShowed -> {
            val selectedPhoto = combinedPhotos.getOrNull(modal.photoUriIndex)
            if (selectedPhoto != null) {
                PetPhotoDialog(
                    onDismissRequest = { onResetPhotoModal() },
                    onDeletePhoto = {
                        if (selectedPhoto.isUrl) {
                            onRemoveUrlPhoto(selectedPhoto.index)
                        } else {
                            onRemovePhoto(selectedPhoto.index)
                        }
                        onResetPhotoModal()
                    },
                    onChangePhoto = {
                        isUpdatingUrlPhoto.value = selectedPhoto.isUrl
                        currentPhotoIndex.intValue = selectedPhoto.index

                        onSetModalState(PetPhotoState.UPDATE)
                        pickMedia.launch(PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        ))
                        onToggleModal(false)
                    },
                    onSetAsCover = {
                        if (selectedPhoto.isUrl) {
                            onSetUrlPhotoAsCover(selectedPhoto.index)
                        } else {
                            onSetCoverPhoto(selectedPhoto.index)
                        }
                        onResetPhotoModal()
                    },
                    isCover = selectedPhoto.isCover
                )
            }
        }

        imageCropperState != null -> {
            ImageCropperDialog(
                state = imageCropperState,
                style = CropperStyle(
                    backgroundColor = MaterialTheme.colorScheme.background,
                    rectColor = LimeGreen,
                    aspects = listOf(AspectRatio(4,5))
                ),
                dialogProperties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                ),
                topBar = {
                    ImageCropperTopBar(it)
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.photos) + " *",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val firstRowCount = minOf(totalPhotos, 3)
            for (i in 0 until firstRowCount) {
                val photo = combinedPhotos[i]
                PetPhotosFormItem(
                    photoUri = photo.uri,
                    isFilled = true,
                    onClick = {
                        onToggleModal(true)
                        onSetPhotoModal(i, photo.isCover)
                    },
                    modifier = Modifier.weight(1f),
                    isCover = photo.isCover
                )
            }

            if (canAddMore && firstRowCount < 3) {
                PetPhotosFormItem(
                    isFilled = false,
                    onClick = {
                        pickMedia.launch(PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
            }

            val totalFirstRowItems = minOf(
                firstRowCount + (if (canAddMore) 1 else 0),
                3
            )
            if (totalFirstRowItems < 3) {
                repeat(3 - totalFirstRowItems) {
                    Box(modifier = Modifier.weight(1f)) {}
                }
            }
        }

        if (totalPhotos > 3 || (totalPhotos == 3 && canAddMore)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 3 until totalPhotos) {
                    val photo = combinedPhotos[i]
                    PetPhotosFormItem(
                        photoUri = photo.uri,
                        isFilled = true,
                        onClick = {
                            onToggleModal(true)
                            onSetPhotoModal(i, photo.isCover)
                        },
                        modifier = Modifier.weight(1f),
                        isCover = photo.isCover
                    )
                }

                if (canAddMore) {
                    PetPhotosFormItem(
                        isFilled = false,
                        onClick = {
                            pickMedia.launch(PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                val totalSecondRowItems = (totalPhotos - 3) + (if (canAddMore) 1 else 0)
                if (totalSecondRowItems < 3) {
                    repeat(3 - totalSecondRowItems) {
                        Box(modifier = Modifier.weight(1f)) {}
                    }
                }
            }
        }
    }
}

@Composable
private fun PetPhotosFormItem(
    modifier: Modifier = Modifier,
    photoUri: String = "",
    isFilled: Boolean = false,
    onClick: () -> Unit = {},
    isCover: Boolean = false
) {
    Card(
        modifier = modifier.height(150.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(
            width = if (isCover) 2.dp else 0.dp,
            color = if (isCover) LimeGreen else Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isFilled) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUri)
                        .crossfade(true)
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_error)
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.add_photo),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview
@Composable
fun PetPhotosFormPreview() {
    PetsterTheme {
        PetPhotosForm(
            photoUris = listOf(
                PetUriState("uri1", ""),
                PetUriState("uri2", ""),
                PetUriState("uri3", ""),
                PetUriState("uri4", ""),
                PetUriState("uri5", "")
            ),
            modal = PetPhotoModalState(
                isShowed = true,
                state = PetPhotoState.ADD,
                photoUriIndex = 0
            ),
            imageCropper = ImageCropper(),
            onAddPhoto = { _, _ -> },
            onUpdatePhoto = { _, _, _ -> },
            onUpdateUrlPhoto = { _, _, _ -> },
            onRemovePhoto = {},
            onRemoveUrlPhoto = {},
            onSetCoverPhoto = {},
            onSetUrlPhotoAsCover = {},
            onToggleModal = {},
            onSetPhotoModal = { _, _ -> },
            onSetModalState = {},
            onResetPhotoModal = {}
        )
    }
}