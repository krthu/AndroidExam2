package com.example.androidexam2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.content.pm.PackageManager
import android.Manifest
import android.content.Intent
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.net.Uri
import android.text.style.BackgroundColorSpan
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext





import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.android.gms.maps.model.Circle
import com.google.firebase.Firebase
import com.google.firebase.annotations.concurrent.Background
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage


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

    @Composable
    fun ComposeContent() {

        val profileUrl = remember {
            mutableStateOf("")
        }
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
                .fillMaxHeight()
                .padding(10.dp)

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = CenterVertically,


            ) {
                profileImage("gs://androidexam2-ea308.appspot.com/images/d3ca4f2e-9134-407f-ada4-86484abccde5")
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
            Barrier()
            Box(
                 modifier = Modifier
                     .weight(1f)
                     .padding(0.dp, 0.dp, 0.dp, 5.dp)
            ){
                addListOfCreatedMeals()
            }

            logOutButton()
        }


    }

    private fun getMeals(callback: (List<Meal>) -> Unit) {
        if (userID != null){
            db.collection("meals").whereEqualTo("creator", userID).get().addOnSuccessListener { data ->
                val listOfMeals = mutableListOf<Meal>()
                if (data != null){
                    for (document in data.documents){
                        val meal = document.toObject<Meal>()
                        if (meal != null){
                            listOfMeals.add(meal)
                        }
                    }
                }
                callback.invoke(listOfMeals)
            }
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
    fun profileImage(firebaseRef: String?) {
        var isImagePermissionGranted by remember { mutableStateOf(ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) }


        val imageModifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .padding(10.dp)
            .border(
                BorderStroke(1.dp, Color.Black),
                CircleShape
            )
            .fillMaxWidth()
            .clickable {

                if (isImagePermissionGranted) {

                    startGallery()
                } else {
                    permissionRequestLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }

            }
        if (firebaseRef != null){
            loadFirebaseImage(firebaseRef, imageModifier)
        }else{
            loadPlaceholderImage(imageModifier)
        }

    }

    @Composable
    private fun loadPlaceholderImage(imageModifier: Modifier) {
        Image(painter = painterResource(id = R.drawable.baseline_question_mark_24),
            contentDescription = stringResource(id = R.string.imageNotSet),
            modifier = imageModifier)

    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun loadFirebaseImage(url: String, imageModifier: Modifier) {
            GlideImage(
                model = url,
                contentDescription = "Profile image",
                modifier = imageModifier
            )
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PERMISSION_REQUESTCODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_REQUESTCODE && resultCode == AppCompatActivity.RESULT_OK && data != null){
            val imageuri = data.data

        }
    }



    @Composable

    fun addListOfCreatedMeals() {
        var mealsState = remember {
            mutableStateListOf<Meal>()
        }
        LaunchedEffect(key1 = true) {
            getMeals { meals ->
                mealsState.addAll(meals)
            }

        }
        LazyColumn(
        ) {
            items(mealsState.toList()) { meal ->
                mealItem(meal)
            }
        }
    }
    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun mealItem(meal: Meal) {
        val downloadedImage = remember {
            mutableStateOf<Uri?>(Uri.parse(meal.imageURI))
        }
        LaunchedEffect(key1 = true){
            downloadedImageFromFireBase(meal.imageURI) { uri ->
                downloadedImage.value = uri
            }
        }

        Row (modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(0.dp, 5.dp)
            .clickable {
                goToDetailsFragment(meal)
            }
            ){
            GlideImage(model = downloadedImage.value,
                contentDescription = "Test",
                modifier = Modifier
                    .size(100.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(5.dp)

            ) {
                meal.name?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_bold)),

                        modifier = Modifier
                          
                    )
                }
                Row {
                    val rating = meal.getAverageRating()
                    var ratingString = stringResource(R.string.no_ratings)
                    if (rating != 0.0){
                        ratingString = String.format("%.1f", rating)
                    }else{
                        ""
                    }
                    Text(
                        text = ratingString,
                        )
                    if (rating != 0.0){
                        Image(
                            painter = painterResource(id = R.drawable.baseline_star_rate_24),
                            contentDescription = "Stars",
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                }
                meal.description?.let {
                    Text(
                        text = it,
                        fontFamily = FontFamily(Font(R.font.montserrat_light)),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis

                    ) }

            }
        }
        Barrier()
    }

    private fun goToDetailsFragment(meal: Meal) {
        val detailsFragment = DetailsFragment()
        val bundle = Bundle()
        bundle.putSerializable("meal", meal)
        detailsFragment.arguments = bundle
        val transaction = parentFragmentManager.beginTransaction()
        transaction.addToBackStack("user")
        transaction.replace(R.id.fragmentContainer, detailsFragment).commit()
    }

    private fun downloadedImageFromFireBase(mealImage: String?, callback: (Uri?) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(mealImage.toString())
        storageRef?.downloadUrl?.addOnSuccessListener {uri ->
            callback.invoke(uri)
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        ComposeContent()
    }
}
@Composable
private fun Barrier(){
    Divider(
        color = Color(R.color.dark_grey),
        thickness = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
    )
}

