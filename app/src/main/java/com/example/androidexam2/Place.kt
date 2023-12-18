package com.example.androidexam2

import com.google.firebase.firestore.DocumentId

data class Place(
    @DocumentId var placeId: String? = null,
    var name: String? = null,
    var description: String? = null,
    var creator: String? = null,
    var published: Boolean = false,
    var imageURI: String? = null
){
}