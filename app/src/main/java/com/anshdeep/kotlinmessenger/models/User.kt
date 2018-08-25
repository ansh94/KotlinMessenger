package com.anshdeep.kotlinmessenger.models

/**
 * Created by ansh on 25/08/18.
 */
data class User(val uid: String, val username: String, val profileImageUrl: String) {
    constructor() : this("", "", "")
}