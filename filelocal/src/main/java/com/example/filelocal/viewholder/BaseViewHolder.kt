package com.example.filelocal.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun convertData(data: Any?) {
        if (data != null) {
            bindView(data as T)
        }
    }

    abstract fun bindView(data: T)

    open fun createView(itemView: View) {}
}