package com.hexagraph.jagrati_android.ui.screens.omniscan

import com.hexagraph.jagrati_android.model.ProcessedImage

data class OmniScanCallback(
    val onListGeneration: (List<ProcessedImage>)->Unit = {}
)