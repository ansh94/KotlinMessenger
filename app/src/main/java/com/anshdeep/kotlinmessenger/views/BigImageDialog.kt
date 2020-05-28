package com.anshdeep.kotlinmessenger.views

import android.app.DialogFragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.anshdeep.kotlinmessenger.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
        val v = inflater.inflate(R.layout.image_dialog_layout, container, false)
        this.dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        this.dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val circularProgressDrawable = CircularProgressDrawable(this.dialog.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        val requestOptions = RequestOptions().placeholder(circularProgressDrawable)

        Glide.with(this.dialog.context)
                .load(imageUrl)
                .apply(requestOptions)
                .into(v.bigImageView)

        return v
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