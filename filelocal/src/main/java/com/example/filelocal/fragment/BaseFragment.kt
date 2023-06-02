package com.example.filelocal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {
    companion object {
        private const val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val outView = inflater.inflate(getLayoutRes(), container, false)
        if (savedInstanceState != null) {
            val isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN)
            val ft = parentFragmentManager.beginTransaction()
            if (isSupportHidden) ft.hide(this)
            else ft.show(this)
            ft.commitAllowingStateLoss()
        }
        return outView
    }

    protected abstract fun getLayoutRes(): Int
}