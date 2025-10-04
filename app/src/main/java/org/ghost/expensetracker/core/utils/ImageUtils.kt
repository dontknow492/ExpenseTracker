package org.ghost.expensetracker.core.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {
    /**
     * Copies an image from a source Uri to the app's internal files directory.
     *
     * @param context The application context.
     * @param sourceUri The Uri of the image to copy (from photo picker, etc.).
     * @return The File object of the newly created cached image, or null on failure.
     */
    fun cacheImageFromUri(context: Context, sourceUri: Uri): File? {
        return try {
            // Get an InputStream from the source Uri
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null

            // Create a directory for avatars if it doesn't exist
            val avatarsDir = File(context.filesDir, "avatars")
            if (!avatarsDir.exists()) {
                avatarsDir.mkdirs()
            }

            // Create a destination file with a unique name
            val destinationFile = File(avatarsDir, "avatar_${System.currentTimeMillis()}.jpg")

            // Copy the data from the InputStream to the FileOutputStream
            FileOutputStream(destinationFile).use { outputStream ->
                inputStream.use {
                    it.copyTo(outputStream)
                }
            }

            destinationFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun deleteCacheImage(context: Context, cachedImageFile: String): Boolean{
        return File(cachedImageFile).delete()
    }
}