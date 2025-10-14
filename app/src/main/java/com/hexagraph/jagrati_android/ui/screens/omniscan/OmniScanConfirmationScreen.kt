//package com.hexagraph.jagrati_android.ui.screens.omniscan
//
//import android.graphics.Bitmap
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.ExperimentalLayoutApi
//import androidx.compose.foundation.layout.FlowRow
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import com.hexagraph.jagrati_android.R
//import com.hexagraph.jagrati_android.model.ProcessedImage
//import com.hexagraph.jagrati_android.ui.components.ScreenHeader
//import com.hexagraph.jagrati_android.ui.screens.main.CameraScreenUiState
//import com.hexagraph.jagrati_android.ui.screens.main.OmniScreens
//
//@Composable
//fun OmniScanConfirmationScreen(
//    viewModel: OmniScanViewModel,
//    onCompletedScan: () -> Unit
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    OmniScreenConfirmationBase(
//        cameraScreenUiState = uiState.cameraScreenUiState,
//        onBackPress = {
//            viewModel.navigate(OmniScreens.CAMERA_SCREEN)
//        },
//        onSwitchToCameraScreenClick = {
//            viewModel.navigate(OmniScreens.CAMERA_SCREEN)
//        },
//        onDeletePress = {
//            viewModel.deleteSelectedFaces()
//        },
//        onCompletedScan = {
//            onCompletedScan()
//        },
//        onSelectFace = { selectedFace ->
//            viewModel.selectFace(selectedFace)
//        },
//        onSwitchToAddManually = {
//            viewModel.navigate(OmniScreens.ADD_MANUALLY_SCREEN)
//        }
//    )
//}
//
//@Composable
//private fun OmniScreenConfirmationBase(
//    cameraScreenUiState: CameraScreenUiState,
//    onBackPress: () -> Unit,
//    onDeletePress: () -> Unit,
//    onSelectFace: (ProcessedImage) -> Unit,
//    onSwitchToCameraScreenClick: () -> Unit,
//    onSwitchToAddManually: () -> Unit,
//    onCompletedScan: () -> Unit
//) {
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.SpaceBetween
//    ) {
//        Column(
//            modifier = Modifier.fillMaxHeight(0.7f)
//        ) {
//            Column(modifier = Modifier.fillMaxWidth()
//                ) {
//                ScreenHeader(
//                    modifier = Modifier.padding(8.dp),
//                    onBackPress = onBackPress,
//                    trailingContent = {
//                        if (cameraScreenUiState.selectedPeople.isNotEmpty())
//                            IconButton(onClick = onDeletePress) {
//                                Icon(
//                                    imageVector = Icons.Default.Delete,
//                                    tint = MaterialTheme.colorScheme.error,
//                                    contentDescription = "Delete selected"
//                                )
//                            }
//                    },
//                    title = "Confirm scans",
//                )
//
//            }
//            RecognizedFacesGrid(
//                recognizedFaces = cameraScreenUiState.savedPeople,
//                selectedFaces = cameraScreenUiState.selectedPeople
//            ) { image ->
//                onSelectFace(image)
//            }
//        }
//        Column(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier.clickable {
//                    onSwitchToCameraScreenClick()
//                }
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(80.dp)
//                        .clip(CircleShape)
//                        .background(MaterialTheme.colorScheme.tertiaryContainer.copy(0.4f))
//
//                )
//                Icon(
//                    painter = painterResource(R.drawable.baseline_photo_camera_24),
//                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
//                    contentDescription = "Camera Icon",
//                    modifier = Modifier
//                        .size(60.dp)
//                )
//            }
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 4.dp, end = 4.dp, bottom = 16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                TextButton(onClick = onSwitchToAddManually) {
//                    Text(
//                        text = "Add manually",
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//                TextButton(onClick = onCompletedScan) {
//                    Text(
//                        text = "Completed",
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
//        }
//    }
//}
//
//
//@OptIn(ExperimentalLayoutApi::class)
//@Composable
//private fun RecognizedFacesGrid(
//    recognizedFaces: List<ProcessedImage>,
//    selectedFaces: List<ProcessedImage>,
//    onSelectFace: (ProcessedImage) -> Unit
//) {
//    val scrollState = rememberScrollState()
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//            .clip(RoundedCornerShape(32.dp))
//            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
//            .padding(16.dp)
//            .verticalScroll(scrollState)
//    ) {
//        Text(
//            text = "${recognizedFaces.size} Recognitions successful",
//            style = MaterialTheme.typography.titleMedium,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )
//
//        FlowRow(
//            modifier = Modifier.fillMaxWidth(),
//        ) {
//            recognizedFaces.forEach { face ->
//                val isSelected = selectedFaces.contains(face)
//                SelectableCard(
//                    name = face.name,
//                    bitmap = face.faceBitmap,
//                    isSelected = isSelected,
//                    onSelectCard = { onSelectFace(face) }
//                )
//            }
//        }
//    }
//}
//
//
//@Composable
//private fun SelectableCard(
//    modifier: Modifier = Modifier,
//    name: String,
//    bitmap: Bitmap?,
//    isSelected: Boolean,
//    onSelectCard: () -> Unit
//) {
//    val backgroundColor = if (isSelected) {
//        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
//    } else {
//        Color.Transparent
//    }
//
//    Card(
//        modifier = modifier
//            .padding(4.dp)
//            .height(100.dp)
//            .width(80.dp)
//            .pointerInput(Unit) {
//                detectTapGestures(
//                    onLongPress = { onSelectCard() }
//                )
//            },
//        colors = CardDefaults.cardColors(containerColor = backgroundColor),
//        shape = RoundedCornerShape(16.dp),
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(8.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(60.dp)
//                    .clip(CircleShape)
//                    .background(Color.Gray),
//                contentAlignment = Alignment.Center
//            ) {
//                bitmap?.let {
//                    Image(
//                        bitmap = it.asImageBitmap(),
//                        contentDescription = "Profile Image",
//                        modifier = Modifier.fillMaxSize(),
//                        contentScale = ContentScale.Crop
//                    )
//                } ?: Icon(
//                    imageVector = Icons.Default.Person,
//                    contentDescription = "Default Icon",
//                    modifier = Modifier.size(40.dp),
//                    tint = Color.White
//                )
//            }
//
//            Spacer(modifier = Modifier.height(4.dp))
//
//            Text(
//                text = name,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                textAlign = TextAlign.Center,
//                style = MaterialTheme.typography.bodySmall
//            )
//        }
//    }
//}
