package com.onseen.livecare.interfaces

interface RowItemClickListener<T> {
    fun onClickedRowItem(obj: T, position: Int)
}