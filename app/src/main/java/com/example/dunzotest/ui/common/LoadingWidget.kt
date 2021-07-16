package com.example.dunzotest.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.example.dunzotest.R

class LoadingWidget : FrameLayout {
    constructor(context: Context) : super(context) {
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initViews()
    }

    var callback: Callback? = null
    lateinit var textView: TextView
    lateinit var loader: ProgressBar

    fun initViews() {
        View.inflate(context, R.layout.widget_loading, this)
        textView = findViewById(R.id.pagination_error_text)
        loader = findViewById(R.id.pagination_loader)
        textView.setOnClickListener {
            callback?.retryNextPageLoad()
        }
    }


    fun showLoading() {
        this.visibility = View.VISIBLE
        loader.visibility = View.VISIBLE
        textView.text = context.getText(R.string.load_next_page)
    }

    fun hideLoading() {
        this.visibility = View.GONE
    }

    fun setRetryMessage(error: String) {
        this.visibility = View.VISIBLE
        loader.visibility = View.GONE
        textView.text = "$error\n${context.getText(R.string.tap_to_retry)}"
    }

    interface Callback {
        fun retryNextPageLoad()
    }


}