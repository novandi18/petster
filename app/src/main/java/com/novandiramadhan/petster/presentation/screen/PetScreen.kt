package com.novandiramadhan.petster.presentation.screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.presentation.components.PetAdoptBar
import com.novandiramadhan.petster.presentation.components.PetInformation
import com.novandiramadhan.petster.presentation.components.VolunteerInformation
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.viewmodel.PetViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.placeholder
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.common.utils.WhatsappUtil.createWhatsAppUrl
import com.novandiramadhan.petster.common.utils.WhatsappUtil.generateWhatsAppMessage
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.UserResult
import com.novandiramadhan.petster.presentation.components.ErrorView
import com.novandiramadhan.petster.presentation.navigation.Destinations
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen
import com.novandiramadhan.petster.presentation.ui.theme.Red
import com.novandiramadhan.petster.presentation.ui.theme.White
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetScreen(
    viewModel: PetViewModel = hiltViewModel(),
    petId: String,
    back: () -> Unit = {},
    navigateTo: (Destinations) -> Unit = {}
) {
    val authState by viewModel.authState.collectAsState()
    val petState by viewModel.pet.collectAsState()
    val volunteerState by viewModel.volunteer.collectAsState()
    val shelterState by viewModel.shelterLoggedIn.collectAsState()
    val selectedImageIndex by viewModel.selectedImageIndex.collectAsState()
    val showDeleteConfirmation by viewModel.showDeleteConfirmation.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    val petFavStatus by viewModel.petFavStatus.collectAsState()
    val adoptedToggleState by viewModel.adoptedToggleState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit, authState?.userType) {
        viewModel.getPet(petId)

        if (authState?.userType == UserType.SHELTER) {
            viewModel.addViewedPet(petId)
        }
    }

    when (deleteState) {
        is Resource.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        is Resource.Success -> {
            LaunchedEffect(Unit) {
                viewModel.resetDeleteState()
                Toast.makeText(context, context.getString(R.string.delete_pet_success), Toast.LENGTH_SHORT).show()
                back()
                navigateTo(Destinations.YourPets)
            }
        }
        is Resource.Error -> {
            val errorMessage = stringResource((deleteState as Resource.Error).messageResId ?: R.string.error_unknown)

            AlertDialog(
                onDismissRequest = { viewModel.resetDeleteState() },
                title = {
                    Text(
                        text = stringResource(R.string.error)
                    )
                },
                text = {
                    Text(errorMessage)
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetDeleteState() }) {
                        Text(
                            text = stringResource(R.string.delete_pet),
                        )
                    }
                }
            )
        }
        else -> {}
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowDeleteConfirmation(false) },
            title = { Text(stringResource(R.string.delete_pet)) },
            text = { Text(stringResource(R.string.delete_pet_confirmation)) },
            confirmButton = {
                ElevatedButton(
                    onClick = {
                        viewModel.setShowDeleteConfirmation(false)
                        viewModel.deletePet()
                    },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Red,
                        contentColor = White
                    )
                ) {
                    Text(stringResource(R.string.delete_pet))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowDeleteConfirmation(false) }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            textContentColor = MaterialTheme.colorScheme.onBackground
        )
    }

    when (val status = petFavStatus) {
        is Resource.Loading -> {}
        is Resource.Success -> {
            Toast.makeText(
                context,
                status.data?.message,
                Toast.LENGTH_SHORT
            ).show()
            viewModel.resetPetFavStatus()
        }
        is Resource.Error -> {
            Toast.makeText(
                context,
                stringResource(status.messageResId ?: R.string.error_unknown),
                Toast.LENGTH_SHORT
            ).show()
            viewModel.resetPetFavStatus()
        }
        null -> {}
    }

    when (val status = adoptedToggleState) {
        is Resource.Loading -> {}
        is Resource.Success -> {
            Toast.makeText(
                context,
                status.data?.message,
                Toast.LENGTH_SHORT
            ).show()
            viewModel.resetAdoptedToggleState()
        }
        is Resource.Error -> {
            Toast.makeText(
                context,
                stringResource(status.messageResId ?: R.string.error_unknown),
                Toast.LENGTH_SHORT
            ).show()
            viewModel.resetAdoptedToggleState()
        }
        null -> {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.size(48.dp),
                        onClick = back
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    authState?.let { state ->
                        if (state.userType == UserType.SHELTER) {
                            IconButton(
                                modifier = Modifier.size(48.dp),
                                onClick = {
                                    petState?.data?.let { pet ->
                                        viewModel.togglePetFavorite(
                                            petId = pet.id ?: "",
                                            isFavorite = !pet.isFavorite
                                        )
                                    }
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onBackground
                                )
                            ) {
                                Icon(
                                    imageVector = if (petState?.data?.isFavorite == true)
                                        Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                    contentDescription = stringResource(R.string.favorite)
                                )
                            }
                        } else if (state.userType == UserType.VOLUNTEER) {
                            IconButton(
                                modifier = Modifier.size(48.dp),
                                onClick = {
                                    petState?.data?.let { pet ->
                                        val newAdoptedStatus = !pet.isAdopted
                                        viewModel.togglePetAdopted(
                                            petId = pet.id ?: "",
                                            isAdopted = newAdoptedStatus
                                        )
                                    }
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = if (petState?.data?.isAdopted == true) LimeGreen
                                    else MaterialTheme.colorScheme.onBackground
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Verified,
                                    contentDescription = if (petState?.data?.isAdopted == true)
                                        stringResource(R.string.set_as_available)
                                    else
                                        stringResource(R.string.set_as_adopted)
                                )
                            }

                            IconButton(
                                modifier = Modifier.size(48.dp),
                                onClick = {
                                    navigateTo(Destinations.UpdatePet(petId))
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onBackground
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = stringResource(R.string.update_pet)
                                )
                            }

                            IconButton(
                                modifier = Modifier.size(48.dp),
                                onClick = {
                                    viewModel.setShowDeleteConfirmation(true)
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onBackground
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = stringResource(R.string.delete_pet)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            when (val state = petState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        strokeWidth = 6.dp
                    )
                }

                is Resource.Error -> {
                    ErrorView(
                        title = stringResource(state.messageResId ?: R.string.error_unknown),
                        onRetry = viewModel::retry
                    )
                }

                is Resource.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(start = 24.dp, end = 24.dp, bottom = 124.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp)),
                            contentDescription = state.data?.name,
                            contentScale = ContentScale.Crop,
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(
                                    if (selectedImageIndex >= 0 &&
                                        state.data?.image?.imageUrls != null &&
                                        selectedImageIndex < state.data.image.imageUrls.size
                                    ) {
                                        state.data.image.imageUrls[selectedImageIndex]
                                    } else {
                                        state.data?.image?.imageCoverUrl
                                    }
                                )
                                .placeholder(R.drawable.image_placeholder)
                                .error(R.drawable.image_error)
                                .build(),
                        )

                        if (state.data?.image?.imageUrls?.isNotEmpty() == true) {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.data.image.imageUrls.size) { index ->
                                    val imageUrl = state.data.image.imageUrls[index]
                                    Card(
                                        modifier = Modifier.size(80.dp),
                                        onClick = {
                                            viewModel.selectImage(index)
                                        },
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        border = BorderStroke(
                                            width = if (index == selectedImageIndex) 2.dp else 0.dp,
                                            color = if (index == selectedImageIndex) LimeGreen else Color.Transparent
                                        )
                                    ) {
                                        AsyncImage(
                                            modifier = Modifier.fillMaxSize(),
                                            contentDescription = state.data.name,
                                            contentScale = ContentScale.Crop,
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(imageUrl)
                                                .placeholder(R.drawable.image_placeholder)
                                                .error(R.drawable.image_error)
                                                .build(),
                                        )
                                    }
                                }
                            }
                        }

                        if (state.data != null) {
                            PetInformation(
                                pet = state.data,
                            )
                        }

                        when (val volunteerState = volunteerState) {
                            is Resource.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .padding(24.dp),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            is Resource.Error -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        text = stringResource(
                                            volunteerState.messageResId ?: R.string.error_volunteer_pet
                                        ),
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            is Resource.Success -> {
                                val volunteer = when (val userResult = volunteerState.data) {
                                    is UserResult.VolunteerResult -> userResult.volunteer
                                    else -> null
                                }

                                volunteer?.let {
                                    VolunteerInformation(volunteer = it)
                                }
                            }

                            null -> {}
                        }
                    }
                }

                null -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        strokeWidth = 6.dp
                    )
                }
            }

            authState?.let { state ->
                if (state.userType == UserType.SHELTER) {
                    PetAdoptBar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        adoptFee = 100000,
                        isAdopted = petState?.data?.isAdopted == true,
                        onAdoptClick = {
                            if (petState is Resource.Success && shelterState is Resource.Success &&
                                volunteerState is Resource.Success) {
                                val pet = (petState as Resource.Success).data
                                val shelter = (shelterState as Resource.Success).data as UserResult.ShelterResult
                                val volunteer = (volunteerState as Resource.Success).data as UserResult.VolunteerResult
                                val volunteerPhone = volunteer.volunteer.phoneNumber

                                if (pet != null) {
                                    val messageText = generateWhatsAppMessage(pet, shelter.shelter, volunteer.volunteer)
                                    val whatsappUrl = createWhatsAppUrl(volunteerPhone, messageText)

                                    if (whatsappUrl != null) {
                                        context.startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                whatsappUrl.toUri()
                                            )
                                        )
                                    } else {
                                        Toast.makeText(
                                            context,
                                            R.string.error_failed_to_open_whatsapp,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    )
                } else if (state.userType == UserType.NONE) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = CircleShape
                            )
                            .padding(vertical = 16.dp, horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.auth_adopt_needed),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.background
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PetScreenPreview() {
    PetsterTheme {
        PetScreen(
            petId = "1"
        )
    }
}