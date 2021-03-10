package com.example.weatherapp

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class ScrollChildSwipeRefreshLayout(context: Context, attributeSet: AttributeSet? = null) :
    SwipeRefreshLayout(context, attributeSet) {
    var scrollUpChild: View? = null
    override fun canChildScrollUp(): Boolean {
        return scrollUpChild?.canScrollVertically(-1) ?: super.canChildScrollUp()
    }
}