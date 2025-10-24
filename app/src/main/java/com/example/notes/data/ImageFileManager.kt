package com.example.notes.data

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class ImageFileManager @Inject constructor(
    @ApplicationContext val context: Context
) {
    val imageDir = context.filesDir
    suspend fun copyImageToInternalStorage(url: String) : String{
        val file = File(imageDir, "img_${UUID.randomUUID()}.jpg")
        withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(url.toUri()).use {inputStream ->
                file.outputStream().use {outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }
        }
        return file.absolutePath
    }

    suspend fun deleteImageInternalStorage(url: String) {
        val file = File(url)
        withContext(Dispatchers.IO) {
            if (file.exists()) {
                file.delete()
            }
        }
    }

    fun isInternal(url: String) : Boolean{
        return url.startsWith(imageDir.absolutePath)
    }
}