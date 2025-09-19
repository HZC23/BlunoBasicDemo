package com.dfrobot.angelo.ui.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.dfrobot.angelo.data.Telemetry

@Composable
fun Dashboard(telemetry: Telemetry) {
    Text("Dashboard Placeholder: ${telemetry.state}")
}
