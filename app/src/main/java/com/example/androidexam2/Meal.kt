package com.example.androidexam2

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Meal(
    @DocumentId var mealId: String? = null,
    var name: String? = null,
    var description: String? = null,
    var creator: String? = null,
    var published: Boolean = false,
    var imageURI: String? = null,
    var gpsArray: MutableList<Double>? = null,
    var ratings: MutableMap<String?, Double>? = null
) : Serializable {
}