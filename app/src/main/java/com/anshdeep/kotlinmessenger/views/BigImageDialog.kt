package com.anshdeep.kotlinmessenger.views

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.anshdeep.kotlinmessenger.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_dialog_layout.view.*

/**
 * Created by ansh on 11/03/19.
 */
class BigImageDialog : DialogFragment() {
    private var imageUrl = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = arguments!!.getString("url")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.image_dialog_layout, container, false)
        this.dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        Picasso.get().load(imageUrl).into(v.bigImageView)
        return v
    }

    override fun onStart() {
        super.onStart()

//        val dialog = dialog
//        if (dialog != null) {
//            dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        }
    }

    companion object {
        @JvmStatic
        fun newInstance(imageUrl: String) =
                BigImageDialog().apply {
                    arguments = Bundle().apply {
                        putString("url", imageUrl)
                    }
                }
    }
}