package com.novandiramadhan.petster.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.states.UploadState
import com.novandiramadhan.petster.presentation.components.DropdownField
import com.novandiramadhan.petster.presentation.components.LoadingDialog
import com.novandiramadhan.petster.presentation.components.PetPhotosForm
import com.novandiramadhan.petster.presentation.components.TextDialog
import com.novandiramadhan.petster.presentation.components.TextFormFieldNoIcon
import com.novandiramadhan.petster.presentation.components.TextFormFieldRupiah
import com.novandiramadhan.petster.presentation.components.TextFormMultiField
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.viewmodel.NewPetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPetScreen(
    viewModel: NewPetViewModel = hiltViewModel(),
    back: () -> Unit = {}
) {
    val uploadState by viewModel.uploadState.collectAsState(UploadState.Idle)
    val isValid by viewModel.isFieldsValid.collectAsState()

    when (uploadState) {
        is UploadState.Loading -> {
            LoadingDialog(
                title = stringResource(R.string.post_loading)
            )
        }
        is UploadState.Success -> {
            viewModel.resetState()
            viewModel.setUploadState(UploadState.Idle)
            back()
        }
        is UploadState.Error -> {
            TextDialog(
                onDismissRequest = {
                    viewModel.setUploadState(UploadState.Idle)
                },
                text = (uploadState as UploadState.Error).message
            )
        }
        else -> {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.post_pet)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = back) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                actions = {
                    ElevatedButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = {
                            viewModel.uploadImagesAndPostPet()
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        enabled = isValid
                    ) {
                        Text(
                            text = stringResource(R.string.post)
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            NewPetContent()
        }
    }
}

@Composable
private fun NewPetContent(
    viewModel: NewPetViewModel = hiltViewModel()
) {
    val petState = viewModel.pet.collectAsState().value
    val photoUris by viewModel.photoUris.collectAsState()
    val modal by viewModel.photoModal.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PetPhotosForm(
            photoUris = photoUris,
            modal = modal,
            imageCropper = viewModel.imageCropper,
            onAddPhoto = viewModel::addPhoto,
            onUpdatePhoto = viewModel::updatePhotoUri,
            onUpdateUrlPhoto = { _, _, _ -> },
            onRemovePhoto = viewModel::removePhoto,
            onRemoveUrlPhoto = {},
            onSetCoverPhoto = viewModel::setCoverPhoto,
            onSetUrlPhotoAsCover = {},
            onToggleModal = viewModel::toggleModal,
            onSetPhotoModal = viewModel::setPhotoModal,
            onSetModalState = viewModel::setModalState,
            onResetPhotoModal = viewModel::resetPhotoModal,
        )

        TextFormFieldNoIcon(
            title = stringResource(R.string.pet_name),
            value = petState.name ?: "",
            onValueChange = viewModel::setPetName
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextFormFieldNoIcon(
                modifier = Modifier.weight(.7f),
                title = stringResource(R.string.pet_age),
                value = viewModel.pet.value.age?.toString() ?: "",
                onValueChange = viewModel::setPetAge,
                keyboardType = KeyboardType.Number
            )

            DropdownField(
                modifier = Modifier.weight(.4f),
                options = stringArrayResource(
                    R.array.pet_age_units
                ).toList(),
                title = stringResource(R.string.pet_age_unit),
                onOptionSelected = viewModel::setPetAgeUnit,
                selectedOption = petState.ageUnit ?: stringArrayResource(
                    R.array.pet_age_units
                ).first()
            )
        }

        DropdownField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            options = stringArrayResource(
                R.array.pet_categories
            ).toList(),
            title = stringResource(R.string.pet_category),
            onOptionSelected = viewModel::setPetCategory,
            selectedOption = petState.category ?: stringArrayResource(
                R.array.pet_categories
            ).first()
        )

        DropdownField(
            modifier = Modifier.fillMaxWidth(),
            options = stringArrayResource(
                R.array.pet_gender
            ).toList(),
            title = stringResource(R.string.pet_gender),
            onOptionSelected = viewModel::setPetGender,
            selectedOption = petState.gender ?: stringArrayResource(
                R.array.pet_gender
            ).first()
        )

        TextFormFieldNoIcon(
            title = stringResource(R.string.pet_color),
            value = viewModel.pet.value.color ?: "",
            onValueChange = viewModel::setPetColor
        )

        TextFormFieldNoIcon(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            title = stringResource(R.string.breed),
            value = viewModel.pet.value.breed ?: "",
            onValueChange = viewModel::setPetBreed,
            isRequired = false
        )

        DropdownField(
            modifier = Modifier.fillMaxWidth(),
            options = stringArrayResource(R.array.pet_size).toList(),
            title = stringResource(R.string.pet_size),
            onOptionSelected = viewModel::setPetSize,
            selectedOption = petState.size ?: stringArrayResource(
                R.array.pet_size
            ).first()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextFormFieldNoIcon(
                modifier = Modifier.weight(.5f),
                title = stringResource(R.string.weight),
                value = viewModel.pet.value.weight ?: "",
                onValueChange = viewModel::setPetWeight,
                keyboardType = KeyboardType.Number
            )

            DropdownField(
                modifier = Modifier.weight(.5f),
                options = stringArrayResource(R.array.pet_weight_units).toList(),
                title = stringResource(R.string.pet_weight_unit),
                onOptionSelected = viewModel::setPetWeightUnit,
                selectedOption = petState.weightUnit ?: stringArrayResource(
                    R.array.pet_weight_units
                ).first()
            )
        }

        TextFormMultiField(
            modifier = Modifier.padding(bottom = 12.dp),
            title = stringResource(R.string.pet_disabilities),
            fields = stringArrayResource(R.array.pet_disabilities).toList(),
            selectedFields = petState.disabilities ?: emptyList(),
            onSelectionChanged = viewModel::setPetDisabilities
        )

        DropdownField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            options = stringArrayResource(
                R.array.confirm
            ).toList(),
            title = stringResource(R.string.pet_vaccinated),
            onOptionSelected = viewModel::setPetVaccinated,
            selectedOption = stringArrayResource(R.array.confirm)[
                if (petState.isVaccinated) 0 else 1
            ]
        )

        TextFormMultiField(
            modifier = Modifier.padding(bottom = 12.dp),
            title = stringResource(R.string.pet_behaviour),
            fields = stringArrayResource(R.array.pet_behaviors).toList(),
            selectedFields = petState.behaviours ?: emptyList(),
            onSelectionChanged = viewModel::setPetBehaviors
        )

        TextFormFieldRupiah(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            title = stringResource(R.string.adopt_fee),
            value = viewModel.pet.value.adoptionFee?.toString() ?: "",
            onValueChange = viewModel::setPetAdoptFee
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NewPetScreenPreview() {
    PetsterTheme {
        NewPetScreen()
    }
}