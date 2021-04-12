package com.unidevteam.cookingapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DialogPicker : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Choose the source")
                    .setNeutralButton("Take a picture",
                            DialogInterface.OnClickListener { dialog, id ->

                            })
                    .setNeutralButton("Choose from library",
                            DialogInterface.OnClickListener { dialog, id ->
                            })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}