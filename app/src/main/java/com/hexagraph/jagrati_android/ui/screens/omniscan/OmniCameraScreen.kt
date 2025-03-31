package com.hexagraph.jagrati_android.ui.screens.omniscan

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.Bitmap
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.ui.screens.main.CameraScreenUiState
import com.hexagraph.jagrati_android.ui.screens.main.OmniScreens
import com.hexagraph.jagrati_android.ui.theme.Dark_Background
import com.hexagraph.jagrati_android.ui.theme.onDark_Background

@Composable
fun OmniScanCameraScreen(
    omniScanViewModel: OmniScanViewModel,
    onExit: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by omniScanViewModel.uiState.collectAsState()
    val controller = remember {
        val executor = ContextCompat.getMainExecutor(context.applicationContext)
        LifecycleCameraController(context.applicationContext).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(
                executor,
                omniScanViewModel.getImageAnalyzer(context, executor)
            )
            imageAnalysisOutputImageFormat = ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888
            imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
        }

    }
    LaunchedEffect(Unit) {
        controller.cameraSelector = if(uiState.cameraScreenUiState.lensFacing == CameraSelector.LENS_FACING_BACK)
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        else
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
    }
    CameraScreen(
        uiState = uiState.cameraScreenUiState,
        onExit = { onExit() },
        onAddManually = {
            omniScanViewModel.navigate(OmniScreens.ADD_MANUALLY_SCREEN)
        },
        onClick = {
            omniScanViewModel.onClickOk()
        },
        onList = {
            omniScanViewModel.navigate(OmniScreens.CONFIRMATION_SCREEN)
        },
        controller = controller,
        onFlipCamera = {
            omniScanViewModel.flipCamera {
                controller.cameraSelector = it
            }
        },
        addFace = {
            omniScanViewModel.addFace()
        },
        onDone = {
            omniScanViewModel.navigate(OmniScreens.CONFIRMATION_SCREEN)
        }
    )
}

@Composable
fun CameraScreen(
    uiState: CameraScreenUiState,
    onExit: () -> Unit,
    onAddManually: () -> Unit,
    onClick: () -> Unit,
    onList: () -> Unit,
    onFlipCamera: () -> Unit,
    addFace: () -> Unit,
    controller: LifecycleCameraController,
    onDone: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
//    Log.i("Face Captured", "CameraScreen recomposed")
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f), tonalElevation = 4.dp,
            color = Dark_Background
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onExit) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit",
                        tint = onDark_Background
                    )
                }
                TextButton(
                    enabled = uiState.currentImage.faceBitmap!= null && !uiState.recognizedImage.matchesCriteria,
                    onClick = addFace
                ) {
                    Text("Save as New", fontSize = 16.sp)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
        )
        {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    PreviewView(it).apply {
                        this.controller = controller
                        controller.bindToLifecycle(lifecycleOwner)
                    }
                }
            )
            if (uiState.currentImage.frame != null) {
                Image(
                    bitmap = uiState.currentImage.frame!!.asImageBitmap(),
                    contentDescription = "Image Frame",
                    modifier = Modifier.fillMaxSize()
                )
            }
            if (uiState.recognizedImage.faceBitmap != null || uiState.currentImage.faceBitmap != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .width(100.dp)
                        .heightIn(max = 190.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Dark_Background.copy(alpha = 0.5f))
                ) {
                    Image(
                        bitmap = uiState.recognizedImage.faceBitmap?.asImageBitmap()
                            ?: uiState.currentImage.faceBitmap!!.asImageBitmap(),
                        contentDescription = "Face",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth()
                    )
                    Text(
                        if (!uiState.recognizedImage.name.isEmpty()) uiState.recognizedImage.name else "Unknown",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .weight(1f),
                        color = onDark_Background,
                        maxLines = 1,
                        fontSize = 12.sp
                    )
                    if (!uiState.recognizedImage.name.isEmpty() && uiState.recognizedImage.similarity != null)
                        Text(
                            "${String.format("%.1f", uiState.recognizedImage.similarity * 100F)}%",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .weight(1f),
                            maxLines = 1,
                            color = onDark_Background,
                            fontSize = 12.sp
                        )
                }
            } else {
                CameraText(text = "Looking For Humans", Modifier.align(Alignment.Center))
            }
        }

        // Bottom bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(), tonalElevation = 4.dp,
            color = Dark_Background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            onFlipCamera()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_flip_camera_android_24),
                            contentDescription = "Flash",
                            tint = onDark_Background
                        )
                    }

                    Box(
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        if (uiState.currentImage.faceBitmap != null)
                                            onClick()
                                    },
                                    onLongPress = {
                                        addFace()
                                    }
                                )
                            }
                            .clip(CircleShape)
                            .size(70.dp)
                            .background(
                                onDark_Background.copy(
                                    if (uiState.currentImage.faceBitmap != null)
                                        0.8f
                                    else 0.2f
                                )
                            )
                    )

                    ListViewIcon(
                        modifier = Modifier,
                        onClick = {
                            onList()
                        },
                        count = uiState.savedPeople.size,
                        image = uiState.savedPeople.getOrNull(uiState.savedPeople.size - 1)?.faceBitmap
                    )
                }

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onAddManually
                    ) {
                        Text(
                            "Add manually",
                            fontSize = 16.sp
                        )
                    }
                    TextButton(
                        onClick = onDone
                    ) {
                        Text("Done", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CameraText(text: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Dark_Background.copy(alpha = 0.5f))
    )
    {
        Text(
            maxLines = 1,
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = onDark_Background,
            fontSize = 12.sp
        )
    }
}

@Composable
fun ListViewIcon(
    modifier: Modifier,
    onClick: () -> Unit,
    count: Int = 0,
    image: Bitmap? = null
) {
        Box(
            modifier = modifier
                .widthIn(min = 40.dp)
                .heightIn(min = 5.dp)
                .clickable{
                    if(image != null)
                    onClick()
                }
        ) {
            if (image != null) {
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = "List",
                    modifier = Modifier
                        .width(40.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = count.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp
                    )
                }
            }
        }

}


@Composable
fun IconButtonWithLabel(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon, contentDescription = label,
                tint = onDark_Background
            )
        }
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

