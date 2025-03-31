package com.hexagraph.jagrati_android.ui.screens.omniscan

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hexagraph.jagrati_android.model.JagratiGroups
import com.hexagraph.jagrati_android.model.StudentDetails
import com.hexagraph.jagrati_android.model.Village
import com.hexagraph.jagrati_android.ui.screens.main.AddManuallyUIState
import com.hexagraph.jagrati_android.ui.screens.main.OmniScreens
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
                    ListRow(
                        image = studentDetails.faceBitmap(context),
                        heading = "${studentDetails.firstName} ${studentDetails.lastName}",
                        subheading = studentDetails.village.title,
                        sideText = "${studentDetails.currentGroupId}",
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

@Composable
fun ListRow(
    image: Bitmap?,
    heading: String,
    subheading: String,
    sideText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(modifier = modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(image!= null)
            Image(
                bitmap = image.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
            )else{
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "No Image",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = heading, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = subheading, fontSize = 14.sp, color = Color.Gray)
            }

            Text(text = sideText, fontSize = 14.sp, color = Color.Gray)
        }

        HorizontalDivider()
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
                        currentGroupId = JagratiGroups.GROUP_A,
                    )
                )
            ),
            onCardSelect = {},
            onBackPress = {},
            onQueryChange = {}
        )
    }
}