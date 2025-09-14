package tests.android.ocr.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OcrApplication: Application() {


    companion object {

        private lateinit var instance: OcrApplication

        fun getInstance(): OcrApplication = this.instance

    }


    override fun onCreate() {

        super.onCreate()

        instance = this

    }


}