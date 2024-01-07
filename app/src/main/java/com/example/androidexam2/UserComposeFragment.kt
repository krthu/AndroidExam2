package com.example.androidexam2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.content.pm.PackageManager
import android.Manifest
import android.content.Intent
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.text.style.BackgroundColorSpan
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
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
import com.google.firebase.annotations.concurrent.Background
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject


class UserComposeFragment : Fragment() {
    private val PERMISSION_REQUESTCODE = 1
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userID = auth.currentUser?.uid
    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startGallery()
            } else {

            }
        }


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

//    @Composable
//    fun checkPermission():Boolean {
//        val context = LocalContext.current
//
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
            logOutButton()
        }


    }

    fun goToListFragment(){
        val listFragment = ListFragment()
        parentFragmentManager.beginTransaction().replace(R.id.fragmentContainer, listFragment).commit()
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
    fun logOutButton() {
        MaterialTheme(

        ){
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    val activity = requireActivity()
                    if (activity is MainActivity){
                        activity.onLogOut()
                    }
                    auth.signOut()
                    goToListFragment()

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(red = 0, green = 32, blue = 63)
                )

            ) {
                Text(text = "Log out")

            }
        }
    }

    @Composable
    fun profileImage() {
        var isImagePermissionGranted by remember { mutableStateOf(ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) }
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

                    if (isImagePermissionGranted){
                        Log.d("!!!", "Have permission")
                        startGallery()
                    }else{
                        permissionRequestLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }

                }
        )
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PERMISSION_REQUESTCODE)
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
