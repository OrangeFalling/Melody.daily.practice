package com.example.filelocal.fragment

abstract class TabFragment: BaseFragment() {
    private var callEnterOrExitNext = false

    open fun onEnter(){}
    open fun onExit(){}

    override fun onPause() {
        super.onPause()
        if (callEnterOrExitNext) {
            callEnterOrExitNext = false
            onExit()
        }
    }

    override fun onResume() {
        super.onResume()
        if (callEnterOrExitNext) {
            callEnterOrExitNext = false
            onEnter()
        }
    }

    fun callEnterOrExitNext() {
        callEnterOrExitNext = true
    }
}