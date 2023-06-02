package com.example.filelocal

import android.app.Application

class LocalApp: Application() {
    companion object {
        private lateinit var mInstance: LocalApp
        fun get(): LocalApp {
            return mInstance
        }
    }

    init {
        mInstance = this
    }
}