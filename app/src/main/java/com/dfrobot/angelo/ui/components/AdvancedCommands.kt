package com.dfrobot.angelo.ui.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun AdvancedCommands(
    onSetSpeed: (Int) -> Unit,
    onGoTo: (Int) -> Unit,
    onToggleLight: (Boolean) -> Unit,
    onCalibrate: () -> Unit
) {
    Text("Advanced Commands Placeholder")
}
