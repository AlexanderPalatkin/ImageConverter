package com.example.imageconverter.mvp.presenter

import com.example.imageconverter.mvp.model.ImageConverter
import com.example.imageconverter.mvp.view.MainView
import com.example.imageconverter.util.ConvertStatus
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit

class MainPresenter(private val scheduler: Scheduler) : MvpPresenter<MainView>() {
    private val imageConverter: ImageConverter = ImageConverter()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var disposable: Disposable
    private var conversionCanceled = false

    fun convertJPGToPNG(jpgFilePath: String) {
        conversionCanceled = false

        disposable = Completable.fromCallable {
            imageConverter.convertJPGToPNG(jpgFilePath)
        }.delay(3000, TimeUnit.MILLISECONDS) // Задержка в 3000 миллисекунд (3 секунды)
            .takeUntil {
                isConversionCanceled()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(scheduler)
            .subscribe(
                {
                    viewState.showConversionResult(ConvertStatus.SUCCESS)
                },
                { error ->
                    viewState.showConversionResult(ConvertStatus.ERROR)
                    error.printStackTrace()
                }
            )
        compositeDisposable.add(disposable)
    }

    private fun isConversionCanceled(): Boolean {
        return conversionCanceled
    }

    fun cancelConversion() {
        conversionCanceled = true
        viewState.onConversionCanceled()
        compositeDisposable.clear()
        viewState.showConversionResult(ConvertStatus.CANCELED)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.add(disposable)
    }
}