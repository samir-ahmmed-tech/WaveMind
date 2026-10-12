package com.example.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyPolicyScreen(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Privacy Policy",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp, top = 24.dp)
        )
        Text(
            text = """
                Last updated: January 2026

                1. Introduction
                Welcome to WaveMind. This privacy policy explains how we collect, use, and protect your data.

                2. Data Collection
                We do not collect any personally identifiable information (PII). Game progress and settings are stored locally on your device.

                3. Advertising
                We use Google AdMob to display advertisements. AdMob may collect and use data for personalized advertising based on your consent.

                4. Third-party Services
                The app uses third-party services that may collect information used to identify you, such as Google Play Services and Google AdMob.

                5. Changes to This Privacy Policy
                We may update our Privacy Policy from time to time. You are advised to review this page periodically for any changes.

                6. Contact Us
                If you have any questions or suggestions about our Privacy Policy, do not hesitate to contact us.
            """.trimIndent(),
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Accept & Continue")
        }
    }
}
