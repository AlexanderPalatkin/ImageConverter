package com.example.imageconverter.mvp.presenter

import com.example.imageconverter.mvp.model.ImageConverter
import com.example.imageconverter.mvp.view.MainView
import com.example.imageconverter.util.ConvertStatus
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter

class MainPresenter(private val scheduler: Scheduler) : MvpPresenter<MainView>() {
    private val imageConverter: ImageConverter = ImageConverter()

    fun convertJPGToPNG(jpgFilePath: String) {
        Completable.fromCallable {
            imageConverter.convertJPGToPNG(jpgFilePath)
        }.subscribeOn(Schedulers.io())
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
    }
}
