package com.ytt.vmv

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.squareup.picasso.Transformation
import java.math.BigInteger
import kotlin.math.min

class CircleTransform : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val size = min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }
        val bitmap = Bitmap.createBitmap(size, size, source.config)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(
            squaredBitmap,
            Shader.TileMode.CLAMP, Shader.TileMode.CLAMP
        )
        paint.shader = shader
        paint.isAntiAlias = true
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        squaredBitmap.recycle()
        return bitmap
    }

    override fun key(): String {
        return "circle"
    }
}

fun showParamDialog(context: Context, paramName: String, param: BigInteger) =
    showParamDialog(context, paramName, param.toString())

fun showParamDialog(context: Context, paramName: String, param: String) {
    AlertDialog.Builder(context)
        .setTitle("Content of $paramName")
        .setMessage(param)
        .setNeutralButton(
            "Copy to Clipboard"
        ) { _, _ ->
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val clip = ClipData.newPlainText(paramName, param)

            clipboard.setPrimaryClip(clip)

            Toast.makeText(
                context,
                "Parameter $paramName copied to clipboard",
                Toast.LENGTH_LONG
            ).show()
        }
        .setPositiveButton(android.R.string.ok, null)
        .show()
}

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}