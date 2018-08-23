package com.anshdeep.kotlinmessenger

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"
    }
}
