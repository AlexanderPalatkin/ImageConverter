package com.example.imageconverter.mvp.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

class ImageConverter {
    fun convertJPGToPNG(jpgFilePath: String) {
        val jpgFile = File(jpgFilePath)

        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val inputBitmap = BitmapFactory.decodeFile(jpgFile.absolutePath, options)

        val pngFilePath = "${jpgFile.parent}/${jpgFile.nameWithoutExtension}_temp.png"
        val pngFile = File(pngFilePath)

        FileOutputStream(pngFile).use { out ->
            inputBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
        }

        pngFile.renameTo(jpgFile)
        jpgFile.delete()
    }
}
