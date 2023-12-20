package com.example.androidexam2

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Place (
    @DocumentId var placeId: String? = null,
    var name: String? = null,
    var description: String? = null,
    var creator: String? = null,
    var published: Boolean = false,
    var imageURI: String? = null
): Serializable{
}