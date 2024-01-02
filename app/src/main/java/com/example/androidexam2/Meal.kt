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
    fun getAverageRating(): Double{

        var sum = 0.0
        ratings?.let { nonNullRatings ->
            for (rating in nonNullRatings){
                sum += rating.value
            }
            return sum/nonNullRatings.size
        }
        return 0.0
    }

}