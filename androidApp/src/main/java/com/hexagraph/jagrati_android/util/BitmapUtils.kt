package com.hexagraph.jagrati_android.util

import android.content.Context
import android.graphics.Bitmap
import com.hexagraph.jagrati_android.model.Student
import com.hexagraph.jagrati_android.util.FileUtility.readBitmapFromFile


fun Student.faceBitmap(context: Context): Bitmap? = context.readBitmapFromFile(faceFileName).getOrNull()
fun Student.imageBitmap(context: Context): Bitmap? = context.readBitmapFromFile(imageFileName).getOrNull()
fun Student.frameBitmap(context: Context): Bitmap? = context.readBitmapFromFile(frameFileName).getOrNull()