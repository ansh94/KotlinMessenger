package com.anshdeep.kotlinmessenger.models

/**
 * Created by ansh on 28/08/18.
 */
class ChatMessage(
        val id: String,
        val text: String,
        val fromId: String,
        val toId: String,
        val timestamp: Long
) {
    constructor() : this("", "", "", "", -1)
}