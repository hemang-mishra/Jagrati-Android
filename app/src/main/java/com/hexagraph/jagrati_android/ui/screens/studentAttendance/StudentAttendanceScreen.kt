package com.hexagraph.jagrati_android.ui.screens.studentAttendance

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.JagratiGroups
import com.hexagraph.jagrati_android.ui.components.ScreenHeader
import com.hexagraph.jagrati_android.ui.components.StudentListRow
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanMainScreen
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanUseCases
import com.hexagraph.jagrati_android.util.TimeUtils
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAttendanceScreen(
    viewModel: StudentAttendanceViewModel = koinViewModel(),
    onBackPress: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var isDatePickerDialogVisible by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        viewModel.onEventDateSelected(System.currentTimeMillis(), context)
    }
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates{
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis <= TimeUtils.getTimeMillisRangeForDate(System.currentTimeMillis()).second
        }
    })
    AnimatedContent(uiState.isScanModeActive) {
        if (it) {
            OmniScanMainScreen(
                useCases = OmniScanUseCases.STUDENT_ATTENDANCE,
                omniScanCallback = {
                    viewModel.takeAttendance(it, context)
                    viewModel.switchView()
                },
                initialList = uiState.allProcessedImage,
                onExit = {
                    viewModel.switchView()
                }
            )
        } else {
            StudentAttendanceScreenBase(
                uiState = uiState,
                onBackPress = onBackPress,
                onClickCamera = {
                    viewModel.switchView()
                },
                onTitleClick = {
                    isDatePickerDialogVisible = true
                }
            )
        }
    }

    if (isDatePickerDialogVisible)
        DatePickerDialog(
            onDismissRequest = {
                isDatePickerDialogVisible = false
            },
            confirmButton = {
                TextButton(onClick = {
                    isDatePickerDialogVisible = false
                    if (datePickerState.selectedDateMillis != null) {
                        viewModel.onEventDateSelected(
                            datePickerState.selectedDateMillis!!,
                            context
                        )
                    }
                }) {
                    Text("Done")
                }
            }) {
            DatePicker(datePickerState)
        }
}


@Composable
private fun StudentAttendanceScreenBase(
    uiState: StudentAttendanceUIState,
    onBackPress: () -> Unit,
    onTitleClick: () -> Unit,
    onClickCamera: () -> Unit
) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            isTitleClickable = true,
            onTitleClick = onTitleClick,
            modifier = Modifier,
            onBackPress = onBackPress,
            title = TimeUtils.convertMillisToDate(uiState.dateMillis),
            trailingContent = {
                IconButton(
                    onClick = onClickCamera
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_photo_camera_24),
                        contentDescription = "Camera"
                    )
                }
            }
        )
        Box(modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
        ){

        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                var count = 1
                SelectionContainer {
                    Column {
                        JagratiGroups.entries.forEach { group ->
                            val studentDetails =
                                uiState.allStudentDetails.filter { it.currentGroupId == group }
                            if (studentDetails.isNotEmpty()) {
                                Text("$group")
                                studentDetails.forEach {
                                    Text("${count++} ${it.firstName} ${it.lastName} ${it.village.title}")
                                }
                            }
                        }
                    }
                }
            }
            JagratiGroups.entries.forEach { group->
                val studentDetails = uiState.allStudentDetails.filter { it.currentGroupId == group}
                if(studentDetails.isNotEmpty()) {
                    item {
                        Text(
                            "$group",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    items(studentDetails.size) { index ->
                        StudentListRow(
                            image = studentDetails[index].faceBitmap(context = context),
                            heading = "${studentDetails[index].firstName} ${studentDetails[index].lastName}",
                            subheading = studentDetails[index].village.title,
                            sideText = studentDetails[index].currentGroupId.groupName,
                            modifier = Modifier.padding(8.dp),
                            onClick = {
//                            onCardSelect(studentDetails)
                            }
                        )
                    }
                    item {
                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}