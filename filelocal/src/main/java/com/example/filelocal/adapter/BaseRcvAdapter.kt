package com.example.filelocal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filelocal.viewholder.BaseViewHolder
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

open class BaseRcvAdapter(private val holderMap: Map<Class<*>, Int>):
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    companion object {
        const val ITEM_TYPE_HEADER = 999
        const val ITEM_TYPE_FOOTER = 1000
    }

    private var onItemClickListener: ((View, Any?)->Unit)? = null

    private var viewLongClickListeners = mutableMapOf<Int, ((Int, View, Any?)->Unit)?>()

    private var viewOnClickListeners = mutableMapOf<Int, ((Int, View, Any?)->Unit)?>()

    private var itemComparator: ((Any?, Any?)->Boolean) = { a, b ->
        false
    }

    private var footerBindCallback: ((View)->Unit)? = null
    private var headerBindCallback: ((View)->Unit)? = null

    private val dataHolderMap = hashMapOf<Type, Class<*>>()
    private val holderViewTypeMap = hashMapOf<Class<*>, Int>()
    private val viewTypeHolderMap = hashMapOf<Int, Class<*>>()
    private val holderResMap = hashMapOf<Class<*>, Int>()

    protected var dataList: List<*>? = null

    private var headerRes = -1
    private var footerRes = -1

    init {
        holderMap.entries.forEachIndexed { index, entry ->
            val holderClass = entry.key
            val dataClass = getDataClass(holderClass)
            if (dataClass == null) throw IllegalArgumentException("must define Data Class Type")
            holderViewTypeMap[holderClass] = index
            dataHolderMap[dataClass] = holderClass
            viewTypeHolderMap[index] = holderClass
            holderResMap[holderClass] = entry.value
        }
    }

    private fun getDataClass(clazz: Class<*>): Type? {
        val parentClazz = clazz.superclass
        if (parentClazz == null) return null
        if (parentClazz == BaseViewHolder::class.java) {
            return (clazz.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        }
        return getDataClass(parentClazz)
    }

    fun getHolderClass(position: Int): Class<*>? {
        val dataPosition = toDataPosition(position)
        val data = dataList?.getOrNull(dataPosition)
        if (data == null) return null
        val result = dataHolderMap[data::class.java]
        return result
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            getHeaderPosition() -> {
                ITEM_TYPE_HEADER
            }
            getFooterPosition() -> {
                ITEM_TYPE_FOOTER
            }
            else -> {
                val holderClass = getHolderClass(position)
                if (holderClass == null) {
                    throw IllegalArgumentException("No match holder found,did you invoke method of 'regist(vararg holders: Class<Holder>)'")
                }
                holderViewTypeMap[holderClass] ?: 0
            }
        }
    }

    private fun getHeaderPosition(): Int {
        return if (headerRes > 0) 0 else -1
    }

    private fun getFooterPosition(): Int {
        if (footerRes < 0) {
            return -1
        }
        val headerAdding = if (headerRes > 0) 1 else 0
        return (dataList?.size?:0) + headerAdding
    }

    private fun toDataPosition(position: Int): Int {
        return if (headerRes > 0) position - 1 else position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        when (viewType) {
            ITEM_TYPE_HEADER -> {
                val itemView = LayoutInflater.from(parent.context).inflate(headerRes, parent, false)
                return object : BaseViewHolder<Any>(itemView) {
                    override fun bindView(data: Any) {}
                }
            }
            ITEM_TYPE_FOOTER -> {
                val itemView = LayoutInflater.from(parent.context).inflate(footerRes, parent, false)
                return object : BaseViewHolder<Any>(itemView) {
                    override fun bindView(data: Any) {}
                }
            }
            else -> {
                var holder: BaseViewHolder<*>? = null
                val holderClazz = viewTypeHolderMap.get(viewType)
                if (holderClazz != null) {
                    val layoutRes = holderResMap[holderClazz]
                    val itemView = LayoutInflater.from(parent.context).inflate(layoutRes!!, parent, false)
                    holder = holderClazz.getDeclaredConstructor(View::class.java)
                        .newInstance(itemView) as BaseViewHolder<*>
                    holder.createView(itemView)
                }
                return holder!!
            }
        }
    }


    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if (position == getHeaderPosition()) {
            headerBindCallback?.invoke(holder.itemView)
            return
        }
        if (position == getFooterPosition()) {
            footerBindCallback?.invoke(holder.itemView)
            return
        }
        val dataPosition = toDataPosition(position)
        val data = dataList?.getOrNull(dataPosition)
        holder.convertData(data)
//        holder.itemView.setFastOnClickListener {
//            onItemClickListener?.invoke(holder.itemView, data)
//        }

        viewLongClickListeners.forEach { entry ->
            if (entry.key > 0) {
                holder.itemView.findViewById(entry.key)
            } else {
                holder.itemView
            }.setOnLongClickListener {
                entry.value?.invoke(entry.key, it, data)
                null != entry.value
            }
        }

        viewOnClickListeners.forEach { entry ->
            val view = if (entry.key > 0 )
                holder.itemView.findViewById<View>(entry.key)
            else
                holder.itemView
//            view?.setFastOnClickListener {
//                entry.value?.invoke(entry.key, it, data)
//            }
        }
    }

    override fun getItemCount(): Int {
        val header = if (headerRes > 0) 1 else 0
        val footer = if (footerRes > 0) 1 else 0
        return (dataList?.size?:0) + header + footer
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView.layoutManager
        if (manager is GridLayoutManager) {
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val itemType = getItemViewType(position)
                    return if (itemType == ITEM_TYPE_FOOTER ||
                        itemType == ITEM_TYPE_HEADER) manager.spanCount else 1
                }
            }
        }
    }

    @Deprecated("Use addOnViewClickListener instead")
    fun setOnItemClickListener(listener: ((View, Any?)->Unit)) {
        onItemClickListener = listener
    }

    fun addOnViewClickListener(viewId: Int = 0, listener: ((Int, View, Any?)->Unit)) {
        viewOnClickListeners[viewId] = listener
    }

    fun addOnViewLongClickListener(viewId: Int = 0, listener: ((Int, View, Any?) -> Unit)) {
        viewLongClickListeners[viewId] = listener
    }

    fun setItemComparator(comparator: ((Any?, Any?)->Boolean)) {
        itemComparator = comparator
    }

    fun setDatas(newList: List<*>) {
        val diff = calculateDiff(newList)
        this.dataList = newList
        diff.dispatchUpdatesTo(this)
    }

    fun setHeaderLayout(res: Int, callback: ((View)->Unit)? = null) {
        headerRes = res
        headerBindCallback = callback
    }

    fun setFooterLayout(res: Int, callback: ((View)->Unit)? = null) {
        footerRes = res
        footerBindCallback = callback
    }

    private fun calculateDiff(newDataList: List<*>): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val newType = newDataList.getOrNull(newItemPosition)
                val oldType = dataList?.getOrNull(oldItemPosition)
                if (newType == null || oldType == null) return false

                return newType::class.java == oldType::class.java
            }

            override fun getOldListSize(): Int {
                return dataList?.size?:0
            }

            override fun getNewListSize(): Int {
                return newDataList.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return itemComparator.invoke(
                    dataList?.getOrNull(oldItemPosition), newDataList.get(newItemPosition))
            }
        })
    }
}