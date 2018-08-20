package com.anshdeep.kotlinmessenger

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by ansh on 18/08/18.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            val email = email_edittext_login.text.toString()
            val password = password_edittext_login.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener {  }
                    .addOnFailureListener {  }
        }

        back_to_register_textview.setOnClickListener {
            finish()
        }
    }
}