package com.hexagraph.jagrati_android.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.R

@Composable
fun OnboardingScreen1(
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Title
        Text(
            text = "Welcome to Jagrati",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        // Image
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with actual onboarding image
            contentDescription = "Onboarding Image 1",
            modifier = Modifier
                .size(300.dp)
                .padding(32.dp)
        )
        
        // Description
        Text(
            text = "Jagrati is a non-profit organization at IIITDM Jabalpur dedicated to education and social welfare.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Next button
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(text = "Next")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}