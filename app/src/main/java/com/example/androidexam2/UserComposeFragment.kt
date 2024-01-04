package com.example.androidexam2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.content.pm.PackageManager
import android.Manifest
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.Circle
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject


class UserComposeFragment : Fragment() {
    private val PERMISSION_REQUESTCODE = 1
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userID = auth.currentUser?.uid


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return ComposeView(requireContext()).apply {

            setContent {
                ComposeContent()
            }
        }
    }


//    fun checkPermission():Boolean {
//        if (ContextCompat.checkSelfPermission(ContentProviderCompat.requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED){
//
//        }
//    }


    @Composable
    fun ComposeContent() {
        val currentUser: MutableState<User?> = remember { mutableStateOf(null) }
        var userName = remember { mutableStateOf("") }
        var userMail = remember { mutableStateOf("") }
        LaunchedEffect(true) {
            getUserFromDB { user ->
                currentUser.value = user
                userName.value = user?.userName.toString()
                userMail.value = user?.userId.toString()

            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = CenterVertically

            ) {
                profileImage()
                Text(
                    text = userName.value,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_bold))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .border(
                            BorderStroke(1.dp, Color.Black)
                        )
                        .padding(5.dp)
                )
            }
        }
    }

    private fun getUserFromDB(callback: (User?) -> Unit) {
        if (userID != null) {
            db.collection("users").document(userID).get().addOnSuccessListener { document ->
                val user = document.toObject<User>()
                Log.d("!!!", user?.userName.toString())
                callback.invoke(user)
            }
        }
    }

    @Composable
    fun profileImage() {
        // val imagePermissionState = remember(Manifest.permission.)
        Image(painter = painterResource(id = R.drawable.baseline_question_mark_24),
            contentDescription = stringResource(id = R.string.imageNotSet),
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .padding(10.dp)
                .border(
                    BorderStroke(1.dp, Color.Black),
                    CircleShape
                )
                .fillMaxWidth()
                .clickable {
                    onProfileClick()
                }
        )
    }

    fun onProfileClick() {

        Log.d("!!!", "ProfileClicked")
    }

    @Composable
    fun addListOfCreatedMeals() {
        // val list = getUserItems()
        Column {

        }
    }

//    fun getUserItems(): MutableList<Meal> {
//       // val user =
//        val dbRef = FirebaseFirestore.getInstance()
//
//    }

//private fun checkPermission(): Boolean{
//    return ContextCompat.checkSelfPermission((requireContext(), Manifest.permission.READ_MEDIA_IMAGES), )
//}


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        ComposeContent()
    }
}
