package com.anshdeep.kotlinmessenger.messages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.anshdeep.kotlinmessenger.R

class ChatLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        supportActionBar?.title = "Chat Log"
    }
}
