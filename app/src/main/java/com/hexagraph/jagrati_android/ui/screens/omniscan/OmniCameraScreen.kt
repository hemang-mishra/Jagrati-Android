package com.hexagraph.jagrati_android.ui.screens.omniscan

import androidx.camera.core.ImageAnalysis
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
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
    CameraScreen(
        uiState = uiState.cameraScreenUiState,
        onExit = { onExit() },
        onAddManually = { /* Handle add manually */ },
        onDone = {
            omniScanViewModel.onDone()
        },
        onList = { /* Handle list */ },
        controller = controller,
        onFlipCamera = {
            omniScanViewModel.flipCamera {
                controller.cameraSelector = it
            }
        }
    )
}

@Composable
fun CameraScreen(
    uiState: CameraScreenUiState,
    onExit: () -> Unit,
    onAddManually: () -> Unit,
    onDone: () -> Unit,
    onList: () -> Unit,
    onFlipCamera: () -> Unit,
    controller: LifecycleCameraController
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.15f), tonalElevation = 4.dp
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onExit) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit"
                    )
                }
                IconButtonWithLabel(
                    icon = Icons.Default.Add,
                    label = "Add manually",
                    onClick = onAddManually
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
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
            if (uiState.currentImage.faceBitmap != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .width(100.dp)
                        .heightIn(max = 190.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                ) {
                    Image(
                        bitmap = uiState.currentImage.faceBitmap.asImageBitmap(),
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
                .fillMaxHeight(), tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                        contentDescription = "Flash"
                    )
                }

                    Box(
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    if (uiState.currentImage.faceBitmap != null)
                                        onDone()
                                }
                            )
                            .clip(CircleShape)
                            .size(70.dp)
                            .background(
                                MaterialTheme.colorScheme.onBackground.copy(
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
                    image = uiState.savedPeople.getOrNull(uiState.savedPeople.size-1)?.faceBitmap
                )
            }
        }
    }
}

@Composable
fun CameraText(text: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
    )
    {
        Text(
            maxLines = 1,
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onBackground,
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
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 5.dp)
                .heightIn(min = 5.dp)
        ) {
            if (image != null) {
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = "List",
                    modifier = Modifier
                        .width(30.dp)
                        .height(50.dp)
                )
                Text(
                    text = count.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}


@Composable
fun IconButtonWithLabel(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = label)
        }
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

