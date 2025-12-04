package com.intive.aifirst.petspot.features.reportmissing.ui.photo

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.intive.aifirst.petspot.R
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.PhotoEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.PhotoViewModel

/**
 * State host composable for Photo screen (Step 2/4).
 * Collects state from ViewModel, handles photo picker, and dispatches intents.
 *
 * Navigation is handled via callbacks injected into the ViewModel,
 * following the hybrid pattern.
 *
 * @param viewModel Screen-specific PhotoViewModel (injected with repository + flowState + callbacks)
 * @param modifier Modifier for the component
 */
@Composable
fun PhotoScreen(
    viewModel: PhotoViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Photo picker launcher for API 33+
    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                if (uri != null) {
                    viewModel.handleIntent(ReportMissingIntent.PhotoSelected(uri.toString()))
                } else {
                    viewModel.handleIntent(ReportMissingIntent.PhotoPickerCancelled)
                }
            },
        )

    // Legacy photo picker launcher for API < 33
    val legacyPhotoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                if (uri != null) {
                    viewModel.handleIntent(ReportMissingIntent.PhotoSelected(uri.toString()))
                } else {
                    viewModel.handleIntent(ReportMissingIntent.PhotoPickerCancelled)
                }
            },
        )

    // Handle effects (LaunchPhotoPicker, ShowToast)
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PhotoEffect.LaunchPhotoPicker -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    } else {
                        legacyPhotoPickerLauncher.launch("image/*")
                    }
                }
                is PhotoEffect.ShowPhotoMandatoryToast -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.report_missing_photo_mandatory),
                        Toast.LENGTH_LONG,
                    ).show()
                }
                is PhotoEffect.ShowMetadataFailedToast -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.report_missing_photo_metadata_failed),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    // Handle system back button/gesture
    BackHandler {
        viewModel.handleIntent(ReportMissingIntent.NavigateBack)
    }

    PhotoContent(
        photoAttachment = state,
        modifier = modifier,
        onBrowseClick = { viewModel.handleIntent(ReportMissingIntent.OpenPhotoPicker) },
        onRemovePhotoClick = { viewModel.handleIntent(ReportMissingIntent.RemovePhoto) },
        onBackClick = { viewModel.handleIntent(ReportMissingIntent.NavigateBack) },
        onContinueClick = { viewModel.handleIntent(ReportMissingIntent.NavigateNext) },
    )
}
