package com.anshdeep.kotlinmessenger.messages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.anshdeep.kotlinmessenger.R
import com.anshdeep.kotlinmessenger.models.ChatMessage
import com.anshdeep.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {
    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = toUser?.username

        listenForMessages()

        send_button_chat_log.setOnClickListener {
            performSendMessage()
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)


                if (chatMessage != null) {

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessagesActivity.currentUser
                        adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }

                }

            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })

    }

    private fun performSendMessage() {
        val text = edittext_chat_log.text.toString()
//        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()


        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user.uid

        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()


        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved our chat message: ${reference.key}")
                    edittext_chat_log.text.clear()
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                }

        toReference.setValue(chatMessage)
    }


}

class ChatFromItem(val text: String, val user: User) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text

        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        Picasso.get().load(user.profileImageUrl).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}

class ChatToItem(val text: String, val user: User) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text

        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(user.profileImageUrl).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}


