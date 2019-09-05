package es.sdos.samplescustomlogger

import android.app.Application

class CustomApplication : Application() {

    companion object {
        @JvmStatic
        lateinit var instance: CustomApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}