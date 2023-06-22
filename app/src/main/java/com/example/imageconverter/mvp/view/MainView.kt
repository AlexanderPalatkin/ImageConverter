package com.example.imageconverter.mvp.view

import com.example.imageconverter.util.ConvertStatus
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface MainView: MvpView {
    fun showConversionResult(status: ConvertStatus)
}