package com.anshdeep.kotlinmessenger.messages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.anshdeep.kotlinmessenger.R
import com.anshdeep.kotlinmessenger.models.ChatMessage
import com.anshdeep.kotlinmessenger.models.User
import com.anshdeep.kotlinmessenger.utils.DateUtils.getFormattedTimeChatLog
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
        val TAG = "ChatLogActivity"
    }

    val adapter = GroupAdapter<ViewHolder>()


    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = toUser?.name

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
                        adapter.add(ChatFromItem(chatMessage.text, currentUser!!, chatMessage.timestamp))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!, chatMessage.timestamp))
                    }

                }

                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })

    }

    private fun performSendMessage() {
        val text = edittext_chat_log.text.toString()
        if (text.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

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

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }

}


class ChatFromItem(val text: String, val user: User, val timestamp: Long) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.textview_from_row.text = text
        viewHolder.itemView.from_msg_time.text = getFormattedTimeChatLog(timestamp)

        val targetImageView = viewHolder.itemView.imageview_chat_from_row

        if (!user.profileImageUrl!!.isEmpty()) {

            //Todo: Change image loading from picasso to glide (refer latest messages activity))

            Picasso.get().load(user.profileImageUrl).placeholder(R.drawable.no_image2).into(targetImageView)
        }
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }


}


class ChatToItem(val text: String, val user: User, val timestamp: Long) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
        viewHolder.itemView.to_msg_time.text = getFormattedTimeChatLog(timestamp)

        val targetImageView = viewHolder.itemView.imageview_chat_to_row

        if (!user.profileImageUrl!!.isEmpty()) {

          //Todo: Change image loading from picasso to glide (refer latest messages activity)

            Picasso.get().load(user.profileImageUrl).placeholder(R.drawable.no_image2).into(targetImageView)
        }
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}


