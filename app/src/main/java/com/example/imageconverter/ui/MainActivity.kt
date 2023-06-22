package com.example.imageconverter.ui

import android.os.Bundle
import android.widget.Toast
import com.example.imageconverter.R
import com.example.imageconverter.databinding.ActivityMainBinding
import com.example.imageconverter.mvp.presenter.MainPresenter
import com.example.imageconverter.mvp.view.MainView
import com.example.imageconverter.util.ConvertStatus
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : MvpAppCompatActivity(), MainView {
    private lateinit var binding: ActivityMainBinding
    private val presenter by moxyPresenter { MainPresenter(AndroidSchedulers.mainThread()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jpgFileName = "cat.jpg"
        convertImage(jpgFileName)
    }

    private fun convertImage(jpgFileName: String) {
        binding.bConvert.setOnClickListener {
            try {
                val jpgFilePath = copyAssetFile(jpgFileName)
                presenter.convertJPGToPNG(jpgFilePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun showConversionResult(status: ConvertStatus) {
        val messageResId = when (status) {
            ConvertStatus.SUCCESS -> R.string.successful_convertation
            ConvertStatus.ERROR -> R.string.error_convertation
        }

        val message = getString(messageResId)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun copyAssetFile(fileName: String): String {
        val targetDir = filesDir
        val targetFile = File(targetDir, fileName)

        if (!targetFile.exists()) {
            assets.open(fileName).use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (inputStream.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                }
            }
        }
        return targetFile.absolutePath
    }
}
