package com.anshdeep.kotlinmessenger.views

import com.anshdeep.kotlinmessenger.R
import com.anshdeep.kotlinmessenger.models.ChatMessage
import com.anshdeep.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

/**
 * Created by ansh on 04/09/18.
 */
class LatestMessageRow(val chatMessage: ChatMessage) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.latest_message_textview.text = chatMessage.text

        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                viewHolder.itemView.username_textview_latest_message.text = user?.username

                Picasso.get().load(user?.profileImageUrl).into(viewHolder.itemView.imageview_latest_message)
            }

        })

    }

}