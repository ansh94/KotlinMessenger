package com.anshdeep.kotlinmessenger.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.anshdeep.kotlinmessenger.R
import com.anshdeep.kotlinmessenger.messages.LatestMessagesActivity
import com.anshdeep.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private var selectedPhotoUri: Uri? = null

    companion object {
        val TAG = RegisterActivity::class.java.simpleName!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.elevation = 0.0f


        register_button_register.setOnClickListener {
            performRegistration()
        }

        already_have_account_text_view.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
        }

        selectphoto_button_register.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f

        }
    }

    private fun performRegistration() {
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()
        val name = name_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        already_have_account_text_view.visibility = View.GONE
        loading_view.visibility = View.VISIBLE

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    // else if successful
                    Log.d(TAG, "Successfully created user with uid: ${it.result.user.uid}")
                    uploadImageToFirebaseStorage()
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to create user: ${it.message}")
                    loading_view.visibility = View.GONE
                    already_have_account_text_view.visibility = View.VISIBLE
                    Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
                }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) {
            // save user without photo
            saveUserToFirebaseDatabase(null)
        } else {
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

            ref.putFile(selectedPhotoUri!!)
                    .addOnSuccessListener {
                        Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                        ref.downloadUrl.addOnSuccessListener {
                            Log.d(TAG, "File Location: $it")
                            saveUserToFirebaseDatabase(it.toString())
                        }
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Failed to upload image to storage: ${it.message}")
                        loading_view.visibility = View.GONE
                        already_have_account_text_view.visibility = View.VISIBLE
                    }
        }

    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String?) {
        val uid = FirebaseAuth.getInstance().uid ?: ""

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user: User
        user = if (profileImageUrl == null) {
            User(uid, name_edittext_register.text.toString(), null)
        } else {
            User(uid, name_edittext_register.text.toString(), profileImageUrl)
        }

        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d(TAG, "Finally we saved the user to Firebase Database")


                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to set value to database: ${it.message}")
                    loading_view.visibility = View.GONE
                    already_have_account_text_view.visibility = View.VISIBLE
                }
    }
}

