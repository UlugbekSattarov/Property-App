package com.example.propertyappg11.util

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(private val space: Int,private val columnNumber: Int = 1,private val endSpace: Int = 0) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            top = space
            left = space

            val pos = (parent.getChildAdapterPosition(view)+1)

            if ( pos % columnNumber == 0 ) {
                right = space
            }

            //This operation in one line gives wrong result...
            val a = parent.adapter?.itemCount ?: 0
            val b = a - columnNumber + 1

            //Last objects are given bottom margin
            if (pos >=  b) {
                bottom = endSpace
            }

        }
    }
}
