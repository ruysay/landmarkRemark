package com.example.landmarkremark.interfaces

/**
 * Listener for RecyclerView
 * Attach to activity and call from adapter
 */
interface RecyclerViewListener {
    /**
     * @param position of item clicked
     */
    fun onRecyclerViewItemClickListener(position: Int) {}

    /**
     * @param arg1 can be anything and null
     * @param arg2 can be anything and null
     * @param arg3 can be anything and null
     */
    fun onRecyclerViewItemClickListener(arg1: Any? = null, arg2: Any? = null, arg3: Any? = null) {}

    fun onSizeChangedListener() {}

}