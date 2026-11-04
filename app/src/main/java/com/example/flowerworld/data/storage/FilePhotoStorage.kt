package com.example.flowerworld.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import com.example.flowerworld.domain.storage.PhotoStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID

/**
 * Stores photos as downscaled JPEG files in the private `files/photos` folder of the app.
 * Nothing outside the app can read them and no storage permission is needed.
 */
class FilePhotoStorage(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PhotoStorage {

    private val photosDir: File
        get() = File(context.filesDir, PhotosDirName).apply { mkdirs() }

    override suspend fun import(sourceUri: String): String = withContext(dispatcher) {
        val uri = Uri.parse(sourceUri)
        val bitmap = decodeScaled(uri)
        val rotated = rotateByExif(uri, bitmap)
        val file = File(photosDir, "${UUID.randomUUID()}.jpg")
        try {
            file.outputStream().use { rotated.compress(Bitmap.CompressFormat.JPEG, JpegQuality, it) }
        } catch (e: IOException) {
            file.delete()
            throw e
        } finally {
            if (rotated !== bitmap) rotated.recycle()
            bitmap.recycle()
        }
        file.absolutePath
    }

    override suspend fun delete(path: String) {
        withContext(dispatcher) { File(path).delete() }
    }

    private fun open(uri: Uri) =
        context.contentResolver.openInputStream(uri) ?: throw IOException("Cannot open $uri")

    /** Decodes the image so that its longer side is not larger than [MaxSidePx]. */
    private fun decodeScaled(uri: Uri): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        open(uri).use { BitmapFactory.decodeStream(it, null, bounds) }
        val longSide = maxOf(bounds.outWidth, bounds.outHeight)
        if (longSide <= 0) throw IOException("Not an image: $uri")

        var sample = 1
        while (longSide / (sample * 2) >= MaxSidePx) sample *= 2
        val options = BitmapFactory.Options().apply { inSampleSize = sample }
        val decoded = open(uri).use { BitmapFactory.decodeStream(it, null, options) }
            ?: throw IOException("Cannot decode $uri")

        val scale = MaxSidePx.toFloat() / maxOf(decoded.width, decoded.height)
        if (scale >= 1f) return decoded
        val scaled = Bitmap.createScaledBitmap(
            decoded,
            (decoded.width * scale).toInt().coerceAtLeast(1),
            (decoded.height * scale).toInt().coerceAtLeast(1),
            true
        )
        if (scaled !== decoded) decoded.recycle()
        return scaled
    }

    /** Camera photos keep their orientation in EXIF; bake it into the pixels. */
    private fun rotateByExif(uri: Uri, bitmap: Bitmap): Bitmap {
        val orientation = runCatching {
            open(uri).use {
                ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            }
        }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private companion object {
        const val PhotosDirName = "photos"
        const val MaxSidePx = 800
        const val JpegQuality = 80
    }
}
