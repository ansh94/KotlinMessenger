package com.anshdeep.kotlinmessenger.registerlogin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.media.ExifInterface
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
import java.io.File
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
//            val exif = ExifInterface(selectedPhotoUri!!.path)
//            val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
//
//            val rotationInDegrees = exifToDegrees(rotation)

            var isCamera = File(selectedPhotoUri!!.path).length() > 0

            Log.d("RegisterActivity","image path : " + selectedPhotoUri!!.path.toString())

//            Picasso.get().load(selectedPhotoUri).rotate(-90.toFloat()).into(selectphoto_imageview_register)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f

        }
    }

    private fun exifToDegrees(exifOrientation: Int): Int {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270
        }
        return 0
    }



    fun modifyOrientation(bitmap: Bitmap, image_absolute_path: String): Bitmap {
        val ei = ExifInterface(image_absolute_path)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90.toFloat())
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180.toFloat())
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270.toFloat())
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipImage(bitmap, true, false)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipImage(bitmap, false, true)
            else -> bitmap
        }
    }

    private fun rotateImage(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun flipImage(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale((if (horizontal) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
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
                    overridePendingTransition(R.anim.enter, R.anim.exit)
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to set value to database: ${it.message}")
                    loading_view.visibility = View.GONE
                    already_have_account_text_view.visibility = View.VISIBLE
                }
    }
}

