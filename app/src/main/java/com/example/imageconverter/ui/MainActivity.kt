package com.example.imageconverter.ui

import android.app.AlertDialog
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

    private lateinit var progressDialog: AlertDialog
    private var conversionCanceled = false

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
                showProgressDialog()
                conversionCanceled = false
                val jpgFilePath = copyAssetFileToFileDir(jpgFileName)
                presenter.convertJPGToPNG(jpgFilePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun showConversionResult(status: ConvertStatus) {
        hideProgressDialog()
        val messageResId = when (status) {
            ConvertStatus.SUCCESS -> R.string.successful_conversion
            ConvertStatus.ERROR -> R.string.error_conversion
            ConvertStatus.CANCELED -> R.string.conversion_canceled
        }

        val message = getString(messageResId)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showProgressDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.conversion_dialog_title)
        builder.setMessage(R.string.conversion_dialog_message)
        builder.setCancelable(false)
        builder.setNegativeButton(R.string.conversion_dialog_cancel) { dialog, _ ->
            presenter.cancelConversion()
            dialog.dismiss()
        }

        progressDialog = builder.create()
        progressDialog.show()
    }

    override fun hideProgressDialog() {
        progressDialog.dismiss()
    }

    override fun onConversionCanceled() {
        conversionCanceled = true
    }

    private fun copyAssetFileToFileDir(fileName: String): String {
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