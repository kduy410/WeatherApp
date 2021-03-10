package com.example.weatherapp.extension

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.weatherapp.Event
import com.google.android.material.snackbar.Snackbar

/**
 * Transforms static java function Snackbar.make() to an extension function of View.
 */
fun View.showSnackBar(text: String, timeLength: Int) {
    Snackbar.make(this, text, timeLength).run {
        addCallback(object : Snackbar.Callback() {

        }).show()
    }
}

/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 */
fun View.setupSnackBar(
    lifecycleOwner: LifecycleOwner,
    snackBarEvent: LiveData<Event<Int>>,
    timeLength: Int
) {
    snackBarEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.also {
            showSnackBar(context.getString(it), timeLength)
        }
    })
}

fun hideKeyboardFrom(context: Context?, view: View) {
    val im: InputMethodManager =
        context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    im.hideSoftInputFromWindow(view.windowToken, 0)
    view.clearFocus()
}



