package com.hexagraph.jagrati_android.service.face_recognition

import android.content.Context
import com.hexagraph.jagrati_android.model.ProcessedImage

interface FaceRecognitionService {
    fun mobileNet(face: ProcessedImage, context: Context): Result<Float>
    fun recognizeFace(face: ProcessedImage, faces: List<ProcessedImage>, context: Context): List<ProcessedImage>?
}