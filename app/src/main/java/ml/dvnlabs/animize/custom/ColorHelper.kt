/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.custom

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import kotlin.math.floor


class ColorHelper {
    companion object {
        const val FILTER_HORIZONTAL = 28
        const val FILTER_VERTICAL = 29
        const val MASTER_COLORED = 30
        const val MASTER_MONOCHROME = 31
    }


    /**
     * [orientation] Provide with FILTER_VERTICAL or FILTER_HORIZONTAL
     *
     * [image] ImageView Target
     *
     * [percent] Percentage filter on ImageView
     *
     * [master] Master color is background
     */
    fun filteringWithPercentage(orientation: Int = FILTER_VERTICAL, image: ImageView, percent: Float, master: Int = MASTER_MONOCHROME) {
        //get bitmap from your ImageView (image)
        if (percent > 100) return
        val originalBitmap = (image.drawable as BitmapDrawable).bitmap
        var width = image.drawable.intrinsicWidth
        var height = image.drawable.intrinsicHeight
        val grandPercent = if (percent == 0f) 1f else percent
        when (orientation) {
            FILTER_VERTICAL -> {
                width = floor(width * grandPercent / 100f).toInt()
            }
            FILTER_HORIZONTAL -> {
                height = floor(height * grandPercent / 100f).toInt()
            }
        }
        //println("HEIGHT : $height WIDTH: $width")

        var backgroundBitmap: Bitmap? = null
        var foregroundBitmap: Bitmap? = null

        //Check Master Color
        when (master) {
            MASTER_COLORED -> {
                backgroundBitmap = originalBitmap
                foregroundBitmap = monoChrome(originalBitmap)
            }
            MASTER_MONOCHROME -> {
                backgroundBitmap = monoChrome(originalBitmap)
                foregroundBitmap = originalBitmap
            }
        }

        //create a bitmap of provided percentage that we will make black and white
        val croppedBitmap = Bitmap.createBitmap(foregroundBitmap!!, 0, 0, width, height)
        //copy the cropped bmp (cropperBitmap) to the background bmp (backgroundBitmap)
        val processedBitmap = overlay(backgroundBitmap!!, croppedBitmap)
        //set imageView to new bitmap
        image.setImageBitmap(processedBitmap)
    }

    private fun monoChrome(bitmap: Bitmap): Bitmap {
        val bmpMonochrome = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmpMonochrome)
        val ma = ColorMatrix()
        ma.setSaturation(0f)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(ma)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return bmpMonochrome
    }

    private fun overlay(bmp1: Bitmap, bmp2: Bitmap?): Bitmap {
        val bmp3 = bmp1.copy(Bitmap.Config.ARGB_8888, true) //mutable copy
        val canvas = Canvas(bmp3)
        canvas.drawBitmap(bmp2!!, Matrix(), null)
        return bmp3
    }
}