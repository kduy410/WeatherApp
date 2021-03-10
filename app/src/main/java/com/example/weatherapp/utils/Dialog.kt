package com.example.weatherapp.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.weatherapp.R

class Dialog(
    private val title: Int,
    private val message: Int,
    private var positiveHandler: DialogInterface.OnClickListener?,
    private var negativeHandler: DialogInterface.OnClickListener?
) : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)

        positiveHandler?.apply {
            builder.setPositiveButton(R.string.ok, this)
        }
        negativeHandler?.apply {
            builder.setNegativeButton(R.string.cancel, this)
        }
        return builder.create()
    }

}