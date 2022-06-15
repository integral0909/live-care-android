package com.onseen.livecare.models.Utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore


class UtilsBaseFunction {

    companion object {
        fun isValidEmail(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isValidPassword(password: String): Boolean {
            return (password.length >= 6)
        }

        fun showAlert(context: Context, title: String?, message: String, buttonHandler: (() -> Unit)? = null) {
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, _ ->
                    if (buttonHandler != null) buttonHandler()
                    dialog.dismiss()
                }
            val alert = dialogBuilder.create()
            alert.setTitle(title)
            alert.show()
        }

        fun isContainKey(json: JSONObject?, key: String): Boolean {
            if(json == null || key.isEmpty()) return false
            if(json.has(key) && !json.isNull(key)) return true

            return false
        }

        fun resizeByWidth(bitmap: Bitmap, width:Int): Bitmap {
            val ratio:Float = bitmap.width.toFloat() / bitmap.height.toFloat()
            val height:Int = Math.round(width / ratio)

            return Bitmap.createScaledBitmap(bitmap, width, height, false)
        }

        fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, imageUri: Uri?): File {

            val wrapper = ContextWrapper(context)

            var orientedBitmap = bitmap
            if(imageUri != null) {
                 orientedBitmap = modifyOrientation(bitmap, UtilsBaseFile.getSmartFilePath(context, imageUri) ?: "")
            }

            val resizedBitmap = resizeByWidth(orientedBitmap, 480)

            var file = wrapper.getDir("images", Context.MODE_PRIVATE)

            file = File(file, ((Calendar.getInstance().getTimeInMillis()).toString() + ".jpg"))

            try {
                val stream: OutputStream = FileOutputStream(file)
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
            } catch (e: IOException){ // Catch the exception
                e.printStackTrace()
            }

            return file
        }

        fun modifyOrientation(bitmap: Bitmap, image_absolute_path: String): Bitmap {

            if(image_absolute_path.isEmpty()) return bitmap

            val ei = ExifInterface(image_absolute_path)
            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> return rotate(bitmap, 90f)

                ExifInterface.ORIENTATION_ROTATE_180 -> return rotate(bitmap, 180f)

                ExifInterface.ORIENTATION_ROTATE_270 -> return rotate(bitmap, 270f)

                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> return flip(bitmap, true, false)

                ExifInterface.ORIENTATION_FLIP_VERTICAL -> return flip(bitmap, false, true)

                else -> return bitmap
            }
        }

        fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degrees)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
            val matrix = Matrix()
            val hor = if(horizontal) -1 else 1
            val ver = if(vertical) -1 else 1
            matrix.preScale(hor.toFloat(), ver.toFloat())
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }
}