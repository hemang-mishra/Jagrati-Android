package com.hexagraph.jagrati_android.ui.screens.omniscan

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hexagraph.jagrati_android.model.JagratiGroups
import com.hexagraph.jagrati_android.model.StudentDetails
import com.hexagraph.jagrati_android.model.Village
import com.hexagraph.jagrati_android.ui.components.StudentListRow
import com.hexagraph.jagrati_android.ui.screens.main.AddManuallyUIState
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme

@Composable
fun AddManuallyScreen(
    viewModel: OmniScanViewModel,
    onBackPress: () -> Unit
){
    val uiState by viewModel.uiState.collectAsState()
    AddManuallyScreenBase(
        uiState = uiState.addManuallyUIState,
        onCardSelect = {
            viewModel.onSelectStudentInAddManuallyScreen(it)
        },
        onBackPress = onBackPress,
        onQueryChange = {
            viewModel.imageSearchQuery(it)
        }
    )
}

@Composable
fun AddManuallyScreenBase(
    uiState: AddManuallyUIState,
    onCardSelect: (StudentDetails)->Unit,
    onBackPress: ()->Unit,
    onQueryChange: (String)->Unit
){
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPress) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }

            TextField(
                textStyle = TextStyle(
                    fontSize = 20.sp
                ),
                value = uiState.queryString,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = "Enter Name",
                        style = TextStyle(fontSize = 20.sp),
                    )
                },
                modifier = Modifier.weight(1f)
                    .focusRequester(focusRequester),

                singleLine = true
            )

            IconButton(onClick = {
                onQueryChange("")
                focusRequester.requestFocus()
                keyboardController?.show()
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                )
            }
        }
        if(uiState.queriedData.isEmpty()){
            Text("No data found")
        }else{
            LazyColumn {
                items(uiState.queriedData.size){index->
                    val studentDetails = uiState.queriedData[index]
                    StudentListRow(
                        image = studentDetails.faceBitmap(context),
                        heading = "${studentDetails.firstName} ${studentDetails.lastName}",
                        subheading = studentDetails.village.title,
                        sideText = studentDetails.currentGroupId.groupName,
                        modifier = Modifier.padding(8.dp),
                        onClick = {
                            onCardSelect(studentDetails)
                        }
                    )

                }
            }
        }
    }
}



@Preview(showBackground = true)
@Preview(showBackground = true, uiMode =  Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AddManuallyScreenPreview(){
    JagratiAndroidTheme {
        AddManuallyScreenBase(
            uiState = AddManuallyUIState(
                queryString = "John Doe",
                queriedData = listOf(
                    StudentDetails(
                        firstName = "John",
                        lastName = "Doe",
                        village = Village.KAKARTALA,
                        currentGroupId = JagratiGroups.GROUP_B,
                    ),
                    StudentDetails(
                        firstName = "Jane",
                        lastName = "Doe",
                        village = Village.MEHAGWAN,
                        currentGroupId = JagratiGroups.GROUP_A1,
                    )
                )
            ),
            onCardSelect = {},
            onBackPress = {},
            onQueryChange = {}
        )
    }
}