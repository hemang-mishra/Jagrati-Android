package com.hexagraph.jagrati_android.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import java.io.File

object FileUtility {
    val isExternalStorageWriteable get(): Boolean = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    fun Context.externalFile(fileName: String, type: String? = null) = File(getExternalFilesDir(type), fileName)
    fun Context.internalFile(fileName: String) = File(filesDir, fileName)
    fun Context.privateFile(fileName: String) = if (isExternalStorageWriteable) externalFile(fileName) else internalFile(fileName)

    fun Context.writeBitmapIntoFile(fileName: String, bitmap: Bitmap) = runCatching{
        val file = privateFile(fileName)
        if(!file.exists()) file.createNewFile()
        if(!file.canWrite()) throw Throwable("Unable to write file $fileName")
        val outputStream = file.outputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.close()
        return@runCatching file.exists()
    }.onFailure {
        Log.e("FileUtility", it.message?:"Error while writing bitmap into file")
    }


    fun Context.readBitmapFromFile(fileName: String): Result<Bitmap> = runCatching {
        val file = privateFile(fileName)
        if(!file.canRead()) throw Throwable("Unable to read file $fileName")
        val inputStream = file.inputStream()
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        return@runCatching bitmap
    }.onFailure {
        Log.e("FileUtility", it.message?:"Error while reading bitmap from file")
    }

}