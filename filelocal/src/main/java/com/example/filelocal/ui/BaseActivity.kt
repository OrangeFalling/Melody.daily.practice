package com.example.filelocal.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

abstract class BaseActivity : AppCompatActivity() {
    private var animInEnter: Int? = null
    private var animInExit: Int? = null
    private var animOutEnter: Int? = null
    private var animOutExit: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentRes())
        if (animInExit != null && animInEnter != null) {
            overridePendingTransition(animInEnter?:0, animInExit?:0)
        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.WHITE
    }

    override fun finish() {
        super.finish()
        if (animOutEnter != null && animOutExit != null) {
            overridePendingTransition(animOutEnter?:0, animOutExit?:0)
        }
    }

    protected abstract fun getContentRes(): Int

    protected fun initAnimations(inEnter: Int, inExit: Int, outEnter: Int, outExit: Int) {
        animInEnter = inEnter
        animInExit = inExit
        animOutEnter = outEnter
        animOutExit = outExit
    }
}